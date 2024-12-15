package client_code.sql_server.backend;

import fi.iki.elonen.NanoHTTPD;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class SimpleServer extends NanoHTTPD {
    private static final String DB_URL = "jdbc:sqlite:server.db";
    private final UserRequestToSQL requestToSQL;

    public SimpleServer(int port) {
        super(port);
        this.requestToSQL = new UserRequestToSQL();
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Response response;

        // Parse query parameters
        Map<String, String> params = session.getParms();
        String userQuery = params.get("userQuery");

        // Debugging: Print the requested URI and parameters
        System.out.println("Received request for URI: " + uri);
        System.out.println("Parameters: " + params);

        switch (uri) {
            case "/users":
                response = getUsers(userQuery);
                break;
            case "/books":
                response = getBooks(userQuery);
                break;
            default:
                response = newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found");
                break;
        }

        // Add CORS headers
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Debugging: Print the response being sent
        System.out.println("Sending response: " + response.getMimeType() + " - " + response.getStatus());

        return response;
    }

    private Response getUsers(String userQuery) {
        String sqlQuery;
        if (userQuery != null && !userQuery.isEmpty()) {
            try {
                sqlQuery = requestToSQL.processUserRequest(userQuery);
            } catch (Exception e) {
                e.printStackTrace();
                String errorJson = "{\"error\": \"" + e.getMessage() + "\"}";
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json", errorJson);
            }
        } else {
            sqlQuery = "SELECT * FROM users";
        }

        return executeSQL(sqlQuery);
    }

    private Response getBooks(String userQuery) {
        String sqlQuery;
        if (userQuery != null && !userQuery.isEmpty()) {
            try {
                sqlQuery = requestToSQL.processBookRequest(userQuery);
            } catch (Exception e) {
                e.printStackTrace();
                String errorJson = "{\"error\": \"" + e.getMessage() + "\"}";
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json", errorJson);
            }
        } else {
            sqlQuery = "SELECT * FROM books";
        }

        return executeSQL(sqlQuery);
    }

    private Response executeSQL(String sqlQuery) {
        StringBuilder response = new StringBuilder("[");
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery)) {

            while (rs.next()) {
                int columnCount = rs.getMetaData().getColumnCount();
                response.append("{");
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rs.getMetaData().getColumnName(i);
                    String columnValue = rs.getString(i);
                    response.append("\"").append(columnName).append("\":\"").append(columnValue).append("\"");
                    if (i < columnCount) response.append(",");
                }
                response.append("},");
            }
        } catch (Exception e) {
            String errorJson = "{\"error\": \"" + e.getMessage() + "\"}";
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json", errorJson);
        }
        if (response.charAt(response.length() - 1) == ',') {
            response.deleteCharAt(response.length() - 1);
        }
        response.append("]");

        System.out.println("Executed SQL: " + sqlQuery);
        System.out.println("Response: " + response);

        return newFixedLengthResponse(Response.Status.OK, "application/json", response.toString());
    }
    
    private static void cleanupResources() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Drop tables (if needed)
            stmt.execute("DROP TABLE IF EXISTS users");
            stmt.execute("DROP TABLE IF EXISTS books");

            // Delete the database file
            File dbFile = new File("server.db");
            if (dbFile.exists() && dbFile.delete()) {
                System.out.println("Database file deleted.");
            } else {
                System.out.println("Failed to delete database file.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Initialize the database
        DatabaseInitializer.initializeDatabase();

        // Get the JSON file path
        String jsonFilePath = new File(SimpleServer.class.getResource("data.json").getFile()).getAbsolutePath();

        // Load data from JSON into the database
        JsonLoader.loadJsonData(jsonFilePath);

        SimpleServer server = new SimpleServer(8080);

        // Add a shutdown hook to stop the server gracefully
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.stop();
            System.out.println("Server stopped. Cleaning up resources...");
            cleanupResources();
            System.out.println("Resources cleaned up.");
        }));

        try {
            server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            System.out.println("Server started at http://localhost:8080");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

