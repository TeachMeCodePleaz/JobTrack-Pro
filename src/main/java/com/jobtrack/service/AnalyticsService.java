package com.jobtrack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private GitHubApiMonitor apiMonitor;

    public Map<String, Object> generateMarketAnalytics() {
        List<Map<String, String>> jobs = apiMonitor.getLatestJobsList();

        if (jobs == null || jobs.isEmpty()) {
            Map<String, Object> emptyResponse = new HashMap<>();
            emptyResponse.put("totalJobs", 0);
            emptyResponse.put("topCompanies", new ArrayList<>());
            return emptyResponse;
        }

        int totalJobs = jobs.size();

        Map<String, Long> companyCounts = jobs.stream()
                .collect(Collectors.groupingBy(job -> job.get("company"), Collectors.counting()));

        List<Map<String, Object>> topCompanies = companyCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(entry -> {
                    Map<String, Object> stat = new HashMap<>();
                    stat.put("company", entry.getKey());
                    stat.put("count", entry.getValue());
                    
                    int percentage = (int) Math.round((entry.getValue() * 100.0) / totalJobs);
                    stat.put("percentage", percentage);
                    
                    return stat;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("totalJobs", totalJobs);
        response.put("topCompanies", topCompanies);
        
        return response;
    }
}