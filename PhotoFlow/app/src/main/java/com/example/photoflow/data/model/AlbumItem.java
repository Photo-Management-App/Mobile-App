package com.example.photoflow.data.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class AlbumItem implements Serializable {

    private final long id;
    private final String title;
    private final Bitmap coverImage;

    public AlbumItem(long id, String title, Bitmap coverImage) {
        this.id = id;
        this.title = title;
        this.coverImage = coverImage;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getCoverImage() {
        return coverImage;
    }

}
