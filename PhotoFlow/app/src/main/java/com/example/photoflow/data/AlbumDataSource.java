package com.example.photoflow.data;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.example.photoflow.R;
import com.example.photoflow.data.model.AlbumItem;
import com.example.photoflow.data.util.TokenManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import android.os.Handler;

public class AlbumDataSource {

    private static final String TAG = "AlbumDataSource";
    private Context context;
    String baseUrl;

    public interface AlbumCallback<T> {
        void onSuccess(Result<T> result);
        void onError(Exception e);
    }

    public interface FileCallback<T> {
        void onSuccess(Result<T> result);
        void onError(Exception e);        
    }

    public AlbumDataSource(Context context) {
        this.context = context.getApplicationContext(); // Use app context to avoid leaks
        baseUrl = context.getString(R.string.base_url);
    }

    public void addAlbum(String title, Long coverId, AlbumCallback<Boolean> callback){
        new Thread(() -> {
            try{
                Log.d(TAG, "Staring album add request...");
                URL url = new URL(baseUrl + "/album/add");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                Log.e("token", TokenManager.loadToken(context));
                jsonParam.put("token", TokenManager.loadToken(context));

                JSONObject albumJson = new JSONObject();
                albumJson.put("title", title);
                Log.d(TAG, "Cover ID: " + coverId);
                albumJson.put("cover_id", coverId);

                jsonParam.put("album_title", albumJson);

                Log.d(TAG, "Sending JSON: " + jsonParam.toString());
                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

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
                    callback.onSuccess(new Result.Success<>(true));
                } else {
                    Log.e(TAG, "Failed to add album, response code: " + responseCode);
                    new Handler(Looper.getMainLooper()).post(() -> callback
                            .onError(new Result.Error(new IOException("Adding failed. Code: " + responseCode)).getError()));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error adding album", e);
                new Handler(Looper.getMainLooper())
                        .post(() -> callback.onError(new Result.Error(new IOException("Error adding a file", e)).getError()));

            }
        }).start();
    }

}
