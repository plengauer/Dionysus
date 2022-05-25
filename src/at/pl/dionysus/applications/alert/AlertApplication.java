package at.pl.dionysus.applications.alert;

import at.pl.dionysus.applications.Application;
import at.pl.razer.chroma.Effect;
import at.pl.razer.chroma.EffectPlayer;
import at.pl.razer.chroma.SDK;
import at.pl.razer.chroma.SingletonEffectPlayer;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlertApplication implements Application {

    private static Tracer TRACER = GlobalOpenTelemetry.getTracer("dionysus", "1.0.0");
    private final Logger logger = Logger.getLogger(AlertApplication.class.getName());
    private final Thread thread = new Thread(this::run, "Alert Application Worker");
    private final int samplingPeriod;
    private final AlertProvider[] providers;

    public AlertApplication(AlertProvider... providers) {
        this(1000 * 10, providers);
    }

    public AlertApplication(int samplingPeriod, AlertProvider... providers) {
        this.samplingPeriod = samplingPeriod;
        this.providers = providers;
        thread.start();
    }

    @Override
    public String getName() {
        return "Alerts";
    }

    @Override
    public String getDescription() {
        return "An application that is creating chroma effects to alert of problems";
    }

    private void run() {
        logger.info("running");
        SingletonEffectPlayer player = null;
        AlertLevel prevLevel = AlertLevel.NONE;
        Span span = null;
        while (!thread.isInterrupted()) {
            try {
                if (player != null) {
                    player.join(samplingPeriod);
                    if (!player.isRunning()) {
                        player = null;
                        if (span != null) {
                            span.end();
                            span = null;
                        }
                    }
                } else {
                    Thread.sleep(samplingPeriod);
                }
                if (span == null) {
                    span = TRACER.spanBuilder("Alert").setSpanKind(SpanKind.CONSUMER).startSpan();
                }
                try (Scope __ = span.makeCurrent()) {
                    AlertLevel level = getAlertLevel();
                    span.setAttribute("level", level.name());
                    if (level != prevLevel) {
                        logger.log(Level.INFO, "alert level changed from {0} to {1}", new Object[] { prevLevel, level });
                        if (level == AlertLevel.NONE) {
                            if (player != null) {
                                player.close();
                                player = null;
                            }
                            if (span != null) {
                                span.end();
                                span = null;
                            }
                        } else {
                            Effect effect = null;
                            if (level == AlertLevel.SEVERE) {
                                effect = new Alert(1000, 0xFF0000);
                            } else if (level == AlertLevel.WARNING) {
                                effect = new Alert(1000 * 2, 0xFCBA03);
                            }
                            if (player != null) {
                                player.close();
                            }
                            player = new SingletonEffectPlayer(getName(), getDescription(), effect);
                        }
                    }
                    prevLevel = level;
                }
            } catch (InterruptedException e) {
                thread.interrupt();
            }
        }
        if (player != null) {
            player.close();
        }
        if (span != null) {
            span.end();
        }
        logger.info("stopped");
    }

    private SDK createSDK() throws IOException, InterruptedException {
        return new SDK(getName(), getDescription());
    }

    private AlertLevel getAlertLevel() {
        AlertLevel level = AlertLevel.NONE;
        for (AlertProvider provider : providers) {
            AlertLevel l = provider.getLevel();
            if (l != AlertLevel.NONE) {
                logger.log(Level.INFO, "Alert {0} ({1})", new Object[] { l, provider.getName() });
            }
            level = AlertLevel.from(Math.max(level.level(), l.level()));
        }
        return level;
    }

    @Override
    public void close() throws IOException {
        logger.info("stopping");
        thread.interrupt();
        for (AlertProvider provider : providers) {
            provider.close();
        }
    }
}
