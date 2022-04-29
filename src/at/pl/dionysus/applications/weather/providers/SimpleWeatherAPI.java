package at.pl.dionysus.applications.weather.providers;

import at.pl.dionysus.applications.weather.WeatherProvider;
import at.pl.razer.util.JSON;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleWeatherAPI implements WeatherProvider {

    private static final String API_KEY = "975ee030e9f74afc8f4213120220402";

    private final Logger logger = Logger.getLogger(SimpleWeatherAPI.class.getName());
    private final HttpClient http = HttpClient.newHttpClient();

    @Override
    public String get() throws IOException, InterruptedException {
        logger.info("checking weather");
        String location = getLocation();
        HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create("http://api.weatherapi.com/v1/current.json?key=" + API_KEY + "&q=" + location + "&aqi=yes")).build();
        String json = http.send(request, HttpResponse.BodyHandlers.ofString()).body();
        String weather = JSON.readField(json, "text");
        logger.log(Level.INFO, "weather \"{0}\"", weather);
        return weather;
    }

    private String getLocation() throws IOException, InterruptedException {
        logger.info("checking location");
        HttpRequest request = HttpRequest.newBuilder() .GET() .uri(URI.create("http://ip-api.com/json/")) .build();
        String json = http.send(request, HttpResponse.BodyHandlers.ofString()).body();
        String city = JSON.readField(json, "city");
        logger.log(Level.INFO, "city \"{0}\"", city);
        return city;
    }
}
