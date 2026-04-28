package com.jobtrack.dao;

import com.jobtrack.db.DatabaseManager;
import com.jobtrack.db.DatabaseExecutor;
import com.jobtrack.util.SecurityUtils;
import com.jobtrack.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

/**
 * Data Access Object for User-related operations.
 */
public class UserDao {

    /**
     * Registers a new user with a hashed password.
     */
    public static void registerUser(String username, String plainPassword) {
        String hashedPassword = SecurityUtils.hashPassword(plainPassword);
        
        DatabaseExecutor.executeWrite(() -> {
            String sql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, username);
                pstmt.setString(2, hashedPassword);
                pstmt.executeUpdate();
                System.out.println("[Auth] User registered successfully: " + username);
                
            } catch (SQLException e) {
                System.err.println("[Auth Error] Registration failed for " + username + ": " + e.getMessage());
            }
        });
    }

    /**
     * Authenticates a user by checking the database and verifying the BCrypt hash.
     * Note: This uses CompletableFuture to return results from the async DB operation.
     */
    public static CompletableFuture<User> authenticate(String username, String plainPassword) {
        CompletableFuture<User> future = new CompletableFuture<>();
        
        // We use the executor to keep DB access off the main thread
        DatabaseExecutor.executeWrite(() -> {
            String sql = "SELECT id, username, password_hash FROM users WHERE username = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (SecurityUtils.verifyPassword(plainPassword, storedHash)) {
                        future.complete(new User(rs.getInt("id"), rs.getString("username"), storedHash));
                    } else {
                        future.complete(null); // Wrong password
                    }
                } else {
                    future.complete(null); // User not found
                }
                
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }
}