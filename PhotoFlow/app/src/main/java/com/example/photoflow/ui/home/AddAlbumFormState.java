package com.example.photoflow.ui.home;

import androidx.annotation.Nullable;


public class AddAlbumFormState {

    @Nullable
    private Integer titleError;

    private boolean isDataValid;

    public AddAlbumFormState(@Nullable Integer titleError, boolean isDataValid) {
        this.titleError = titleError;
        this.isDataValid = isDataValid;
    }

    public AddAlbumFormState(boolean isDataValid) {
        this.isDataValid = isDataValid;
        this.titleError = null;
    }

    @Nullable
    public Integer getTitleError() {
        return titleError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }

}
