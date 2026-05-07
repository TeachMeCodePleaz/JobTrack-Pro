package com.jobtrack;

import com.jobtrack.db.DatabaseManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import javax.annotation.PostConstruct; // Replaced with jakarta.annotation.PostConstruct for Spring Boot 3.x compatibility
import jakarta.annotation.PostConstruct;

/**
 * The main entry point for the JobTrack Pro Web Application.
 * This class starts the embedded Tomcat server.
 */
@SpringBootApplication
public class JobTrackApplication {

    public static void main(String[] args) {
        // Start the Spring Boot Web Server
        SpringApplication.run(JobTrackApplication.class, args);
    }

    /**
     * Runs automatically after the Spring context is initialized.
     * We use this to initialize our SQLite database.
     */
    @PostConstruct
    public void init() {
        System.out.println("[System] Initializing Database for Web Environment...");
        DatabaseManager.initializeDatabase();
    }
}