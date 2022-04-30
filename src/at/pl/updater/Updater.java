package at.pl.updater;

import at.pl.razer.util.JSON;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Updater implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(Updater.class.getName());

    private final String githubRepository;
    private final Action restart;
    private final Thread thread;
    private final HttpClient http;

    @FunctionalInterface
    public interface Action {
        void execute() throws IOException;
    }

    public Updater(String githubRepository, Action restart) {
        this.githubRepository = githubRepository;
        this.restart = restart;
        thread = new Thread(this::run);
        http = HttpClient.newHttpClient();

        thread.setName("Updater");
        if (System.getProperty("update", "false").equals("true")) {
            thread.start();
        }
    }

    private void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (cleanup() | update()) {
                    restart.execute();
                    Thread.currentThread().interrupt();
                } else {
                    Thread.sleep(1000 * 60 * 60);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                // mimimi nothing to do
                LOGGER.log(Level.SEVERE, "error", e);
            }
        }
    }

    private boolean cleanup() throws IOException {
        LOGGER.info("cleaning");
        if (provideFiles().anyMatch(file -> file.endsWith(".new"))) {
            LOGGER.info("cleaning new files and restoring old files");
            provideFiles().filter(file -> file.endsWith(".new")).map(File::new).forEach(File::delete);
            provideFiles().filter(file -> file.endsWith(".old")).forEach(file -> {
                String other = file.substring(0, file.length() - ".old".length());
                new File(other).delete();
                move(file, other);
            });
            return true;
        } else {
            LOGGER.info("cleaning old files");
            provideFiles().filter(file -> file.endsWith(".old")).map(File::new).forEach(File::delete);
            return false;
        }
    }

    private boolean update() throws IOException, InterruptedException {
        LOGGER.info("updating");
        String json = http.send(HttpRequest.newBuilder().GET().uri(URI.create(githubRepository + "/releases/latest")).build(), HttpResponse.BodyHandlers.ofString()).body();
        String date = JSON.readField(json, "published_at");
        if (!isNewerVersion(date)) {
            LOGGER.info("aborting");
            return false;
        }

        Set<String> oldFiles = provideFiles().collect(Collectors.toSet());

        LOGGER.info("downloading");
        HttpResponse<InputStream> response = http.send(HttpRequest.newBuilder().GET().uri(URI.create(JSON.readField(json, "browser_download_url"))).build(), HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() == 302) {
            response = http.send(HttpRequest.newBuilder().GET().uri(URI.create(response.headers().firstValue("location").get())).build(), HttpResponse.BodyHandlers.ofInputStream());
        }

        Set<String> newFiles = new HashSet<>();
        try (ZipInputStream in = new ZipInputStream(response.body())) {
            byte[] buffer = new byte[1024 * 4];
            for (ZipEntry entry = in.getNextEntry(); entry != null; entry = in.getNextEntry()) {
                String name = "./" + entry.getName();
                LOGGER.info("expanding " + name);
                newFiles.add(name);
                try (OutputStream out = new FileOutputStream(name + ".new")) {
                    for (int n = in.read(buffer); n > 0; n = in.read(buffer)) {
                        out.write(buffer, 0, n);
                    }
                }
            }
        }

        LOGGER.info("backing up old files");
        if (oldFiles.stream().map(file -> move(file, file + ".old")).collect(Collectors.reducing(Boolean.TRUE, (b1, b2) -> b1.booleanValue() && b2.booleanValue()))) {
            LOGGER.info("replacing files");
            if (!newFiles.stream().peek(file -> new File(file).delete()).map(file -> move(file + ".new", file)).collect(Collectors.reducing(Boolean.TRUE, (b1, b2) -> b1.booleanValue() && b2.booleanValue()))) {
                LOGGER.info("replacing failed");
                return false;
            }
        } else {
            LOGGER.info("overwriting files with new files");
            if (!newFiles.stream().map(file -> move(file + ".new", file)).collect(Collectors.reducing(Boolean.TRUE, (b1, b2) -> b1.booleanValue() && b2.booleanValue()))) {
                LOGGER.info("overwriting failed");
                return false;
            }
        }
        LOGGER.info("update complete");
        return true;
    }

    private static Stream<String> provideFiles() throws IOException {
        return Files.walk(Paths.get("."))
                .filter(Files::isRegularFile)
                .map(Object::toString)
                .filter(file -> !file.endsWith(".log"))
                .filter(file -> !file.contains(".log."))
                .filter(file -> !file.endsWith(".lck"))
                .filter(file -> !file.endsWith(VERSION_FILE.substring(VERSION_FILE.lastIndexOf('/') + 1)));
    }

    private static boolean move(String old, String _new) {
        if (new File(old).renameTo(new File(_new))) {
            return true;
        }
        try {
            Files.move(Path.of(old), Path.of(_new), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            // mimimi
        }
        try {
            try (FileOutputStream out = new FileOutputStream(_new)) {
                try (FileInputStream in = new FileInputStream(old)) {
                    byte[] buffer = new byte[1024 * 4];
                    for (int n = in.read(buffer); n > 0; n = in.read(buffer)) {
                        out.write(buffer, 0, n);
                    }
                }
            }
            new File(old).delete();
            return true; // haben fertisch
        } catch (IOException e) {
            // mimimi
        }
        return false;
    }

    private static final String VERSION_FILE = "./.version.txt";

    private boolean isNewerVersion(String newVersion) {
        if (!isNewerVersion(readCurrentVersion(), newVersion)) {
            return false;
        }
        saveCurrentVersion(newVersion);
        return true;
    }

    private boolean isNewerVersion(String current, String other) {
        return !other.equals(current);
    }

    private static void saveCurrentVersion(String version) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(VERSION_FILE))) {
            writer.write(version);
        } catch (IOException e) {
            // mimimi
        }
    }

    private static String readCurrentVersion() {
        try (BufferedReader in = new BufferedReader(new FileReader(VERSION_FILE))) {
            StringBuilder string = new StringBuilder();
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                string.append(line + '\n');
            }
            return string.toString().trim();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            // mimimi
            return null;
        }
    }

    @Override
    public void close() {
        thread.interrupt();
    }
}
