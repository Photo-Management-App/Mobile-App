package com.example.photoflow.ui.register;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoflow.R;
import com.example.photoflow.data.RegisterDataSource;
import com.example.photoflow.data.RegisterRepository;

public class RegisterViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public RegisterViewModelFactory(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            String baseUrl = context.getString(R.string.base_url);
            return (T) new RegisterViewModel(RegisterRepository.getInstance(new RegisterDataSource(baseUrl)));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}

