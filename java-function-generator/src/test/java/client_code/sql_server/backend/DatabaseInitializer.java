package client_code.sql_server.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseInitializer {
    private static final String DB_URL = "jdbc:sqlite:server.db";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement()) {
            // Create users table
        stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                     + "id INTEGER PRIMARY KEY, "
                     + "name TEXT NOT NULL, "
                     + "email TEXT NOT NULL, "
                     + "company TEXT NOT NULL, "
                     + "age INTEGER NOT NULL)");

        // Create books table
        stmt.execute("CREATE TABLE IF NOT EXISTS books ("
                     + "id INTEGER PRIMARY KEY, "
                     + "title TEXT NOT NULL, "
                     + "author TEXT NOT NULL, "
                     + "genre TEXT NOT NULL, "
                     + "year INTEGER NOT NULL)");

            System.out.println("Database initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private DatabaseInitializer() {
    }
}
