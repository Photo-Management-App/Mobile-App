package com.example.photoflow.ui.settings;

import androidx.annotation.Nullable;

public class SettingsResult {
    
    @Nullable
    private final Boolean success;
    @Nullable
    private final String message;


    public SettingsResult(@Nullable Boolean success) {
        this.success = success;
        this.message = null;
    }

    public SettingsResult(@Nullable String message) {
        this.message = message;
        this.success = null;
    }

    @Nullable
    public Boolean getSuccess() {
        return success;
    }
    @Nullable
    public String getMessage() {
        return message;
    }


}
