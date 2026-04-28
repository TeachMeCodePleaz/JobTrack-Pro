package com.jobtrack.db;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DatabaseExecutor provides a dedicated thread pool for database write operations.
 * This ensures thread safety and transactional atomicity by serializing writes.
 */
public class DatabaseExecutor {
    
    // Single-threaded executor to handle all database write tasks in sequence
    private static final ExecutorService dbWriter = Executors.newSingleThreadExecutor();

    /**
     * Submits a database write task to the background queue.
     * @param task Runnable task containing SQL logic
     */
    public static void executeWrite(Runnable task) {
        dbWriter.submit(task);
    }

    /**
     * Gracefully shuts down the executor, allowing pending tasks to complete.
     */
    public static void shutdown() {
        dbWriter.shutdown();
    }
}