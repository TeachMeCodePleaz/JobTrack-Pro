package com.jobtrack.dao;

import com.jobtrack.db.DatabaseManager;
import com.jobtrack.db.DatabaseExecutor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ApplicationDao {

    /**
     * Fetches all applications for a specific user asynchronously.
     */
    public static CompletableFuture<List<String[]>> getApplicationsByUserId(int userId) {
        CompletableFuture<List<String[]>> future = new CompletableFuture<>();
        DatabaseExecutor.executeWrite(() -> {
            List<String[]> list = new ArrayList<>();
            String sql = "SELECT id, company_name, position, status, notes FROM applications WHERE user_id = ?";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    list.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("company_name"),
                        rs.getString("position"),
                        rs.getString("status"),
                        rs.getString("notes")
                    });
                }
                future.complete(list);
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    /**
     * Updated to support a callback after writing is finished.
     */
    public static void addApplicationAsync(int userId, String company, String pos, String status, String notes, Runnable onComplete) {
        DatabaseExecutor.executeWrite(() -> {
            String sql = "INSERT INTO applications(user_id, company_name, position, status, notes) VALUES(?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, company);
                pstmt.setString(3, pos);
                pstmt.setString(4, status);
                pstmt.setString(5, notes);
                pstmt.executeUpdate();
                if (onComplete != null) onComplete.run();
            } catch (SQLException e) {
                System.err.println("[DB Error] " + e.getMessage());
            }
        });
    }
}