package com.example.photoflow.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.photoflow.R;
import com.example.photoflow.data.AlbumDataSource;
import com.example.photoflow.data.AlbumRepository;
import com.example.photoflow.data.Result;
import com.example.photoflow.data.model.AlbumItem;

public class AddAlbumViewModel extends ViewModel {

    private boolean isLoading;
    private final AlbumRepository albumRepository;
    private final MutableLiveData<AddAlbumFormState> formState = new MutableLiveData<>();
    private final MutableLiveData<AddAlbumResult> addAlbumResult = new MutableLiveData<>();

    AddAlbumViewModel(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
        isLoading = false;
    }
    
    public LiveData<AddAlbumFormState> getFormState() {
        return formState;
    }

    public LiveData<AddAlbumResult> getAddAlbumResult() {
        return addAlbumResult;
    }

    public void addAlbum(String title, Long coverId) {
        isLoading = true;
        albumRepository.addAlbum(title, coverId, new AlbumDataSource.AlbumCallback<Boolean>() {
                    @Override
                    public void onSuccess(Result<Boolean> result) {
                        isLoading = false;

                        if (result instanceof Result.Success) {
                            Boolean success = ((Result.Success<Boolean>) result).getData();
                            if (success != null && success) {
                                Log.d("AddAlbumViewModel", "Album added successfully");
                                addAlbumResult.postValue(new AddAlbumResult(true));
                            } else {
                                Log.e("AddAlbumViewModel", "Add album returned false success");
                                addAlbumResult.postValue(new AddAlbumResult("Add album failed"));
                            }
                        } else {
                            Log.e("AddAlbumViewModel", "Unexpected result type in onSuccess");
                            addAlbumResult.postValue(new AddAlbumResult("Unexpected result"));
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        isLoading = false;
                        Log.e("AddAlbumViewModel", "Add album failed: " + e.getMessage());
                        addAlbumResult.postValue(new AddAlbumResult("Add album failed: " + e.getMessage()));
                    }
                }
        );

    }

    public void dataChanged(String title) {
        if (!isTitleValid(title)) {
            formState.setValue(new AddAlbumFormState(R.string.invalid_album_title, false));
        } else {
            formState.setValue(new AddAlbumFormState(true));
        }
    }

    private boolean isTitleValid(String title) {
        return title != null && title.trim().length() > 0;
    }

}
