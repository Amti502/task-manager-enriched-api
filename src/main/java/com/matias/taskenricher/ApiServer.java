package com.matias.taskenricher; // O tu paquete

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

public class ApiServer {
    private TaskManager taskManager;
    private Gson gson = new Gson();

    public ApiServer(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/tasks", new TasksHandler()); // Ruta principal para tareas
        // Si quieres un handler específico para /api/tasks/{id}, necesitarías otro createContext
        // o una lógica de ruteo más compleja en TasksHandler (como el path.matches).
        server.setExecutor(null);
        server.start();
        System.out.println("Servidor API de Tareas Enriquecidas iniciado en http://localhost:" + port);
    }

    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath(); // ej: "/api/tasks" o "/api/tasks/1"
            String responseBody = "";
            int statusCode = 200;

            // Cabeceras CORS y Content-Type (mejor ponerlas una vez antes de enviar)
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*"); // Para pruebas locales

            try {
                if (path.equals("/api/tasks")) { // Manejar "/api/tasks"
                    if ("GET".equals(method)) {
                        List<Task> tasks = taskManager.getAllTasks();
                        responseBody = gson.toJson(tasks);
                    } else if ("POST".equals(method)) {
                        InputStream requestBodyStream = exchange.getRequestBody();
                        String requestJson = new String(requestBodyStream.readAllBytes(), StandardCharsets.UTF_8);
                        try {
                            JsonObject taskDataJson = gson.fromJson(requestJson, JsonObject.class);
                            String description = taskDataJson.has("description") ? taskDataJson.get("description").getAsString() : null;
                            String locationName = taskDataJson.has("locationName") ? taskDataJson.get("locationName").getAsString() : null;

                            if (description == null || description.trim().isEmpty()) {
                                statusCode = 400;
                                responseBody = "{\"error\":\"La descripcion de la tarea es requerida\"}";
                            } else {
                                Task addedTask = taskManager.addTask(description, locationName);
                                responseBody = gson.toJson(addedTask);
                                statusCode = 201;
                            }
                        } catch (JsonSyntaxException | IllegalStateException e) {
                            statusCode = 400;
                            responseBody = "{\"error\":\"JSON malformado o datos invalidos: " + e.getMessage() + "\"}";
                        }
                    } else {
                        statusCode = 405; // Method Not Allowed
                        responseBody = "{\"error\":\"Metodo no permitido para la ruta /api/tasks\"}";
                    }
                } else if (path.matches("/api/tasks/\\d+")) { // Manejar "/api/tasks/{id}"
                    if ("GET".equals(method)) {
                        try {
                            // Extraer el ID de la ruta
                            String idStr = path.substring(path.lastIndexOf('/') + 1);
                            int id = Integer.parseInt(idStr);
                            Optional<Task> taskOpt = taskManager.getTaskById(id);
                            if (taskOpt.isPresent()) {
                                responseBody = gson.toJson(taskOpt.get());
                            } else {
                                statusCode = 404; // Not Found
                                responseBody = "{\"error\":\"Tarea con ID " + id + " no encontrada\"}";
                            }
                        } catch (NumberFormatException e) {
                            statusCode = 400; // Bad Request
                            responseBody = "{\"error\":\"ID de tarea invalido en la ruta\"}";
                        }
                    } else {
                        statusCode = 405; // Method Not Allowed
                        responseBody = "{\"error\":\"Metodo no permitido para la ruta /api/tasks/{id}\"}";
                    }
                } else {
                    statusCode = 404; // Not Found
                    responseBody = "{\"error\":\"Ruta no encontrada: " + path + "\"}";
                }
            } catch (Exception e) {
                statusCode = 500; // Internal Server Error
                responseBody = "{\"error\":\"Error interno del servidor procesando la peticion\"}";
                System.err.println("Error en el handler: " + e.getMessage());
                e.printStackTrace();
            }

            // Asegurarse de que responseBody no sea null
            if (responseBody == null || responseBody.isEmpty()) {
                // Esto puede pasar si una ruta válida no generó un cuerpo de respuesta explícito
                // o si hubo un error antes de asignar responseBody.
                if (statusCode == 200 || statusCode == 201) { // Si se esperaba éxito pero no hay cuerpo
                    responseBody = "{}"; // Enviar un JSON vacío si es un GET exitoso sin datos
                } else if (responseBody.isEmpty()){ // Si es un error y el cuerpo está vacío
                    responseBody = "{\"error\":\"Respuesta vacia para el codigo de estado " + statusCode + "\"}";
                }
            }

            byte[] responseBytes = responseBody.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}