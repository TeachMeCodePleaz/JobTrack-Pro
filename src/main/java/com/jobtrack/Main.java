package com.jobtrack;

import com.jobtrack.db.DatabaseManager;
import com.jobtrack.db.DatabaseExecutor;
import com.jobtrack.dao.ApplicationDao;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting JobTrack Pro...");

        DatabaseManager.initializeDatabase();
        System.out.println("System ready.");

        System.out.println("Submitting write tasks to the background pool...");
        
        // Simulate concurrent user/background updates
        ApplicationDao.addApplicationAsync(1, "Amazon", "SDE Intern", "Applied", "Referral from alumni");
        ApplicationDao.addApplicationAsync(1, "TikTok", "Backend Intern", "OA Pending", "Need to review multithreading");

        // Ensures the background thread completes existing tasks before exiting
        DatabaseExecutor.shutdown();
        
        System.out.println("Main thread execution finished. Waiting for background DB operations...");
    }
}