package at.pl.dionysus.applications.weather.providers;

import at.pl.dionysus.applications.weather.WeatherProvider;

import java.io.IOException;

public class WeatherSimulator implements WeatherProvider {

    private static final String[] WEATHER = {
            "Clear",
            "Sunny",
            "Light rain",
            "Light rain shower",
            "Patchy light rain",
            "Light freezing rain",
            "Light drizzle",
            "Light sleet",
            "Light sleet showers",
            "Moderate rain",
            "Moderate rain at times",
            "Drizzle",
            "Freezing Drizzle",
            "Torrential rain shower",
            "Heavy rain",
            "Heavy rain at times",
            "Heavy Freezing Drizzle",
            "Moderate or heavy rain shower",
            "Moderate or heavy freezing rain",
            "Moderate or heavy sleet",
            "Moderate or heavy sleet showers",
            "Light snow",
            "Patchy light snow",
            "Light snow showers",
            "Moderate snow",
            "Patchy moderate snow",
            "Heavy snow",
            "Patchy heavy snow",
            "Moderate or heavy snow shower",
            "Partly cloudy",
            "Cloudy",
            "Heavy clouds",
            "Overcast",
            "Fog",
            "Freezing fog",
            "Mist",
            "Patchy light rain with thunder",
            "Moderate or heavy rain with thunder",
            "Patchy light snow with thunder",
            "Moderate or heavy snow with thunder",
            "Blizzard",
    };

    private final String[] weather;

    public WeatherSimulator() {
        this(WEATHER);
    }

    public WeatherSimulator(String... weather) {
        this.weather = weather;
    }

    @Override
    public String get() throws IOException, InterruptedException {
        return weather[(int) (weather.length * Math.random())];
    }
}
