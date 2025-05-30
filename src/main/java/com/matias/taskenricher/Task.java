package com.matias.taskenricher;

public class Task {
    private static int nextId = 1;
    private int id;
    private String description;
    private boolean completed;
    private String locationName; // Ej: "Supermercado Plaza Vea Salaverry", "Oficina San Isidro"
    private LocationData enrichedLocationData; // Datos de la API externa

    public Task(String description, String locationName) {
        this.id = nextId++;
        this.description = description;
        this.locationName = locationName; // Puede ser null o vacío si no hay ubicación
        this.completed = false;
    }

    // Getters
    public int getId() { return id; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }
    public String getLocationName() { return locationName; }
    public LocationData getEnrichedLocationData() { return enrichedLocationData; }

    // Setters
    public void setDescription(String description) { this.description = description; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public void setEnrichedLocationData(LocationData enrichedLocationData) {
        this.enrichedLocationData = enrichedLocationData;
    }
}