package com.example.photoflow.ui.register;

/**
 * Class exposing registered user details to the UI.
 */
public class RegisteredUserView {
    private String displayName;

    public RegisteredUserView(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

