package  com.example.photoflow.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    private String email;
    private long profilePicId;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, String email, long profilePicId) {
        this.displayName = displayName;
        this.email = email;
        this.profilePicId = profilePicId;
    }

    String getDisplayName() {
        return displayName;
    }

    String getEmail() {
        return email;
    }

    long getProfilePicId() {
        return profilePicId;
    }
}