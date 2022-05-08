package at.pl.dionysus.applications.weather;

import at.pl.razer.chroma.Effect;
import at.pl.dionysus.applications.Application;
import at.pl.razer.chroma.SingletonEffectPlayer;
import at.pl.razer.chroma.effects.StarlightEffect;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherApplication implements Application {

    private static Tracer TRACER = GlobalOpenTelemetry.getTracer("dionysus", "1.0.0");
    private final Logger logger = Logger.getLogger(WeatherApplication.class.getName());
    private final Thread thread = new Thread(this::run, "Date Application Worker");
    private final WeatherProvider weather;
    private final long samplingPeriod;
    private final long effectDuration;

    public WeatherApplication(WeatherProvider weather) {
        this(1000 * 60, 1000 * 60, weather);
    }

    public WeatherApplication(long samplingPeriod, long effectDuration, WeatherProvider weather) {
        this.samplingPeriod = samplingPeriod;
        this.effectDuration = effectDuration;
        this.weather = weather;
        thread.start();
    }

    @Override
    public String getName() { return "Weather"; }

    @Override
    public String getDescription() {
        return "An application that is creating chroma effects based on the current weather";
    }

    private void run() {
        logger.info("running");
        String weather = "Unknown";
        while (!thread.isInterrupted()) {
            try {
                Thread.sleep(Math.max(1, samplingPeriod));
                Span span = TRACER.spanBuilder("Weather").setSpanKind(SpanKind.CONSUMER).startSpan();
                try (Scope __ = span.makeCurrent()) {
                    String currentWeather = this.weather.get();
                    span.setAttribute("weather", currentWeather);
                    boolean dirty = currentWeather != null && (weather == null || !currentWeather.equals(weather));
                    span.setAttribute("changed", dirty);
                    if (dirty) {
                        logger.log(Level.INFO, "weather changed from \"{0}\" to \"{1}\"", new Object[] { weather, currentWeather });
                        Effect effect = getEffect(currentWeather);
                        if (effect != null) {
                            try (SingletonEffectPlayer player = new SingletonEffectPlayer(getName(), getDescription(), effect)) {
                                player.join(effectDuration);
                            }
                        }
                    }
                    weather = currentWeather;
                } finally {
                    span.end();
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, "", e);
            } catch (InterruptedException e) {
                thread.interrupt();
            }
        }
        logger.info("stopped");
    }

    private Effect getEffect(String weather) {
        // https://www.weatherapi.com/docs/weather_conditions.csv
        switch (weather) {
            case "Clear": return new StarlightEffect(0.1, 1000 * 10, 0xFFC81F);
            case "Sunny": return new Sunny();
            case "Light rain":
            case "Light rain shower":
            case "Patchy light rain":
            case "Light freezing rain":
            case "Light drizzle":
            case "Light sleet":
            case "Light sleet showers":
                return new Rain(0.05);
            case "Moderate rain":
            case "Moderate rain at times":
            case "Drizzle":
            case "Freezing Drizzle":
            case "Torrential rain shower":
                return new Rain(0.1);
            case "Heavy rain":
            case "Heavy rain at times":
            case "Heavy Freezing Drizzle":
            case "Moderate or heavy rain shower":
            case "Moderate or heavy freezing rain":
            case "Moderate or heavy sleet":
            case "Moderate or heavy sleet showers":
                return new Rain(0.3);
            case "Light snow":
            case "Patchy light snow":
            case "Light snow showers":
                return new Snow(0.05);
            case "Moderate snow":
            case "Patchy moderate snow":
                return new Snow(0.1);
            case "Heavy snow":
            case "Patchy heavy snow":
            case "Moderate or heavy snow showers":
                return new Snow(0.3);
            case "Partly cloudy": return new Clouds(0.2, 2, 4);
            case "Cloudy": return new Clouds(0.5, 3, 6);
            case "Heavy clouds": return new Clouds(0.9, 4, 8);
            case "Overcast": return new Clouds.Overcast();
            case "Fog":
            case "Freezing fog":
                return new Clouds.Fog();
            case "Mist": return new Clouds.Mist();
            case "Patchy light rain with thunder": return new Thunder(1000 * 3, getEffect("Light rain"));
            case "Moderate or heavy rain with thunder": return new Thunder(1000 * 3, getEffect("Heavy rain"));
            case "Patchy light snow with thunder": return new Thunder(1000 * 3, getEffect("Light snow"));
            case "Moderate or heavy snow with thunder": return new Thunder(1000 * 3, getEffect("Heavy snow"));
            case "Blizzard": return new Blizzard();
            default: return null;
        }
    }

    @Override
    public void close() throws IOException {
        logger.info("stopping");
        thread.interrupt();
    }
}
