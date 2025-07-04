package com.example.photoflow.data.model;

import android.graphics.Bitmap;

public class AlbumItem {

    private final long id;
    private final String title;
    private final long coverId;
    private final Bitmap coverImage;

    public AlbumItem(long id, String title, long coverId, Bitmap coverImage) {
        this.id = id;
        this.title = title;
        this.coverId = coverId;
        this.coverImage = coverImage;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getCoverId() {
        return coverId;
    }

    public Bitmap getCoverImage() {
        return coverImage;
    }

}
