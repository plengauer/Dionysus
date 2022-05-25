package at.pl.razer.chroma;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingletonEffectPlayer implements java.io.Closeable {

    private static final Object MONITOR = new Object();
    private static final Stack<SingletonEffectPlayer> PLAYERS = new Stack<>();
    private static boolean RUNNING = false;
    private static Tracer TRACER = GlobalOpenTelemetry.getTracer("razer.chroma.manual", "1.0.0");

    private final Logger logger = Logger.getLogger(SingletonEffectPlayer.class.getName());
    private final String title, description;
    private final Effect effect;
    private final Thread thread;

    public SingletonEffectPlayer(String title, String description, Effect effect) {
        Span current = Span.current();
        this.title = title;
        this.description = description;
        this.effect = effect;
        this.thread = new Thread(() -> run(current));

        thread.start();
    }

    private void run(Span parent) {
        logger.info("running");
        synchronized (MONITOR) {
            PLAYERS.push(this);
            MONITOR.notifyAll();
        }
        while (!Thread.currentThread().isInterrupted()) {
            try (Scope ___ = parent.makeCurrent()) {
                Span span = TRACER.spanBuilder("Razer Chroma Singleton Effect Player").startSpan();
                span.setAttribute("title", title);
                span.setAttribute("description", description);
                try (Scope __ = span.makeCurrent()) {
                    synchronized (MONITOR) {
                        if (PLAYERS.peek() == this) {
                            try {
                                while (RUNNING) {
                                    MONITOR.wait();
                                }
                                RUNNING = true;
                                try (SDK sdk = new SDK(title, description)) {
                                    try (EffectPlayer player = new EffectPlayer(sdk, effect)) {
                                        // player.join();
                                        while (player.isRunning() && PLAYERS.peek() == this) {
                                            MONITOR.wait(1000 * 1);
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                logger.log(Level.SEVERE, "", e);
                            } finally {
                                RUNNING = false;
                                MONITOR.notifyAll();
                            }
                        } else {
                            try {
                                MONITOR.wait();
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    span.end();
                }
            }
        }
        synchronized (MONITOR) {
            PLAYERS.remove(this);
            MONITOR.notifyAll();
        }
        logger.info("stopped");
    }

    public boolean isRunning() {
        return thread.isAlive();
    }

    public void close() {
        logger.info("stopping");
        this.thread.interrupt();
        try {
            this.thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void join(long time) throws InterruptedException {
        thread.join(time);
    }

}
