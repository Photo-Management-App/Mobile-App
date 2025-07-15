package  com.example.photoflow.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    private String email;
    //... other data fields that may be accessible to the UI

    LoggedInUserView(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
    }

    String getDisplayName() {
        return displayName;
    }

    String getEmail() {
        return email;
    }
}