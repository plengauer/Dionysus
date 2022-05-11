package at.pl.dionysus;

import at.pl.dionysus.applications.Application;
import at.pl.dionysus.applications.alert.AlertApplication;
import at.pl.dionysus.applications.alert.providers.CPUUsageAlert;
import at.pl.dionysus.applications.alert.providers.InternetConnectionAlert;
import at.pl.dionysus.applications.date.ImportantDatesApplication;
import at.pl.dionysus.applications.weather.providers.SimpleWeatherAPI;
import at.pl.dionysus.applications.weather.WeatherApplication;
import at.pl.dionysus.applications.weather.providers.WeatherSimulator;
import at.pl.razer.chroma.EffectPlayer;
import at.pl.razer.chroma.SDK;
import at.pl.razer.chroma.effects.Flashing;
import at.pl.updater.Updater;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Thread.sleep(1000 * Long.parseLong(System.getProperty("dionysus.delay", String.valueOf(1))));

        for (;;) {
            try {
                try (SDK sdk = new SDK("Intro", "intro")) {
                    try (EffectPlayer player = new EffectPlayer(sdk, new Flashing(1000, 0xFF0000, 0xFF8800, 0x00FF00))) {
                        player.join(1000 * 10);
                    }
                }
                break;
            } catch (IOException ioe) {
                Thread.sleep(1000);
                continue;
            }
        }

        Application[] applications = parse(args);
        boolean[] running = { true };
        try (Updater updater = new Updater("https://api.github.com/repos/plengauer/Dionysus", () -> {
            synchronized (running) {
                running[0] = false;
                running.notifyAll();
            }
        }, () -> {
            //TODO find exec path
            try {
                return Runtime.getRuntime().exec(new String[] { "./jre/bin/java.exe", "-version" }).waitFor() == 0;
            } catch (IOException | InterruptedException e) {
                return false;
            }
        })) {
            synchronized (running) {
                while (running[0]) running.wait();
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
            case "none":
                return new Application[0];
            default:
                throw new IllegalArgumentException();
        }
    }
}
