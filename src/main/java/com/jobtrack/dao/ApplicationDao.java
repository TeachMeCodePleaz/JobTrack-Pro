package com.jobtrack.dao;

import com.jobtrack.db.DatabaseManager;
import com.jobtrack.db.DatabaseExecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Data Access Object for job application records.
 */
public class ApplicationDao {

    /**
     * Asynchronously adds a new job application to the database.
     */
    public static void addApplicationAsync(int userId, String companyName, String position, String status, String notes) {
        DatabaseExecutor.executeWrite(() -> {
            String sql = "INSERT INTO applications(user_id, company_name, position, status, notes) VALUES(?, ?, ?, ?, ?)";
            
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                 
                pstmt.setInt(1, userId);
                pstmt.setString(2, companyName);
                pstmt.setString(3, position);
                pstmt.setString(4, status);
                pstmt.setString(5, notes);
                
                pstmt.executeUpdate();
                System.out.println("[DB] Async write success: " + companyName + " (" + position + ")");
                
            } catch (SQLException e) {
                System.err.println("[DB Error] Failed to write record: " + e.getMessage());
            }
        });
    }
}