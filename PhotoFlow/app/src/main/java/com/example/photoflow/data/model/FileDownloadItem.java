package com.example.photoflow.data.model;

public class FileDownloadItem {
    public long id;
    public String fileName;
    public byte[] data;

    public FileDownloadItem(long id, String fileName, byte[] data) {
        this.id = id;
        this.fileName = fileName;
        this.data = data;
    }
}

