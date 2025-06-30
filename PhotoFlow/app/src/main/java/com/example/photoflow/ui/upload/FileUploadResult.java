package com.example.photoflow.ui.upload;

import androidx.annotation.Nullable;

/**
 * Class that holds the result of a file upload attempt.
 */
public class FileUploadResult {
    @Nullable
    private final Boolean success;
    @Nullable
    private final String error;

    public FileUploadResult(@Nullable Boolean success) {
        this.success = success;
        this.error = null;
    }

    public FileUploadResult(@Nullable String error) {
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
