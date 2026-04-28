package com.jobtrack.db;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseExecutor {
    
    // 核心亮点：单线程执行器。所有并发的写操作在这里被强制排队（串行化）
    private static final ExecutorService dbWriter = Executors.newSingleThreadExecutor();

    /**
     * 提交数据库写任务。
     * 任何线程调用此方法，任务都会进入队列，由唯一的后台线程安全执行。
     */
    public static void executeWrite(Runnable task) {
        dbWriter.submit(task);
    }

    /**
     * 在系统关闭时调用，优雅地释放线程池资源。
     */
    public static void shutdown() {
        dbWriter.shutdown();
    }
}