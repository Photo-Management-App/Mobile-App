package com.example.photoflow.data;

import android.content.Context;
import android.os.Looper;
import android.os.Handler;
import android.util.Log;

import com.example.photoflow.R;
import com.example.photoflow.data.util.TokenManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class ShareDataSource {

    private static final String TAG = "ShareFileSource";
    private Context context;
    String baseUrl;

    public interface ShareCallback<T> {

        void onSuccess(Result<T> result);
        void onError(Exception e); 
        
    }

    public ShareDataSource(Context context) {
        this.context = context.getApplicationContext();
        baseUrl = context.getString(R.string.base_url);
    }

    public void shareFile(long photoId, ShareCallback<String> callback) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting share request ...");
                URL url = new URL(baseUrl + "/file/share/add");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                String token = TokenManager.loadToken(context);
                Log.e("token", token);
                jsonParam.put("token", token);
                jsonParam.put("file_id", photoId);
                jsonParam.put("max_uses", 5);

                Log.d(TAG, "Sending JSON: " + jsonParam.toString());

                // ✅ Write JSON to request body
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "HTTP response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();
                    Log.d(TAG, "Response: " + response.toString());
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String shareUrl = jsonResponse.getString("url");
                    callback.onSuccess(new Result.Success<>(shareUrl));

                } else {
                    Log.e(TAG, "Failed to share a file, response code: " + responseCode);
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onError(new Result.Error(new IOException("Adding failed. Code: " + responseCode)).getError()));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error sharing file: ", e);
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError(new Result.Error(new IOException("Error sharing file: " + e.getMessage())).getError()));
            }
        }).start();
    }





}
