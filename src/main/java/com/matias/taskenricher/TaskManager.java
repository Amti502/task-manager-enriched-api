package com.matias.taskenricher; // O tu paquete

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private ExternalApiService externalApiService;

    public TaskManager(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
        // Añadimos algunas tareas de ejemplo al iniciar
        System.out.println("Inicializando TaskManager y añadiendo tareas de ejemplo...");
        addTask("Comprar leche", "Supermercado Cerca");
        addTask("Pagar recibo de luz", null); // Tarea sin ubicación, no se enriquecerá
        addTask("Reunión con equipo", "Oficina San Isidro");
        System.out.println("TaskManager inicializado con tareas de ejemplo.");
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks); // Devolver una copia para proteger la lista interna
    }

    public Optional<Task> getTaskById(int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst();
    }

    public Task addTask(String description, String locationName) {
        Task newTask = new Task(description, locationName);
        System.out.println("Intentando añadir tarea: '" + description + (locationName != null ? "' en '" + locationName + "'" : "'"));

        if (locationName != null && !locationName.trim().isEmpty()) {
            System.out.println("Intentando enriquecer ubicación: " + locationName);
            Optional<LocationData> locationDataOpt = externalApiService.getWeatherForLocation(locationName);
            if (locationDataOpt.isPresent()) {
                newTask.setEnrichedLocationData(locationDataOpt.get());
                System.out.println("Datos de ubicación enriquecidos para: " + locationName);
            } else {
                System.out.println("No se pudieron obtener datos de ubicación enriquecidos para: " + locationName);
            }
        }
        this.tasks.add(newTask);
        System.out.println("Tarea ID " + newTask.getId() + " añadida a la lista.");
        return newTask;
    }
    // No implementaremos update/delete por tiempo, pero se podrían añadir.
}