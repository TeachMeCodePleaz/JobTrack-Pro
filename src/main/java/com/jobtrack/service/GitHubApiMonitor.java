package com.jobtrack.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class GitHubApiMonitor {

    private final RestTemplate restTemplate = new RestTemplate();
    
    // Concurrency & Thread Safety
    // use volatile to ensure visibility across threads, and CopyOnWriteArrayList for thread-safe updates
    private volatile List<Map<String, String>> latestJobsList = new CopyOnWriteArrayList<>();
    private volatile String lastNewestJobTitle = "";
    private volatile String latestNotification = "📡 System initializing... Scanning GitHub repository.";

    // Parallel Multithreading
    // Using a fixed thread pool to manage concurrent requests to different branches
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

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
        System.out.println("\n[Background Thread] PARALLEL SCRAPING GITHUB REPO AT " + fetchTime);

        String[] branches = {"dev", "main", "master"};

        // Parallel Multithreading: simultaneously start 3 threads to fetch 3 branches, use whoever fetches first!
        List<CompletableFuture<String>> futures = Arrays.stream(branches)
                .map(branch -> CompletableFuture.supplyAsync(() -> {
                    try {
                        String url = "https://raw.githubusercontent.com/SimplifyJobs/Summer2026-Internships/" + branch + "/README.md";
                        String responseText = restTemplate.getForObject(url, String.class);
                        if (responseText != null && (responseText.contains("<tr>") || responseText.contains("|"))) {
                            System.out.println("[Thread-" + Thread.currentThread().getId() + "] Successfully fetched from branch: " + branch);
                            return responseText;
                        }
                    } catch (Exception e) {
                        // one of the threads might fail due to branch not existing, just log and let others continue
                    }
                    return null;
                }, executorService))
                .collect(Collectors.toList());

        // Wait for all threads to complete and get the first successful result
        String rawData = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (rawData != null) {
            parseRepositoryData(rawData, fetchTime);
        } else {
            System.err.println("[Background Thread] ERROR: Could not fetch data from any branch.");
        }
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

                    String finalUrl = "https://github.com/SimplifyJobs/Summer2026-Internships";
                    Matcher urlMatcher = urlPattern.matcher(rowHtml);
                    while (urlMatcher.find()) {
                        String found = urlMatcher.group(1) != null ? urlMatcher.group(1) : urlMatcher.group(2);
                        if (found == null) continue;
                        if (!found.contains("imgur.com") && !found.contains("simplify.jobs/c/")) {
                            finalUrl = found;
                            break; 
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
                    if (parsedJobs.size() >= 50) break;
                }
            }
        }

        if (!parsedJobs.isEmpty()) {
            // Concurrency & Thread Safety: update the shared latestJobsList atomically by replacing the entire list reference
            latestJobsList = new CopyOnWriteArrayList<>(parsedJobs);
            
            Map<String, String> newestJob = parsedJobs.get(0);
            String currentTopJob = newestJob.get("company") + " - " + newestJob.get("title");
            if (!currentTopJob.equals(this.lastNewestJobTitle)) {
                this.lastNewestJobTitle = currentTopJob;
                this.latestNotification = "Live Update (" + fetchTime + "): " + currentTopJob;
            }
        }
    }
}