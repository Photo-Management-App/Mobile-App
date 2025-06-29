package com.example.photoflow.data.model;

public class PhotoItem {
    public final String base64Image;  // or image URL
    public final String title;
    public final String description;
    public final String coordinates;
    public final String createdAt;

    public PhotoItem(String base64Image, String title, String description, String coordinates, String createdAt) {
        this.base64Image = base64Image;
        this.title = title;
        this.description = description;
        this.coordinates = coordinates;
        this.createdAt = createdAt;

    }
    
    public String getBase64() { return base64Image; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCoordinates() { return coordinates; }
    public String getCreatedAt() { return createdAt; }
}

