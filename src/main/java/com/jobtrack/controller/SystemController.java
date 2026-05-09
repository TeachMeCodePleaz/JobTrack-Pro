package com.jobtrack.controller;

import com.jobtrack.service.AnalyticsService;
import com.jobtrack.service.GitHubApiMonitor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SystemController {

    @Autowired
    private GitHubApiMonitor apiMonitor;

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/api/status")
    public Map<String, String> getStatus() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "System Ready");
        return response;
    }

    @GetMapping("/api/system/live-market")
    public Map<String, Object> getLiveMarketData() {
        Map<String, Object> response = new HashMap<>();
        response.put("notification", apiMonitor.getLatestNotification());
        response.put("jobsList", apiMonitor.getLatestJobsList());
        return response;
    }

    @GetMapping("/api/system/analytics")
    public Map<String, Object> getAnalytics() {
        return analyticsService.generateMarketAnalytics();
    }
}