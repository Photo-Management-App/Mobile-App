package com.example.photoflow.ui.home;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoflow.data.AlbumDataSource;
import com.example.photoflow.data.AlbumRepository;
import com.example.photoflow.data.FileDataSource;
import com.example.photoflow.data.FileRepository;

public class AddAlbumViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public AddAlbumViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AddAlbumViewModel.class)) {
            // Initialize FileRepository
            FileDataSource fileDataSource = new FileDataSource(context);
            FileRepository fileRepository = FileRepository.getInstance(fileDataSource, context);

            // Pass it to AlbumDataSource and AlbumRepository
            AlbumDataSource albumDataSource = new AlbumDataSource(context, fileRepository);
            AlbumRepository albumRepository = AlbumRepository.getInstance(context);

            return (T) new AddAlbumViewModel(albumRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
