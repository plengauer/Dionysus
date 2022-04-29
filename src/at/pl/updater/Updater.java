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
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Updater implements AutoCloseable {

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
                // mimimi nothing to do, maybe log
            }
        }
    }

    private void cleanup() throws IOException {
        provideFiles().filter(file -> file.endsWith(".old")).map(File::new).forEach(File::delete);
    }

    private boolean update() throws IOException, InterruptedException {
        String json = http.send(HttpRequest.newBuilder().GET().uri(URI.create(githubRepository + "/releases/latest")).build(), HttpResponse.BodyHandlers.ofString()).body();
        String date = JSON.readField(json, "published_at");
        if (!isNewerVersion(date)) {
            return false;
        }

        Set<String> oldFiles = provideFiles().collect(Collectors.toSet());

        HttpResponse<InputStream> response = http.send(HttpRequest.newBuilder().GET().uri(URI.create(JSON.readField(json, "browser_download_url"))).build(), HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() == 302) {
            response = http.send(HttpRequest.newBuilder().GET().uri(URI.create(response.headers().firstValue("location").get())).build(), HttpResponse.BodyHandlers.ofInputStream());
        }

        Set<String> newFiles = new HashSet<>();
        try (ZipInputStream in = new ZipInputStream(response.body())) {
            byte[] buffer = new byte[1024 * 4];
            for (ZipEntry entry = in.getNextEntry(); entry != null; entry = in.getNextEntry()) {
                String name = "./" + entry.getName();
                newFiles.add(name);
                try (OutputStream out = new FileOutputStream(name + ".new")) {
                    for (int n = in.read(buffer); n > 0; n = in.read(buffer)) {
                        out.write(buffer);
                    }
                }
            }
        }

        oldFiles.stream().forEach(file -> new File(file).renameTo(new File(file + ".old")));
        newFiles.stream().forEach(file -> new File(file + ".new").renameTo(new File(file)));

        return true;
    }

    private static Stream<String> provideFiles() throws IOException {
        return Files.walk(Paths.get("."))
                .filter(Files::isRegularFile)
                .map(file -> file.toUri().relativize(new File(".").getAbsoluteFile().toURI()).getPath())
                .map(Object::toString);
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
