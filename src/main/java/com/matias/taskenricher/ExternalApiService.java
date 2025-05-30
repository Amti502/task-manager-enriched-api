package com.matias.taskenricher; // O tu paquete

import com.google.gson.Gson;
import com.google.gson.JsonArray; // Necesario para leer arrays JSON
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Optional;

public class ExternalApiService {
    private HttpClient httpClient;
    private Gson gson;

    public ExternalApiService() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    public Optional<LocationData> getWeatherForLocation(String locationName) {
        double lat, lon;

        // Mapeo de ubicaciones a coordenadas
        if ("Oficina San Isidro".equalsIgnoreCase(locationName)) {
            lat = -12.0884; lon = -77.0503;
        } else if ("Supermercado Salaverry".equalsIgnoreCase(locationName)) {
            lat = -12.0895; lon = -77.0544;
        } else if ("Supermercado Cerca".equalsIgnoreCase(locationName)) {
            lat = -12.1024; lon = -77.0294; // Ejemplo: Miraflores, Lima
        } else {
            System.out.println("Ubicación no mapeada para API de clima: " + locationName);
            return Optional.empty();
        }

        // Pide la temperatura horaria (temperature_2m) y especifica la zona horaria (auto)
        String apiUrl = String.format(Locale.US,
                "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&hourly=temperature_2m&timezone=auto",
                lat, lon
        );
        System.out.println("URL API Clima (Horaria) construida para [" + locationName + "]: " + apiUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("User-Agent", "JavaHttpClientMatias") // Añadir un User-Agent simple
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Respuesta de API externa para [" + locationName + "]: Status " + response.statusCode());

            if (response.statusCode() == 200) {
                JsonObject weatherJson = gson.fromJson(response.body(), JsonObject.class);

                if (weatherJson.has("hourly")) {
                    JsonObject hourlyData = weatherJson.getAsJsonObject("hourly");
                    if (hourlyData.has("temperature_2m") && hourlyData.getAsJsonArray("temperature_2m").size() > 0) {
                        JsonArray temperatures = hourlyData.getAsJsonArray("temperature_2m");
                        // Tomamos la primera temperatura del array horario como simplificación
                        double temperature = temperatures.get(0).getAsDouble();
                        return Optional.of(new LocationData(temperature, "Temperatura Horaria (Open-Meteo)"));
                    } else {
                        System.err.println("Array de 'temperature_2m' no encontrado o vacío en 'hourly' para [" + locationName + "]. Body: " + response.body());
                    }
                } else {
                    System.err.println("Respuesta JSON de Open-Meteo no contiene 'hourly' para [" + locationName + "]. Body: " + response.body());
                }
            } else {
                System.err.println("Error de API externa para [" + locationName + "]: " + response.statusCode() + " - Body: " + response.body());
            }
        } catch (Exception e) {
            System.err.println("Excepción al llamar a API externa para [" + locationName + "]: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }
}