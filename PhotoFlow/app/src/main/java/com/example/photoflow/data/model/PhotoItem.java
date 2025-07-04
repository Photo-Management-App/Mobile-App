package com.example.photoflow.data.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.graphics.Bitmap;

public class PhotoItem implements Serializable{
    public final long id;
    public final Bitmap bitmap;  // or image URL
    public final String fileName;
    public final String createdAt;
    public final String[] tags;

    public PhotoItem(long id, Bitmap bitmap, String fileName, String createdAt, String[] tags) {
        this.id = id;
        this.bitmap = bitmap;
        this.fileName = fileName;
        this.createdAt = createdAt;
        this.tags = tags;

    }
    
    public long getId() { return id; }
    public Bitmap getBitmap() { return bitmap; }
    public String getFileName() { return fileName; }
    public String[] getTags() { return tags;}

    public String getCreatedAt() {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(createdAt);

            SimpleDateFormat readableFormat = new SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault());
            return readableFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return createdAt; // fallback to raw value
        }
    }

}


