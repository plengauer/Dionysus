package at.pl.dionysus;

import at.pl.dionysus.applications.Application;
import at.pl.dionysus.applications.alert.AlertApplication;
import at.pl.dionysus.applications.alert.providers.CPUUsageAlert;
import at.pl.dionysus.applications.alert.providers.InternetConnectionAlert;
import at.pl.dionysus.applications.date.ImportantDatesApplication;
import at.pl.dionysus.applications.weather.providers.SimpleWeatherAPI;
import at.pl.dionysus.applications.weather.WeatherApplication;
import at.pl.dionysus.applications.weather.providers.WeatherSimulator;
import at.pl.updater.Updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Thread.sleep(1000 * Long.parseLong(System.getProperty("dionysus.delay", String.valueOf(1))));
        Application[] applications = parse(args);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            try (Updater updater = new Updater("https://api.github.com/repos/plengauer/Dionysus", in::close)) {
                for (String line = in.readLine(); line != null; line = in.readLine()) {
                    if (line.equals("q") || line.equals("quit")) {
                        break;
                    }
                }
            }
        } finally {
            for (Application application : applications) {
                application.close();
            }
        }
    }

    private static Application[] parse(String[] args) {
        switch (args[0]) {
            case "default":
                return new Application[] {
                        new AlertApplication(new InternetConnectionAlert(), new CPUUsageAlert()),
                        new WeatherApplication(new SimpleWeatherAPI()),
                        new ImportantDatesApplication()
                };
            case "weather":
                return new Application[] {
                        new WeatherApplication(0, 1000 * 60 * 15, new SimpleWeatherAPI()),
                };
            case "fake-weather":
                return new Application[] {
                        new WeatherApplication(0, 1000 * 60, new WeatherSimulator()),
                };
            default:
                throw new IllegalArgumentException();
        }
    }
}
