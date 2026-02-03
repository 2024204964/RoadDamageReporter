package com.example.roaddamagereporter;

import java.io.Serializable;

public class Report implements Serializable {

    private String reportId;
    private String userId;
    private String description;
    private String imagePath; // local content URI
    private double latitude;
    private double longitude;
    private long timestamp;

    public Report() {} // required for Firebase

    // full constructor without reportId
    public Report(String userId, String description, String imagePath, double latitude, double longitude, long timestamp) {
        this.userId = userId;
        this.description = description;
        this.imagePath = imagePath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // Optional constructor with reportId
    public Report(String reportId, String userId, String description, String imagePath,
                  double latitude, double longitude, long timestamp) {
        this.reportId = reportId;
        this.userId = userId;
        this.description = description;
        this.imagePath = imagePath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    // Alias getter (so code can call getImageUrl() too)
    public String getImageUrl() { return imagePath; }
    public void setImageUrl(String imageUrl) { this.imagePath = imageUrl; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getReportDetails() {
        return "Description: " + description +
                "\nLatitude: " + latitude +
                "\nLongitude: " + longitude;
    }
}
