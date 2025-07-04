package com.example.photoflow.ui.home;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoflow.data.AlbumDataSource;
import com.example.photoflow.data.AlbumRepository;

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
            AlbumDataSource dataSource = new AlbumDataSource(context);
            AlbumRepository repo = AlbumRepository.getInstance(dataSource, context);
            return (T) new AddAlbumViewModel(repo);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}