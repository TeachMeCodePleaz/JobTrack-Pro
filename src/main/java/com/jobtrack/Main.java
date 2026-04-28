package com.jobtrack;

import com.jobtrack.db.DatabaseManager;
import com.jobtrack.db.DatabaseExecutor;
import com.jobtrack.dao.ApplicationDao;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting JobTrack Pro...");
        
        // 第一步：初始化数据库
        DatabaseManager.initializeDatabase();
        System.out.println("System ready.");

        // 第二步：测试单线程并发写入 
        System.out.println("正在向后台线程池提交写任务...");
        
        // 模拟用户在 GUI 上疯狂点击或者后台爬虫同时抓取到数据
        ApplicationDao.addApplicationAsync(1, "Amazon", "SDE Intern", "Applied", "找学长内推的");
        ApplicationDao.addApplicationAsync(1, "TikTok", "Backend Intern", "OA Pending", "需要复习 Java 多线程");

        // 第三步：优雅关闭线程池
        // 告诉线程池：“不接新任务了，把队列里排队的这俩任务干完就下班。”
        DatabaseExecutor.shutdown();
        
        System.out.println("主线程执行完毕，等待后台数据库操作...");
    }
}