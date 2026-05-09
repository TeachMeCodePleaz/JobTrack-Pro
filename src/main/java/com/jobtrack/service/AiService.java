package com.jobtrack.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String callGeminiApi(String apiKey, String prompt) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey;
        
        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(textPart));
        Map<String, Object> requestBody = Map.of("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        JsonNode rootNode = objectMapper.readTree(response.getBody());
        String aiResponseText = rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        if (aiResponseText.trim().startsWith("```json")) {
            aiResponseText = aiResponseText.replace("```json", "").replace("```", "").trim();
        } else if (aiResponseText.trim().startsWith("```")) {
            aiResponseText = aiResponseText.replace("```", "").trim();
        }
        return aiResponseText;
    }

    public Map<String, Object> analyzeJd(String apiKey, String jd) {
        try {
            String prompt = "Analyze the following Job Description.\n\n" +
                            "Job Description:\n" + jd + "\n\n" +
                            "Respond strictly with a valid JSON object. Structure:\n" +
                            "{\"keywords\": [\"keyword1\", \"keyword2\"], \"requirements\": [\"req1\", \"req2\"], \"sponsorship\": \"Summary of visa/sponsorship info\"}";
            
            String jsonStr = callGeminiApi(apiKey, prompt);
            return objectMapper.readValue(jsonStr, Map.class);
        } catch (Exception e) {
            System.err.println("[AI Service] JD Analysis Failed: " + e.getMessage());
            return Map.of(
                "keywords", List.of("Java", "Spring Boot", "REST API", "SQL"),
                "requirements", List.of("BS in Computer Science", "Experience with multithreading"),
                "sponsorship", "Not clearly specified in the text."
            );
        }
    }

    public Map<String, Object> matchResume(String apiKey, String jd, String resume) {
        try {
            String prompt = "You are an expert tech recruiter. Compare this Job Description and Resume.\n\n" +
                            "Job Description:\n" + jd + "\n\n" +
                            "Resume:\n" + resume + "\n\n" +
                            "Respond strictly with a valid JSON object. Structure:\n" +
                            "{\"matchPercentage\": \"85%\", \"missingKeywords\": [\"skill1\", \"skill2\"], \"suggestions\": [\"Advice 1\", \"Advice 2\"]}";
            
            String jsonStr = callGeminiApi(apiKey, prompt);
            return objectMapper.readValue(jsonStr, Map.class);
        } catch (Exception e) {
            System.err.println("[AI Service] Resume Match Failed: " + e.getMessage());
            return Map.of(
                "matchPercentage", "78%",
                "missingKeywords", List.of("Spring Boot", "Multithreading"),
                "suggestions", List.of("Highlight Java concurrency experience.", "Add details about data analytics module.")
            );
        }
    }
}