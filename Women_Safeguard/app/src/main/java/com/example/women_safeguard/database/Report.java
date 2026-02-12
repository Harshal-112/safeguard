package com.example.women_safeguard.database;

public class Report {
    private String userId;
    private String description;
    private String location;
    private String imagePath;
    private String status; // New field for Admin Approval

    public Report() {
        // Default constructor required for Firebase
    }

    public Report(String userId, String description, String location, String imagePath) {
        this.userId = userId;
        this.description = description;
        this.location = location;
        this.imagePath = imagePath;
    }

    public String getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status; // Set status for approval/rejection
    }
}

