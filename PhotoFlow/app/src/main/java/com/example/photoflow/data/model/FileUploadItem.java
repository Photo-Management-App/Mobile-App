package com.example.photoflow.data.model;

import org.json.JSONObject;

import java.util.List;

public class FileUploadItem {
    public byte[] data;
    public JSONObject metadata;
    public List<String> tags;

    public FileUploadItem(byte[] data, JSONObject metadata, List<String> tags) {
        this.data = data;
        this.metadata = metadata;
        this.tags = tags;
    }
}

