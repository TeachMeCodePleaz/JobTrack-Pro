package com.jobtrack;

import com.jobtrack.db.DatabaseManager;
import com.jobtrack.db.DatabaseExecutor;
import com.jobtrack.dao.UserDao;
import com.jobtrack.dao.ApplicationDao;
import com.jobtrack.model.User;

import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Starting JobTrack Pro Multi-User Test...");
        
        // Step 1: Initialize Database
        DatabaseManager.initializeDatabase();

        // Step 2: Register users (Async writes)
        UserDao.registerUser("sodium_dev", "safePass123");
        UserDao.registerUser("nyu_tester", "tandon2026");

        // Small sleep to ensure async registrations complete before logging in
        Thread.sleep(1000);

        // Step 3: Simulate Logins and Data Entry using CompletableFuture
        System.out.println("\n--- Attempting Logins ---");

        // User 1: sodium_dev
        CompletableFuture<User> loginFuture1 = UserDao.authenticate("sodium_dev", "safePass123");
        
        // Use .join() to wait for the background thread to finish verifying the password
        User user1 = loginFuture1.join(); 
        if (user1 != null) {
            System.out.println("[App] Login successful for: " + user1.getUsername() + " (ID: " + user1.getId() + ")");
            // Link applications to User 1
            ApplicationDao.addApplicationAsync(user1.getId(), "Amazon", "SDE Intern", "Applied", "Referral from alumni");
            ApplicationDao.addApplicationAsync(user1.getId(), "TikTok", "Backend Intern", "OA Pending", "Java focus");
            ApplicationDao.addApplicationAsync(user1.getId(), "Tesla", "Software Engineer", "Applied", "NYC Office");
        } else {
            System.out.println("[App] Login failed for sodium_dev.");
        }

        // User 2: nyu_tester
        CompletableFuture<User> loginFuture2 = UserDao.authenticate("nyu_tester", "tandon2026");
        
        User user2 = loginFuture2.join();
        if (user2 != null) {
            System.out.println("[App] Login successful for: " + user2.getUsername() + " (ID: " + user2.getId() + ")");
            // Link applications to User 2
            ApplicationDao.addApplicationAsync(user2.getId(), "Google", "Data Analyst", "Interview", "Round 1 complete");
            ApplicationDao.addApplicationAsync(user2.getId(), "Meta", "Frontend Intern", "Offer", "Accepted!");
        } else {
            System.out.println("[App] Login failed for nyu_tester.");
        }

        // Step 4: Graceful Shutdown
        System.out.println("\n--- Main Thread Execution Finished ---");
        System.out.println("Waiting for background database operations to complete...");
        DatabaseExecutor.shutdown();
    }
}