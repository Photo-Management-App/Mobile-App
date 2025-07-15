package com.example.photoflow.data.model;

public class TagItem {
    
    public final long id;
    public final String name;

    public TagItem(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
