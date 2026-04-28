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

   public static CompletableFuture<User> authenticate(String username, String plainPassword) {
    CompletableFuture<User> future = new CompletableFuture<>();
    
    DatabaseExecutor.executeWrite(() -> {
        String sql = "SELECT id, username, password_hash FROM users WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                // 增加调试输出
                System.out.println("[Debug] User found in DB. Verifying password...");
                
                if (SecurityUtils.verifyPassword(plainPassword, storedHash)) {
                    System.out.println("[Debug] Password verified!");
                    future.complete(new User(rs.getInt("id"), rs.getString("username"), storedHash));
                } else {
                    System.out.println("[Debug] Password verification failed.");
                    future.complete(null);
                }
            } else {
                System.out.println("[Debug] Username not found in database: " + username);
                future.complete(null);
            }
            
        } catch (SQLException e) {
            System.err.println("[Debug Error] DB Query error: " + e.getMessage());
            future.completeExceptionally(e);
        }
    });
    
    return future;
    
    }
}