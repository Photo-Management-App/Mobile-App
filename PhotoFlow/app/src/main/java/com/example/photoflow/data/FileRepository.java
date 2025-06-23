package com.example.photoflow.data;

import android.content.Context;

import com.example.photoflow.data.model.FileUploadItem;

import java.util.List;

public class FileRepository {

    private static FileRepository instance;
    private final FileDataSource dataSource;

    private FileRepository(Context context) {
        this.dataSource = new FileDataSource(context);
    }

    public static FileRepository getInstance(Context context) {
        if (instance == null) {
            instance = new FileRepository(context);
        }
        return instance;
    }

    public void uploadFiles(List<FileUploadItem> files, FileDataSource.FileUploadCallback callback) {
        dataSource.uploadFile(files, callback);
    }

    public void downloadFiles(List<Long> fileIds, FileDataSource.FileDownloadCallback callback) {
        dataSource.downloadFiles(fileIds, callback);
    }
}

