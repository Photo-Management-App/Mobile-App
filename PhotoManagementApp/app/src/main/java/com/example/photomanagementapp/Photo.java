package com.example.photomanagementapp;

public class Photo {
    private String title;
    private String username;
    private String imageUrl; // or resource id, whatever you use
    private String description;

    public Photo(String title, String username, String imageUrl, String description) {
        this.title = title;
        this.username = username;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getTitle() { return title; }
    public String getUsername() { return username; }
    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
}
