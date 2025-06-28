package com.example.photoflow.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.photoflow.data.model.LoggedInUser;
import com.example.photoflow.data.util.TokenManager;

import org.json.JSONArray;
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
public class FileDataSource {

    private static final String TAG = "FileDataSource";
    private Context context;

    public interface FileCallback {
        void onSuccess(Result<Boolean> result);
        void onError(Result.Error error);
    }

    public FileDataSource(Context context) {
        this.context = context.getApplicationContext(); // Use app context to avoid leaks
    }

    public void upload(String base64EncodedFile, String file_name, String title, String description, String coordinates, FileCallback callback) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting file upload request...");

                //URL url = new URL("http://ec2-13-60-9-150.eu-north-1.compute.amazonaws.com:8000/file/upload");
                URL url = new URL("http://192.168.0.145:8000/file/upload");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONArray filesArray = new JSONArray();
                JSONObject fileObject = new JSONObject();
                fileObject.put("file", base64EncodedFile);

                JSONObject metadata = new JSONObject();
                metadata.put("file_name", file_name);
                metadata.put("title", title);
                metadata.put("description", description);
                metadata.put("coordinates", coordinates);

                fileObject.put("metadata", metadata);
                JSONArray tagsArray = new JSONArray();
                tagsArray.put("tag1");
                tagsArray.put("tag2");
                fileObject.put("tags", tagsArray);

                filesArray.put(fileObject);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("token", TokenManager.loadToken(context));
                Log.e("token", TokenManager.loadToken(context));
                jsonParam.put("Files", filesArray);


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

                    // Post result on main thread
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(new Result.Success<>(true)));

                } else {
                    Log.e(TAG, "Upload failed. HTTP code: " + responseCode);
                    new Handler(Looper.getMainLooper()).post(() ->
                            callback.onError(new Result.Error(new IOException("Upload failed. Code: " + responseCode))));
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception during upload", e);
                new Handler(Looper.getMainLooper()).post(() ->
                        callback.onError(new Result.Error(new IOException("Error uploading a file", e))));
            }
        }).start();
    }

}

