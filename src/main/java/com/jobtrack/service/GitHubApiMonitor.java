package com.jobtrack.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GitHubApiMonitor {

    private final RestTemplate restTemplate = new RestTemplate();
    private List<Map<String, String>> latestJobsList = new ArrayList<>();
    private String lastNewestJobTitle = "";
    private String latestNotification = "📡 System online. Monitoring SimplifyJobs Repo...";

    public List<Map<String, String>> getLatestJobsList() {
        return latestJobsList;
    }

    public String getLatestNotification() {
        return latestNotification;
    }

    @Async
    @Scheduled(fixedRate = 30000)
    public void monitorJobsAPI() {
        String fetchTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("\n[Background Thread] SCRAPING GITHUB AT " + fetchTime);

        String[] branches = {"dev", "main", "master"};
        String rawData = null;

        for (String branch : branches) {
            try {
                String url = "https://raw.githubusercontent.com/SimplifyJobs/Summer2026-Internships/" + branch + "/README.md";
                String responseText = restTemplate.getForObject(url, String.class);
                if (responseText != null && (responseText.contains("<tr>") || responseText.contains("|"))) {
                    rawData = responseText;
                    break;
                }
            } catch (Exception e) {}
        }

        if (rawData != null) parseRepositoryData(rawData, fetchTime);
    }

    private void parseRepositoryData(String data, String fetchTime) {
        List<Map<String, String>> parsedJobs = new ArrayList<>();
        Pattern urlPattern = Pattern.compile("href=\"(https?://[^\"]+)\"|(?<!href=\")(https?://[^\\s\\)\"'>]+)");

        if (data.contains("<tr>") && data.contains("<td>")) {
            Pattern rowPattern = Pattern.compile("<tr>(.*?)</tr>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
            Matcher rowMatcher = rowPattern.matcher(data);
            
            while (rowMatcher.find()) {
                String rowHtml = rowMatcher.group(1);
                Pattern cellPattern = Pattern.compile("<td.*?>(.*?)</td>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
                Matcher cellMatcher = cellPattern.matcher(rowHtml);
                
                List<String> cells = new ArrayList<>();
                while (cellMatcher.find()) cells.add(cellMatcher.group(1).trim());
                
                if (cells.size() >= 3) {
                    String rawCompany = cells.get(0).replaceAll("<[^>]*>", "").replaceAll("[*#🛂🔒\\[\\]]", "").trim();
                    String rawRole = cells.get(1).replaceAll("<[^>]*>", "").replaceAll("[*#\\[\\]]", "").trim();
                    
                    if (rawCompany.isEmpty() || rawCompany.toLowerCase().contains("company") || rawCompany.contains("↳")) continue;

                    // --- NEW SECURITY URL FILTERING ---
                    String finalUrl = "https://github.com/SimplifyJobs/Summer2026-Internships";
                    Matcher urlMatcher = urlPattern.matcher(rowHtml);
                    
                    while (urlMatcher.find()) {
                        String found = urlMatcher.group(1) != null ? urlMatcher.group(1) : urlMatcher.group(2);
                        if (found == null) continue;

                        // BLACKLIST: Ignore Imgur (screenshots) and Simplify company profiles
                        boolean isImgur = found.contains("imgur.com");
                        boolean isSimplifyProfile = found.contains("simplify.jobs/c/");
                        
                        if (!isImgur && !isSimplifyProfile) {
                            finalUrl = found;
                            break; // Take the FIRST valid application link and stop
                        }
                    }

                    Map<String, String> jobData = new HashMap<>();
                    jobData.put("company", rawCompany);
                    jobData.put("title", rawRole);
                    jobData.put("url", finalUrl);
                    
                    String lastCell = cells.get(cells.size() - 1).replaceAll("<[^>]*>", "").trim();
                    jobData.put("posted", (!lastCell.contains("http") && lastCell.length() <= 15) ? lastCell : "Recent");

                    if (parsedJobs.stream().noneMatch(j -> j.get("company").equals(rawCompany) && j.get("title").equals(jobData.get("title")))) {
                        parsedJobs.add(jobData);
                    }
                    if (parsedJobs.size() >= 20) break;
                }
            }
        }

        if (!parsedJobs.isEmpty()) {
            latestJobsList = parsedJobs;
            Map<String, String> newestJob = parsedJobs.get(0);
            String currentTopJob = newestJob.get("company") + " - " + newestJob.get("title");
            if (!currentTopJob.equals(this.lastNewestJobTitle)) {
                this.lastNewestJobTitle = currentTopJob;
                this.latestNotification = "Live Update (" + fetchTime + "): " + currentTopJob;
            }
        }
    }
}