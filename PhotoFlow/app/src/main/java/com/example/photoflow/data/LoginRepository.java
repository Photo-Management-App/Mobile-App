package com.example.photoflow.data;

import android.content.Context;

import com.example.photoflow.data.model.LoggedInUser;
import com.example.photoflow.data.util.TokenManager;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private LoginDataSource dataSource;
    private final Context context;


    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource, Context context) {
        this.dataSource = dataSource;
        this.context = context.getApplicationContext(); // Use app context to avoid leaks
    }

    public static LoginRepository getInstance(LoginDataSource dataSource , Context context) {
        if(instance == null){
            instance = new LoginRepository(dataSource, context);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        if (context != null) {
            TokenManager.clearToken(context); // Clears token from SharedPreferences
        }
    }


    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    // Async login with callback
    public void login(String username, String password, LoginDataSource.LoginCallback callback) {
        dataSource.login(username, password, new LoginDataSource.LoginCallback() {
            @Override
            public void onSuccess(Result<LoggedInUser> result) {
                setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
                callback.onSuccess(result);
            }

            @Override
            public void onError(Result.Error error) {
                callback.onError(error);
            }
        });
    }

    public LoggedInUser getLoggedInUser() {
        return user;
    }


}
