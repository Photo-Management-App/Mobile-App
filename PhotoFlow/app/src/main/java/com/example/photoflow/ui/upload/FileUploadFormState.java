package com.example.photoflow.ui.upload;

import androidx.annotation.Nullable;

public class FileUploadFormState {
    @Nullable
    private Integer fileNameError;
    @Nullable
    private Integer tagsError;
    private boolean isDataValid;

    public FileUploadFormState(@Nullable Integer fileNameError, @Nullable Integer tagsError, boolean isDataValid) {
        this.fileNameError = fileNameError;
        this.tagsError = tagsError;
        this.isDataValid = isDataValid;
    }

    public FileUploadFormState(boolean isDataValid) {
        this.isDataValid = isDataValid;
        this.fileNameError = null;
        this.tagsError = null;
    }

    @Nullable
    public Integer getFileNameError() {
        return fileNameError;
    }

    @Nullable
    public Integer getTagsError() {
        return tagsError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}

