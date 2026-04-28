package com.jobtrack.model;

/**
 * Represents a user in the JobTrack Pro system.
 */
public class User {
    private int id;
    private String username;
    private String passwordHash;

    public User(int id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
}