package client_code.sql_server.backend;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

public class JsonLoader {
    private static final String DB_URL = "jdbc:sqlite:server.db";

    public static void loadJsonData(String jsonFilePath) {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Loading JSON data into database..." + jsonFilePath);

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            @SuppressWarnings("unchecked")
            Map<String, List<Map<String, Object>>> data = mapper.readValue(new File(jsonFilePath), Map.class);

            // Load users
            List<Map<String, Object>> users = data.get("users");
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO users (name, email, company, age) VALUES (?, ?, ?, ?)")) {
                for (Map<String, Object> user : users) {
                    pstmt.setString(1, (String) user.get("name"));
                    pstmt.setString(2, (String) user.get("email"));
                    pstmt.setString(3, (String) user.get("company"));
                    pstmt.setInt(4, (Integer) user.get("age"));
                    pstmt.executeUpdate();
                }
            }

            // Load books
            List<Map<String, Object>> books = data.get("books");
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO books (title, author, genre, year) VALUES (?, ?, ?, ?)")) {
                for (Map<String, Object> book : books) {
                    pstmt.setString(1, (String) book.get("title"));
                    pstmt.setString(2, (String) book.get("author"));
                    pstmt.setString(3, (String) book.get("genre"));
                    pstmt.setInt(4, (Integer) book.get("year"));
                    pstmt.executeUpdate();
                }
            }

            System.out.println("JSON data loaded into database.");
        } catch (Exception e) {
            System.out.println("Error loading JSON data into database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JsonLoader() {
    }
}
