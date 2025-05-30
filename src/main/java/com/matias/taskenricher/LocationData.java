package com.matias.taskenricher; // Asegúrate de que el paquete sea el correcto

public class LocationData {
    private Double temperature; // Ejemplo si usas API de clima
    private String weatherDescription; // Ejemplo
    // Podrías añadir latitud, longitud si las obtienes de una API de geocodificación

    // Constructor
    public LocationData(Double temperature, String weatherDescription) {
        this.temperature = temperature;
        this.weatherDescription = weatherDescription;
    }

    // Getters (y Setters si los necesitaras más adelante)
    public Double getTemperature() {
        return temperature;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    // Puedes añadir Setters si en algún momento necesitas modificar estos datos después de crear el objeto
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }
}