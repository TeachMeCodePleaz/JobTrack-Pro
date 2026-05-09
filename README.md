# JobTrack Pro 🚀

**Course:** CS6103 Introduction to Java - Spring 2026
**Name:** Ruihan Zhang
**NetID:** rz3373
Please see the video about the project details.

## 📖 Project Overview
JobTrack Pro is a comprehensive, full-stack desktop-class web application designed to streamline the software engineering job-hunting process. It moves beyond a simple CRUD application by integrating a real-time multi-threaded web scraper, advanced data analytics using Java Stream API, and an AI-powered Resume Workshop that leverages the Gemini 2.5 Flash Large Language Model.

## 🛠️ 3+ Advanced Java Concepts Implemented

### 1. Multithreading & Concurrency (Thread Safety)
To provide real-time market data without freezing the user interface, I implemented parallel multithreading.
* **Parallel Fetching:** Utilized `CompletableFuture` and `ExecutorService` to concurrently spawn multiple threads, scraping data from different GitHub branches simultaneously. The system dynamically selects the first successful thread, significantly reducing network latency.
* **Thread Safety & Memory Visibility:** Background `@Scheduled` threads constantly update the market data while multiple HTTP requests might attempt to read it. To prevent `ConcurrentModificationException` and race conditions, the global job list is wrapped in a `CopyOnWriteArrayList` and marked with the `volatile` keyword, ensuring strict thread safety and transactional atomicity across user sessions.

### 2. Network Programming & RESTful API Architecture
The application heavily relies on Java networking concepts to communicate with both internal and external services.
* **External Data Scraping:** Used Spring's `RestTemplate` to establish HTTP connections with GitHub's raw data servers, pulling live markdown/HTML repository data.
* **AI Integration:** Constructed custom JSON payloads in Java and executed POST requests to the Google Gemini API to process resumes and job descriptions.
* **RESTful Backend:** Built a robust Controller-Service architecture exposing endpoints (`/api/applications`, `/api/system/live-market`, `/api/workshop/*`) that strictly adhere to REST conventions.

### 3. Java Stream API & Real-Time Data Analytics
Instead of simply displaying the scraped data, the application performs real-time analytics on the backend.
* Leveraged Java 8 `Stream API` to process the parsed job listings. Used complex stream operations such as `Collectors.groupingBy` and `Map.Entry.comparingByValue().reversed()` to dynamically calculate the top hiring companies and their market percentage.
* This data is structured into a nested JSON object and sent to the frontend to render a live, pure CSS-based data visualization dashboard.

### 4. Advanced Regex & State-Machine Parsing
Developed a highly resilient dual-engine parser (handling both Raw HTML and Markdown table formats). It utilizes `Pattern.DOTALL` for multi-line regex extraction and a custom state-machine algorithm to safely parse markdown pipes (`|`) while ignoring those nested inside hyperlink brackets, ensuring zero data corruption during the scrape.

---

## 🚀 How to Run the Project

This project is fully self-contained and designed to be runnable out of the box.

### Prerequisites
* Java 17 or higher
* Maven installed
* An IDE (IntelliJ IDEA, Eclipse, or VS Code)

### Execution Steps
1.  Unzip the project folder and open it in your preferred IDE.
2.  Navigate to `src/main/java/com/jobtrack/JobTrackProApplication.java`.
3.  Run the `main` method to start the Spring Boot server.
4.  Open your web browser and go to: `http://localhost:8080/dashboard.html`

*(Note: If a database is required for the application tracker CRUD functionality, an H2 in-memory database is pre-configured and will automatically initialize upon startup.)*

---

## 🔑 API Key Handling (Important for Grading)

As per the final project requirements, **no API keys are hardcoded in the source code**. 

To test the **AI Workshop** feature:
1. Navigate to the "AI Workshop" tab in the application (`http://localhost:8080/workshop.html`).
2. The UI is designed to **ask for the user input an API key** before any processing occurs.
3. Please copy and paste the following provided Gemini API Key into the secure input field on the screen:
   
   **Grader API Key:** `Please see the APIkey.txt and copy it, test it in the project`

   *(This key is specifically authorized for the `gemini-2.5-flash` model required by the workshop).*
4. Paste a sample Job Description and Resume in the respective text areas, and click "Extract JD Insights" or "Analyze & Optimize Resume" to see the networking and AI integration in action.

--- 
*Designed and built for CS6103 Final Project.*