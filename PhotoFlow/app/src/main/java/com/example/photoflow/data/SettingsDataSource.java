package com.example.photoflow.data;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.example.photoflow.R;
import com.example.photoflow.data.model.UserSession;
import com.example.photoflow.data.util.TokenManager;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler;

public class SettingsDataSource {

    private static final String TAG = "SettingsDataSource";
    private Context context;

    public interface SettingsCallback<T> {
        void onSuccess(Result<T> result);
        void onError(Result.Error error);
    }

    public SettingsDataSource(Context context) {
        this.context = context.getApplicationContext(); // Use app context to avoid leaks
    } 

    public void updateProfilePic(long photoId, SettingsCallback callback) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting profile picture update request...");
                URL url = new URL(context.getString(R.string.base_url) + "/register");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", UserSession.getUser().getEmail()); // Assuming you have a UserSession to get the email
                jsonParam.put("profile", String.valueOf(photoId)); // Assuming you have a UserSession to get the profile pic ID
                jsonParam.put("token", TokenManager.loadToken(context)); // Assuming you have a TokenManager to get the token

                Log.d(TAG, "Sending JSON: " + jsonParam.toString());

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "HTTP response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Update successful
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(new Result.Success<>(true)));
                } else {
                    // Handle error
                    String errorMessage = "Failed to update profile picture. Response code: " + responseCode;
                    Log.e(TAG, errorMessage);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(new Result.Error(new IOException(errorMessage))));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error updating profile picture", e);
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(new Result.Error(new IOException(e))));
            }
        }).start();
    }

        public void updateEmail(String email, SettingsCallback callback) {
            new Thread(() -> {
                try {
                    Log.d(TAG, "Starting email update request...");
                    URL url = new URL(context.getString(R.string.base_url) + "/register");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoOutput(true);

                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("email", email);
                    jsonParam.put("profile", String.valueOf(UserSession.getUser().getProfilePicId())); // Assuming you have a UserSession to get the profile pic ID
                    jsonParam.put("token", TokenManager.loadToken(context)); // Assuming you have a TokenManager to get the token

                    Log.d(TAG, "Sending JSON: " + jsonParam.toString());

                    OutputStream os = conn.getOutputStream();
                    os.write(jsonParam.toString().getBytes("UTF-8"));
                    os.close();

                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "HTTP response code: " + responseCode);

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Email update successful
                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(new Result.Success<>(true)));
                    } else {
                        // Handle error
                        String errorMessage = "Failed to update email. Response code: " + responseCode;
                        Log.e(TAG, errorMessage);
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError(new Result.Error(new IOException(errorMessage))));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error updating email", e);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError(new Result.Error(new IOException(e))));
                }
            }).start();
        
    }

}