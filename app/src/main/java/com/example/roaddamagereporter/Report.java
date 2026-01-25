package com.example.roaddamagereporter;

import java.io.Serializable;

public class Report implements Serializable {

    private String userId;
    private String description;
    private String imagePath; // local content URI
    private double latitude;
    private double longitude;
    private long timestamp;

    public Report() {} // required for Firebase

    public Report(String userId, String description, String imagePath, double latitude, double longitude, long timestamp) {
        this.userId = userId;
        this.description = description;
        this.imagePath = imagePath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getImageUrl() {
        return getImageUrl();
    }
}
