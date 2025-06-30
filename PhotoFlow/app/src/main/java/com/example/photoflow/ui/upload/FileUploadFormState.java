package com.example.photoflow.ui.upload;

import androidx.annotation.Nullable;

public class FileUploadFormState {
    @Nullable
    private Integer fileNameError;
    @Nullable
    private Integer titleError;
    @Nullable
    private Integer tagsError;
    private boolean isDataValid;

    public FileUploadFormState(@Nullable Integer fileNameError, @Nullable Integer titleError, @Nullable Integer tagsError) {
        this.fileNameError = fileNameError;
        this.titleError = titleError;
        this.tagsError = tagsError;
        this.isDataValid = false;
    }

    public FileUploadFormState(boolean isDataValid) {
        this.isDataValid = isDataValid;
        this.fileNameError = null;
        this.titleError = null;
        this.tagsError = null;
    }

    @Nullable
    public Integer getFileNameError() {
        return fileNameError;
    }

    @Nullable
    public Integer getTitleError() {
        return titleError;
    }

    @Nullable
    public Integer getTagsError() {
        return tagsError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}

