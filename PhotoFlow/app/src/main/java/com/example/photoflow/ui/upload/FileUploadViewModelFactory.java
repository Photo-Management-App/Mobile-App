package com.example.photoflow.ui.upload;


import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoflow.data.FileDataSource;
import com.example.photoflow.data.FileRepository;

public class FileUploadViewModelFactory implements ViewModelProvider.Factory {

    private final String base64EncodedFile;
    private final Context context;

    public FileUploadViewModelFactory(String base64EncodedFile, Context context) {
        this.base64EncodedFile = base64EncodedFile;
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FileUploadViewModel.class)) {
            FileDataSource dataSource = new FileDataSource(context);
            FileRepository repo = FileRepository.getInstance(dataSource, context);
            return (T) new FileUploadViewModel(base64EncodedFile, repo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}

