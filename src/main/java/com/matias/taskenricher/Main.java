package com.matias.taskenricher;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        ExternalApiService externalApiService = new ExternalApiService();
        TaskManager taskManager = new TaskManager(externalApiService);
        ApiServer apiServer = new ApiServer(taskManager);

        try {
            apiServer.start(8080);
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor API: " + e.getMessage());
            e.printStackTrace();
        }
    }
}