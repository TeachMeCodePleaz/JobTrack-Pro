package com.jobtrack.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager handles the lifecycle of the SQLite database.
 * It ensures tables are created upon the first launch.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:jobtrack_pro.db";

    /**
     * Establishes a connection to the SQLite database.
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * Initializes the database schema.
     * This method should be called at the start of the application.
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create Users table for authentication
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                """;
            stmt.execute(createUsersTable);

            // Create Applications table for job tracking
            String createApplicationsTable = """
                CREATE TABLE IF NOT EXISTS applications (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    company_name TEXT NOT NULL,
                    position TEXT NOT NULL,
                    status TEXT NOT NULL,
                    notes TEXT,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
                );
                """;
            stmt.execute(createApplicationsTable);

            System.out.println("[DB] Database initialized successfully. Tables are ready.");

        } catch (SQLException e) {
            System.err.println("[DB Error] Initialization failed: " + e.getMessage());
            throw new RuntimeException("Critical database error", e);
        }
    }
}