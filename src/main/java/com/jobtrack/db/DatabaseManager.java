package com.jobtrack.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // 数据库文件将存放在项目根目录
    private static final String DB_URL = "jdbc:sqlite:jobtrack_pro.db";

    /**
     * 获取数据库连接。每次需要查询时调用此方法。
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * 初始化数据库架构。应该在程序入口 (main 方法) 处最先调用。
     */
    public static void initializeDatabase() {
        // 使用 try-with-resources 确保资源自动释放
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. 创建用户表
            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                );
                """;
            stmt.execute(createUsersTable);

            // 2. 创建求职申请表
            // 根据 Proposal 包含: company name, position, status, notes
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

            System.out.println("数据库初始化成功，表结构已就绪。");

        } catch (SQLException e) {
            System.err.println("数据库初始化失败: " + e.getMessage());
            // 数据库初始化失败属于致命错误，直接抛出运行时异常
            throw new RuntimeException("Database initialization failed", e);
        }
    }
}