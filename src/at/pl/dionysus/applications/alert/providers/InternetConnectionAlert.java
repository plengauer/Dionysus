package at.pl.dionysus.applications.alert.providers;

import at.pl.dionysus.applications.alert.AlertLevel;
import at.pl.dionysus.applications.alert.AlertProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.http.HttpClient;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InternetConnectionAlert implements AlertProvider {

    private final Logger logger = Logger.getLogger(InternetConnectionAlert.class.getName());
    private final HttpClient http = HttpClient.newHttpClient();
    private final Thread thread = new Thread(this::run, InternetConnectionAlert.class.getSimpleName() + " Worker");

    private final long maxTime;
    private AlertLevel level;

    public InternetConnectionAlert() {
        this(100);
    }

    public InternetConnectionAlert(long maxTime) {
        this.maxTime = maxTime;
        this.level = AlertLevel.NONE;
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public String getName() {
        return "Internet Connection";
    }

    @Override
    public AlertLevel getLevel() {
        return level;
    }

    private void run() {
        logger.info("running");
        while (!thread.isInterrupted()) {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec("ping -t www.google.com");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    for (String line = in.readLine(); line != null; line = in.readLine()) {
                        // "Reply from 142.251.37.4: bytes=32 time=16ms TTL=118"
                        logger.log(Level.FINE, line);
                        int to = line.indexOf("ms");
                        if (to < 0) {
                            level = AlertLevel.SEVERE;
                            continue;
                        }
                        int from = to;
                        while (from >= 0 && line.charAt(from) != '=') from--;
                        from++;
                        try {
                            long time = Long.parseLong(line.substring(from, to));
                            level = time < maxTime ? AlertLevel.NONE : AlertLevel.WARNING;
                        } catch (NumberFormatException nfe) {
                            level = AlertLevel.SEVERE;
                        }
                    }
                }
            } catch (InterruptedIOException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                logger.log(Level.WARNING, "", e);
            } finally {
                process.destroyForcibly();
            }
        }
        logger.info("stopped");
    }

    @Override
    public void close() {
        logger.info("closing");
        thread.interrupt();
    }
}
