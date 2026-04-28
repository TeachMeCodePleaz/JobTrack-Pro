package com.jobtrack.dao;

import com.jobtrack.db.DatabaseManager;
import com.jobtrack.db.DatabaseExecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ApplicationDao {

    /**
     * 异步添加一条新的求职申请记录。
     * 结合了 JDBC 和单线程并发调度器。
     */
    public static void addApplicationAsync(int userId, String companyName, String position, String status, String notes) {
        // 将包含 SQL 逻辑的任务打包成一个 Runnable，扔给单线程池排队执行
        DatabaseExecutor.executeWrite(() -> {
            String sql = "INSERT INTO applications(user_id, company_name, position, status, notes) VALUES(?, ?, ?, ?, ?)";
            
            // 使用 try-with-resources 自动管理数据库连接关闭
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                 
                pstmt.setInt(1, userId);
                pstmt.setString(2, companyName);
                pstmt.setString(3, position);
                pstmt.setString(4, status);
                pstmt.setString(5, notes);
                
                pstmt.executeUpdate();
                System.out.println("[DB] 成功异步写入一条申请记录: " + companyName + " - " + position);
                
            } catch (SQLException e) {
                System.err.println("[DB Error] 写入记录失败: " + e.getMessage());
            }
        });
    }
}