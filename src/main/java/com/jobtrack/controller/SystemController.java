package com.jobtrack.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * SystemController handles basic health checks and system status.
 */
@RestController
public class SystemController {

    /**
     * Test endpoint to verify the server is running.
     * URL: http://localhost:8080/api/status
     */
    @GetMapping("/api/status")
    public Map<String, String> getStatus() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "System Ready");
        response.put("version", "1.0-Web");
        response.put("message", "JobTrack Pro Backend is running on Spring Boot!");
        return response;
    }
}