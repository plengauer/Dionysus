package at.pl.updater;

import at.pl.razer.util.JSON;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
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
                cleanup();
                if (update()) {
                    restart.execute();
                    Thread.currentThread().interrupt();
                } else {
                    Thread.sleep(1000 * 60 * 60);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                // mimimi nothing to do
                LOGGER.log(Level.SEVERE, "error occured", e);
            }
        }
    }

    private void cleanup() throws IOException {
        LOGGER.info("cleaning");
        if (provideFiles().anyMatch(file -> file.endsWith(".new"))) {
            LOGGER.info("cleaning new files and restoring old files");
            provideFiles().filter(file -> file.endsWith(".new")).map(File::new).forEach(File::delete);
            provideFiles().filter(file -> file.endsWith(".old")).forEach(file -> {
                String other = file.substring(0, file.length() - ".old".length());
                new File(other).delete();
                rename(file, other);
            });
        } else {
            LOGGER.info("cleaning old files");
            provideFiles().filter(file -> file.endsWith(".old")).map(File::new).forEach(File::delete);
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
                        out.write(buffer);
                    }
                }
            }
        }

        LOGGER.info("replacing files");
        if (!oldFiles.stream().map(file -> rename(file, file + ".old")).collect(Collectors.reducing(Boolean.TRUE, (b1, b2) -> b1.booleanValue() && b2.booleanValue()))) {
            LOGGER.warning("backup failed");
            return false;
        }
        if (!newFiles.stream().peek(file -> new File(file).delete()).map(file -> rename(file + ".new", file)).collect(Collectors.reducing(Boolean.TRUE, (b1, b2) -> b1.booleanValue() && b2.booleanValue()))) {
            LOGGER.info("overwrite failed");
            return false;
        }

        LOGGER.info("update complete");
        return true;
    }

    private static Stream<String> provideFiles() throws IOException {
        return Files.walk(Paths.get("."))
                .filter(Files::isRegularFile)
                .filter(file -> !file.endsWith(".log"))
                .map(file -> file.toUri().relativize(new File(".").getAbsoluteFile().toURI()).getPath())
                .map(Object::toString);
    }

    private static boolean rename(String old, String _new) {
        return new File(old).renameTo(new File(_new));
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
