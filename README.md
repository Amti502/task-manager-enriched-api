# Gestor de Tareas Enriquecidas con API en Java

Este proyecto es una aplicación Java que gestiona una lista de tareas. Cada tarea puede tener una descripción y, opcionalmente, una ubicación. Si se proporciona una ubicación reconocida, la aplicación consulta la API de Open-Meteo para obtener la temperatura horaria actual de esa ubicación y la añade a los datos de la tarea.

La aplicación expone una API HTTP que permite:
*   Listar todas las tareas (incluyendo datos de ubicación enriquecidos si están disponibles).
*   Obtener una tarea específica por su ID.
*   Añadir nuevas tareas (con o sin ubicación para enriquecimiento).

Este proyecto fue desarrollado como un ejercicio práctico para demostrar conceptos de Java, manejo de colecciones, consumo de APIs externas con `java.net.http.HttpClient`, creación de un servidor HTTP simple con `com.sun.net.httpserver`, y serialización/deserialización JSON con la librería Gson.

## Tecnologías Utilizadas

*   **Java:** JDK 21 (o la versión que estés usando).
*   **Apache Maven:** Para la gestión de dependencias y la construcción del proyecto.
*   **`com.sun.net.httpserver`:** Librería incorporada en el JDK para crear el servidor HTTP.
*   **`java.net.http.HttpClient`:** Cliente HTTP incorporado en Java (desde Java 11+) para consumir la API externa.
*   **Gson (Google):** Librería para convertir objetos Java a formato JSON y viceversa. Versión utilizada: 2.10.1 (o la que tengas en tu `pom.xml`).
*   **Open-Meteo API:** API pública gratuita utilizada para obtener datos meteorológicos (temperatura horaria).

## Requisitos Previos

Antes de ejecutar este proyecto, asegúrate de tener instalado:

1.  **Java Development Kit (JDK):** Versión 11 o superior (recomendado JDK 17 o 21). Asegúrate de que esté correctamente configurado en las variables de entorno de tu sistema (que el comando `java -version` funcione en tu terminal).
2.  **Apache Maven:** (Opcional si solo vas a ejecutar desde un IDE que lo maneje). Asegúrate de que esté correctamente configurado en las variables de entorno (que el comando `mvn -version` funcione).
3.  **Git:** Para clonar el repositorio.
4.  **Conexión a Internet:** Necesaria para que la aplicación pueda consultar la API de Open-Meteo.

## Instrucciones para Ejecutar el Proyecto

Sigue estos pasos para poner en marcha la aplicación:

1.  **Clonar el Repositorio desde GitHub:**
    Abre tu terminal o consola y ejecuta:
    ```bash
    git clone https://github.com/Amti502/task-manager-enriched-api.git
    ```
    *(Nota: Reemplaza `Amti502/task-manager-enriched-api` con tu nombre de usuario y nombre de repositorio exactos).*

2.  **Navegar a la Carpeta del Proyecto:**
    ```bash
    cd task-manager-enriched-api
    ```

3.  **Opción A: Construir y Ejecutar con Maven (Recomendado para un JAR ejecutable):**
    *   **Construir el proyecto:** Este comando compilará el código y empaquetará la aplicación en un archivo `.jar` dentro de la carpeta `target/`.
        ```bash
        mvn clean package
        ```
    *   **Ejecutar el archivo JAR:** Una vez construido, ejecuta el JAR. El nombre del JAR será `artifactId-version.jar` (ej: `task-manager-enriched-api-1.0-SNAPSHOT.jar`).
        ```bash
        java -jar target/task-manager-enriched-api-1.0-SNAPSHOT.jar
        ```
    La aplicación se iniciará y verás un mensaje en la consola indicando que el servidor API está escuchando en el puerto 8080.

