package at.pl.razer.chroma;

import at.pl.razer.util.JSON;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SDK implements Closeable {

    private static final String BASE_URI = "http://localhost:54235/razer/chromasdk";
    private static Tracer TRACER = GlobalOpenTelemetry.getTracer("razer.chroma.manual", "1.0.0");

    private final Logger logger;
    private final HttpClient http = HttpClient.newBuilder().build();
    private final String uri;

    private final Thread thread;

    public SDK(String name, String description) throws IOException, InterruptedException {
        this(name, description, 1000 * 1);
    }

    public SDK(String name, String description, long delay) throws IOException, InterruptedException {
        logger = Logger.getLogger(SDK.class.getName() + " " + name);
        logger.info("initializing");
        String application =
                "{\n" +
                "   \"title\": \"" + name + "\",\n" +
                "   \"description\": \"" + description + "\",\n" +
                "   \"author\": { \"name\": \"Philipp Lengauer\", \"contact\": \"p.lengauer@gmail.com\" },\n" +
                "   \"device_supported\": [ \"keyboard\", \"mouse\", \"mousepad\" ],\n" +
                "   \"category\": \"application\"\n" +
                "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URI))
                .POST(HttpRequest.BodyPublishers.ofString(application))
                .header("Content-Type", "application/json")
                .build();
        uri = JSON.readField(http.send(request, HttpResponse.BodyHandlers.ofString()).body(), "uri");
        thread = new Thread(this::heartbeats, "SDK Heartbeat");
        thread.setDaemon(true);

        logger.fine("waiting until heartbeat works");
        long time = System.currentTimeMillis();
        long retries = 0;
        for (;;) {
            try {
                heartbeat();
                break;
            } catch (IOException e) {
                if (System.currentTimeMillis() - time > 1000 * 60) {
                    throw new IOException("Razer SDK takes too long to respond well to heartbeats", e);
                }
                retries++;
                Thread.sleep(10);
                continue;
            }
        }
        logger.log(Level.FINE, "server ready ({0} retries, {1}ms)", new Object[] { retries, System.currentTimeMillis() - time });
        thread.start();

        if (delay > 0) {
            logger.fine("waiting " + delay + "ms");
            Thread.sleep(delay); // give razer some time
        }

        logger.info("initialized");
    }

    @Override
    public void close() throws IOException {
        thread.interrupt();
        try {
            send(request(null).DELETE().build(), null);
        } catch (InterruptedException e) {
            throw new InterruptedIOException();
        }
        logger.info("shut down");
    }

    private void heartbeats() {
        Thread self = Thread.currentThread();
        while(!self.isInterrupted()) {
            try {
                Thread.sleep(1000 * 1);
                heartbeat();
            } catch (IOException e) {
                logger.log(Level.WARNING, "", e);
                try {
                    close();
                } catch (IOException ex) {
                    // mimimi
                }
            } catch (InterruptedException e) {
                self.interrupt();
            }
        }
    }

    private void heartbeat() throws IOException, InterruptedException {
        logger.fine("heartbeat");
        send(request("heartbeat").PUT(HttpRequest.BodyPublishers.ofString("")).build(), null);
    }

    public String createEffect(Device device, String effect) throws IOException, InterruptedException {
        logger.log(Level.FINE, "creating effect {0} on {1}", new Object[] { effect, device });
        return send(request(device.type()).POST(HttpRequest.BodyPublishers.ofString(effect)).build(), "id");
    }

    public void deleteEffect(String effectID) throws IOException, InterruptedException {
        logger.log(Level.FINE, "deleting effect {0}", new Object[] { effectID });
        send(request("effect").method("DELETE", HttpRequest.BodyPublishers.ofString("{\"id\":\"" + effectID + "\"}")).build(), null);
    }

    public void activateEffect(String effectID) throws IOException, InterruptedException {
        logger.log(Level.FINE, "activating effect {0}", new Object[] { effectID });
        send(request("effect").PUT(HttpRequest.BodyPublishers.ofString("{\"id\":\"" + effectID + "\"}")).build(), null);
    }

    private String send(HttpRequest request, String key) throws IOException, InterruptedException {
        Span span = TRACER.spanBuilder("Razer Chroma SDK").startSpan();
        try(Scope scope = span.makeCurrent()) {
            logger.log(Level.FINEST, "HTTP {0} {1}", new Object[]{request.method(), request.uri()});
            String response = http.send(request, HttpResponse.BodyHandlers.ofString()).body();
            logger.log(Level.FINER, "HTTP {0} {1} -> {2}", new Object[]{request.method(), request.uri(), response});
            String error = JSON.readField(response, "result");
            if (error != null && Integer.parseInt(error) != 0) {
                throw new IOException("Razer Chroma SDK Error " + error);
            }
            if (key != null) {
                response = JSON.readField(response, key);
            }
            return response;
        } catch (IOException | InterruptedException | RuntimeException e) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(e);
            throw e;
        } finally {
            span.end();
        }
    }

    private HttpRequest.Builder request(String endpoint) {
        return HttpRequest.newBuilder().uri(URI.create(uri + (endpoint != null ? "/" + endpoint : ""))).header("Content-Type", "application/json");
    }
}
