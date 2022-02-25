package at.pl.dionysus.applications.weather;

import java.io.IOException;

public interface WeatherProvider {
    public abstract String get() throws IOException, InterruptedException;
}
