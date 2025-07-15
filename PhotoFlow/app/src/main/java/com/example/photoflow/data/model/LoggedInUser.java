package com.example.photoflow.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private String displayName;
    private String email;
    private long profilePicId;

    public LoggedInUser( String displayName, String email, long profilePicId) {

        this.displayName = displayName;
        this.email = email;
        this.profilePicId = profilePicId;
    }
    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public long getProfilePicId() {
        return profilePicId;
    }
}