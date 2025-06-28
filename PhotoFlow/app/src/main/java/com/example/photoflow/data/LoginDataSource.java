package com.example.photoflow.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.photoflow.data.model.LoggedInUser;
import com.example.photoflow.data.util.TokenManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private static final String TAG = "LoginDataSource";
    private Context context;

    public interface LoginCallback {
        void onSuccess(Result<LoggedInUser> result);
        void onError(Result.Error error);
    }

    public LoginDataSource(Context context) {
        this.context = context.getApplicationContext(); // Use app context to avoid leaks
    }

    public void login(String username, String password, LoginCallback callback) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting login request...");

                //URL url = new URL("http://ec2-13-60-9-150.eu-north-1.compute.amazonaws.com:8000/login");
                URL url = new URL("http://192.168.0.145:8000/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("login", username);
                jsonParam.put("password", password);

                Log.d(TAG, "Sending JSON: " + jsonParam.toString());

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "HTTP response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    Log.d(TAG, "Response body: " + response);

                    JSONObject json = new JSONObject(response.toString());
                    String token = json.getString("token");
                    String email = json.getString("email");

                    Log.d(TAG, "Login successful. Token: " + token + ", Email: " + email);

                    // Save token
                    TokenManager.saveToken(context, token);

                    LoggedInUser realUser = new LoggedInUser(token, email);

                    // Post result on main thread
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(new Result.Success<>(realUser)));

                } else {
                    Log.e(TAG, "Login failed. HTTP code: " + responseCode);
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onError(new Result.Error(new IOException("Login failed. Code: " + responseCode))));
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception during login", e);
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError(new Result.Error(new IOException("Error logging in", e))));
            }
        }).start();
    }

    public void logout() {
        Log.d(TAG, "Logout called.");
        TokenManager.clearToken(context);
    }
}

