package com.example.photoflow.data;

import android.content.Context;

public class SettingsRepository {

    private static volatile SettingsRepository instance;
    
    private SettingsDataSource dataSource;
    private final Context context;

    public SettingsRepository(Context context) {
        this.dataSource = new SettingsDataSource(context);
        this.context = context.getApplicationContext(); // Use app context to avoid leaks
    }

    public static SettingsRepository getInstance(Context context) {
        if (instance == null) {
            instance = new SettingsRepository(context);
        }
        return instance;
    }

    public void updateProfilePic(long photoId, SettingsDataSource.SettingsCallback<Boolean> callback) {
        dataSource.updateProfilePic(photoId, new SettingsDataSource.SettingsCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> result) {
                // Handle success
                callback.onSuccess(result);
            }

            @Override
            public void onError(Result.Error error) {
                // Handle error
                callback.onError(error);
            }
        });
    }

    public void updateEmail(String email, SettingsDataSource.SettingsCallback<Boolean> callback) {
        dataSource.updateEmail(email, new SettingsDataSource.SettingsCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> result) {
                // Handle success
                callback.onSuccess(result);
            }

            @Override
            public void onError(Result.Error error) {
                // Handle error
                callback.onError(error);
            }
        });
    }

}