4.  **Opción B: Ejecutar directamente desde un IDE (como IntelliJ IDEA):**
    *   Abre IntelliJ IDEA (o tu IDE preferido).
    *   Selecciona `File` -> `Open...` y navega hasta la carpeta `task-manager-enriched-api` que clonaste. Ábrela como un proyecto.
    *   IntelliJ debería reconocerlo como un proyecto Maven. Permite que importe las dependencias si te lo pide (especialmente Gson).
    *   Localiza la clase `Main.java` dentro del paquete `com.matias.taskenricher` (o tu paquete) en `src/main/java`.
    *   Haz clic derecho sobre el archivo `Main.java` o dentro del editor en el método `main` y selecciona "Run 'Main.main()'".
    *   Verás la salida en la consola de IntelliJ, indicando que el servidor API se ha iniciado y los logs de las llamadas a la API de Open-Meteo.

## Endpoints de la API

Una vez que el servidor esté en ejecución (escuchando en `http://localhost:8080`), puedes interactuar con la API usando un navegador web (para GETs), Postman, Insomnia o `curl`.

### 1. Listar Todas las Tareas
*   **Método:** `GET`
*   **URL:** `http://localhost:8080/api/tasks`
*   **Respuesta Exitosa (Código 200):** Un array JSON con todas las tareas. Las tareas con ubicaciones mapeadas (`Oficina San Isidro`, `Supermercado Salaverry`, `Supermercado Cerca`) incluirán un objeto `enrichedLocationData` con la temperatura.
    ```json
    [
      {
        "id": 1,
        "description": "Comprar leche",
        "completed": false,
        "locationName": "Supermercado Cerca",
        "enrichedLocationData": {
          "temperature": 16.4, // Ejemplo de temperatura
          "weatherDescription": "Temperatura Horaria (Open-Meteo)"
        }
      },
      // ... más tareas
    ]
    ```

### 2. Obtener una Tarea por ID
*   **Método:** `GET`
*   **URL:** `http://localhost:8080/api/tasks/{id}`
    *   Reemplaza `{id}` con el ID numérico de la tarea (ej: `/api/tasks/1`).
*   **Respuesta Exitosa (Código 200):** Un objeto JSON con los detalles de la tarea encontrada.
*   **Respuesta si no se encuentra (Código 404):** `{"error":"Tarea con ID {id} no encontrada"}`

### 3. Añadir una Nueva Tarea
*   **Método:** `POST`
*   **URL:** `http://localhost:8080/api/tasks`
*   **Headers:** `Content-Type: application/json`
*   **Cuerpo de la Petición (Body - raw JSON):**
    *   **Ejemplo con ubicación (para enriquecimiento):**
        ```json
        {
            "description": "Llevar el coche al taller",
            "locationName": "Oficina San Isidro" 
        }
        ```
    *   **Ejemplo sin ubicación:**
        ```json
        {
            "description": "Leer documentación de Salesforce"
        }
        ```
*   **Respuesta Exitosa (Código 201 - Created):** Un objeto JSON con los detalles de la tarea recién creada, incluyendo su ID y `enrichedLocationData` si la ubicación fue procesada.
*   **Respuesta si faltan datos (Código 400):** `{"error":"La descripcion de la tarea es requerida"}`

## Autor

Matias Zúñiga (Amti502)

## Consideraciones

*   **Mapeo de Ubicaciones:** El enriquecimiento de datos de clima solo funciona para las ubicaciones pre-mapeadas a coordenadas en `ExternalApiService.java` (`Oficina San Isidro`, `Supermercado Salaverry`, `Supermercado Cerca`). Otras ubicaciones no obtendrán datos de clima.
*   **Persistencia:** Los datos de las tareas se almacenan solo en memoria y se pierden cuando la aplicación se detiene.
*   **API de Clima:** Se utiliza Open-Meteo. La fiabilidad depende de la disponibilidad de este servicio externo.
*   **Simplificación:** Para el parseo de la respuesta horaria de Open-Meteo, se toma la primera temperatura disponible en el array.