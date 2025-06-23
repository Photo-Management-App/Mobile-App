package com.example.photoflow.data.model;

/**
 * Data class that captures user information for registered users retrieved from RegisterRepository
 */
public class RegisteredUser {

    private String userId;
    private String displayName;
    private String email;

    public RegisteredUser(String userId, String displayName, String email) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
    }

    public RegisteredUser(String displayName) {
        this.userId = null;
        this.displayName = displayName;
        this.email = null;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }
}
