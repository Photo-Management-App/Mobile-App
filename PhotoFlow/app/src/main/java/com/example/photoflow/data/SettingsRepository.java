package com.example.photoflow.data;

import android.content.Context;

public class SettingsRepository {

    private static volatile SettingsRepository instance;
    
    private SettingsDataSource dataSource;
    private final Context context;

    private SettingsRepository(SettingsDataSource dataSource, Context context) {
        this.dataSource = dataSource;
        this.context = context.getApplicationContext(); // Use app context to avoid leaks
    }

    public static SettingsRepository getInstance(SettingsDataSource dataSource, Context context) {
        if (instance == null) {
            instance = new SettingsRepository(dataSource, context);
        }
        return instance;
    }

    public void updateProfilePic(long photoId, SettingsDataSource.SettingsCallback callback) {
        dataSource.updateProfilePic(photoId, new SettingsDataSource.SettingsCallback() {
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

    public void updateEmail(String email, SettingsDataSource.SettingsCallback callback) {
        dataSource.updateEmail(email, new SettingsDataSource.SettingsCallback() {
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