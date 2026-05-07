package com.jobtrack.controller;

import com.jobtrack.service.GitHubApiMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * SystemController handles basic health checks and system-wide data retrieval.
 */
@RestController
public class SystemController {

    // Inject the background monitoring service
    @Autowired
    private GitHubApiMonitor apiMonitor;

    /**
     * Health check endpoint to verify the server is running.
     * URL: /api/status
     */
    @GetMapping("/api/status")
    public Map<String, String> getStatus() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "System Ready");
        response.put("version", "1.0-Web");
        response.put("message", "JobTrack Pro Backend is running on Spring Boot!");
        return response;
    }

    /**
     * The frontend polls this endpoint to get both the top ticker notification
     * and the bottom live market table data.
     * URL: /api/system/live-market
     */
    @GetMapping("/api/system/live-market")
    public Map<String, Object> getLiveMarketData() {
        Map<String, Object> response = new HashMap<>();
        response.put("notification", apiMonitor.getLatestNotification());
        response.put("jobsList", apiMonitor.getLatestJobsList());
        return response;
    }
}