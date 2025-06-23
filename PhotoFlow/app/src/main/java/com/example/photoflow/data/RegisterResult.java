package com.example.photoflow.data;

import androidx.annotation.Nullable;

import com.example.photoflow.ui.register.RegisteredUserView;

/**
 * Registration result: success (user details) or error message.
 */
public class RegisterResult {
    @Nullable
    private RegisteredUserView success;
    @Nullable
    private Integer error;

    public RegisterResult(@Nullable Integer error) {
        this.error = error;
    }

    public RegisterResult(@Nullable RegisteredUserView success) {
        this.success = success;
    }

    @Nullable
    public RegisteredUserView getSuccess() {
        return success;
    }

    @Nullable
    public Integer getError() {
        return error;
    }
}

