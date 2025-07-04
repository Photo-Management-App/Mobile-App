package com.example.photoflow.ui.home;

import androidx.annotation.Nullable;

public class AddAlbumResult {

    @Nullable
    private final Boolean success;
    @Nullable
    private final String error;

    public AddAlbumResult(@Nullable Boolean success) {
        this.success = success;
        this.error = null;
    }

    public AddAlbumResult(@Nullable String error) {
        this.error = error;
        this.success = null;
    }

    @Nullable
    public Boolean getSuccess() {
        return success;
    }
    @Nullable
    public String getError() {
        return error;
    }


}
