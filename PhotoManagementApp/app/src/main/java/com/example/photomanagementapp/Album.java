package com.example.photomanagementapp;

import java.util.List;

public class Album {
    private String name;
    private int coverImageResId; // or a URL/path
    private List<Integer> photoResIds; // or List<String> if from storage

    public Album(String name, int coverImageResId, List<Integer> photoResIds) {
        this.name = name;
        this.coverImageResId = coverImageResId;
        this.photoResIds = photoResIds;
    }

    public String getName() { return name; }
    public int getCoverImageResId() { return coverImageResId; }
    public List<Integer> getPhotoResIds() { return photoResIds; }
}

