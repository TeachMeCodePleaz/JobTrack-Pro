package com.jobtrack.controller;

import com.jobtrack.dao.ApplicationDao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * REST API Controller for managing job applications.
 */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    // DTO for receiving JSON data from the frontend
    public static class ApplicationRequest {
        public int userId;
        public String companyName;
        public String position;
        public String status;
        public String notes;
    }

    /**
     * GET: Fetch all applications for a specific user.
     * URL: /api/applications?userId=1
     */
    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getApplications(@RequestParam int userId) {
        try {
            List<String[]> rawData = ApplicationDao.getApplicationsByUserId(userId).join();
            
            // Convert String array to a Map so it becomes nice JSON objects
            List<Map<String, String>> responseList = new ArrayList<>();
            for (String[] row : rawData) {
                Map<String, String> app = new HashMap<>();
                app.put("id", row[0]);
                app.put("companyName", row[1]);
                app.put("position", row[2]);
                app.put("status", row[3]);
                app.put("notes", row[4]);
                responseList.add(app);
            }
            return ResponseEntity.ok(responseList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST: Add a new application.
     * URL: /api/applications
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> addApplication(@RequestBody ApplicationRequest req) {
        Map<String, String> response = new HashMap<>();
        try {
            // Use CompletableFuture to turn the callback into a waitable task
            CompletableFuture<Void> future = new CompletableFuture<>();
            ApplicationDao.addApplicationAsync(req.userId, req.companyName, req.position, req.status, req.notes, () -> {
                future.complete(null);
            });
            future.join(); // Wait for the DB write to finish

            response.put("message", "Application added successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to add application");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}