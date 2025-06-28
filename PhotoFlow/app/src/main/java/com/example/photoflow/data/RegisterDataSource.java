package com.example.photoflow.data;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.photoflow.data.model.RegisteredUser;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterDataSource {

    private static final String TAG = "RegisterDataSource";

    public interface RegisterCallback {
        void onSuccess(Result<RegisteredUser> result);
        void onError(Result.Error error);
    }

    public void register(String username, String password, String email, RegisterCallback callback) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting registration request...");

                //URL url = new URL("http://ec2-13-60-9-150.eu-north-1.compute.amazonaws.com:8000/register");
                URL url = new URL("http://192.168.0.145:8000/register");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("login", username);
                jsonParam.put("password", password);
                jsonParam.put("email", email);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "HTTP response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    RegisteredUser user = new RegisteredUser(username); // Or parse server response if available

                    new Handler(Looper.getMainLooper()).post(() -> 
                        callback.onSuccess(new Result.Success<>(user))
                    );
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> 
                        callback.onError(new Result.Error(new IOException("Registration failed: " + responseCode)))
                    );
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception during registration", e);
                new Handler(Looper.getMainLooper()).post(() -> 
                    callback.onError(new Result.Error(new IOException("Error registering", e)))
                );
            }
        }).start();
    }

    public void logout() {
        // Not used for register
    }
}

