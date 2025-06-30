package com.example.photoflow.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PhotoViewModel extends ViewModel {

    private final MutableLiveData<String> mText = new MutableLiveData<>();

    public PhotoViewModel() {
        mText.setValue("This is photo detail fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
