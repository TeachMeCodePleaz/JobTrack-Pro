package com.jobtrack.controller;

import com.jobtrack.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/workshop")
public class AiWorkshopController {

    @Autowired
    private AiService aiService;

    @PostMapping("/analyze-jd")
    public ResponseEntity<?> analyzeJd(@RequestBody Map<String, String> request) {
        String apiKey = request.get("apiKey");
        String jd = request.get("jd");

        if (apiKey == null || apiKey.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "API Key is required."));
        }
        if (jd == null || jd.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Job Description is required."));
        }

        return ResponseEntity.ok(aiService.analyzeJd(apiKey, jd));
    }

    @PostMapping("/match-resume")
    public ResponseEntity<?> matchResume(@RequestBody Map<String, String> request) {
        String apiKey = request.get("apiKey");
        String jd = request.get("jd");
        String resume = request.get("resume");

        if (apiKey == null || apiKey.trim().isEmpty() || jd == null || resume == null || jd.isEmpty() || resume.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "API Key, JD, and Resume are all required for matching."));
        }

        return ResponseEntity.ok(aiService.matchResume(apiKey, jd, resume));
    }
}