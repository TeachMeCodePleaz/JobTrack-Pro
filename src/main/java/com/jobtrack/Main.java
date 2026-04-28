package com.jobtrack;

import com.jobtrack.db.DatabaseManager;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting JobTrack Pro...");
        
        // 第一步：初始化数据库
        DatabaseManager.initializeDatabase();
        
        System.out.println("System ready.");
    }
}