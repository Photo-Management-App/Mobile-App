package com.example.photoflow.data.model;

import android.graphics.Bitmap;

public class PhotoItem {
    public final Bitmap bitmap;  // or image URL
    public final String title;
    public final String createdAt;
    public final String[] tags;

    public PhotoItem(Bitmap bitmap, String title, String createdAt, String[] tags) {
        this.bitmap = bitmap;
        this.title = title;
        this.createdAt = createdAt;
        this.tags = tags;

    }
    
    public Bitmap getBitmap() { return bitmap; }
    public String getTitle() { return title; }
    public String getCreatedAt() { return createdAt; }
    public String[] getTags() { return tags;}
}

