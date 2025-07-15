package com.example.photoflow.ui.settings;

import androidx.annotation.Nullable;

public class SettingFormState {

    @Nullable
    private Integer emailError;
    
    private boolean isDataValid;
 
    public SettingFormState(boolean isDataValid) {
        this.isDataValid = isDataValid;
        this.emailError = null;
    }

    public SettingFormState(@Nullable Integer emailError) {
        this.emailError = emailError;
        this.isDataValid = false;
    }

    @Nullable
    public Integer getEmailError() {
        return emailError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }

}