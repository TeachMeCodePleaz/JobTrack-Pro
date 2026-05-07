package com.jobtrack.controller;

import com.jobtrack.dao.UserDao;
import com.jobtrack.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AuthController handles user registration and login via REST APIs.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * Data Transfer Object (DTO) to map incoming JSON to Java fields.
     */
    public static class AuthRequest {
        public String username;
        public String password;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody AuthRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            // Call your existing Phase 3 logic!
            UserDao.registerUser(request.username, request.password);
            
            response.put("message", "Registration successful! You can now login.");
            return ResponseEntity.ok(response); // HTTP 200 OK
        } catch (Exception e) {
            response.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        // Wait for the async database query to complete using .join()
        User user = UserDao.authenticate(request.username, request.password).join();
        
        if (user != null) {
            response.put("message", "Login successful!");
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            return ResponseEntity.ok(response); // HTTP 200 OK
        } else {
            response.put("error", "Invalid username or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response); // HTTP 401
        }
    }
}