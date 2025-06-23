package com.example.photoflow.data;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.example.photoflow.data.model.FileDownloadItem;
import com.example.photoflow.data.model.FileUploadItem;
import com.example.photoflow.data.util.TokenManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FileDataSource {

    private static final String TAG = "FileDataSource";
    private static final String BASE_URL = "http://ec2-13-60-9-150.eu-north-1.compute.amazonaws.com:8000";
    private final Context context;

    public FileDataSource(Context context) {
        this.context = context.getApplicationContext();
    }

    public interface FileUploadCallback {
        void onSuccess();
        void onError(Exception e);
    }

    public void uploadFile(List<FileUploadItem> files, FileUploadCallback callback) {
        new Thread(() -> {
            try {
                String token = TokenManager.loadToken(context);
                if (token == null) throw new IOException("No token found");

                JSONArray filesArray = new JSONArray();
                for (FileUploadItem item : files) {
                    JSONObject fileJson = new JSONObject();
                    fileJson.put("file", Base64.encodeToString(item.data, Base64.NO_WRAP));
                    fileJson.put("metadata", item.metadata); // Already JSONObject
                    fileJson.put("tags", new JSONArray(item.tags));
                    filesArray.put(fileJson);
                }

                JSONObject payload = new JSONObject();
                payload.put("token", token);    // <-- add token here
                payload.put("Files", filesArray); // <-- uppercase F

                URL url = new URL(BASE_URL + "/file/upload");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                // Remove Authorization header if backend expects token only in JSON body
                // conn.setRequestProperty("Authorization", token);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(payload.toString().getBytes("UTF-8"));
                os.close();

                int code = conn.getResponseCode();
                Log.d(TAG, "Upload response code: " + code);
                if (code == 200) {
                    callback.onSuccess();
                } else {
                    callback.onError(new IOException("Server returned " + code));
                }

            } catch (Exception e) {
                Log.e(TAG, "Upload error", e);
                callback.onError(e);
            }
        }).start();
    }


    public interface FileDownloadCallback {
        void onSuccess(List<FileDownloadItem> files);
        void onError(Exception e);
    }

    public void downloadFiles(List<Long> fileIds, FileDownloadCallback callback) {
        new Thread(() -> {
            try {
                String token = TokenManager.loadToken(context);
                if (token == null) throw new IOException("No token found");

                JSONObject payload = new JSONObject();
                payload.put("token", token);
                JSONArray ids = new JSONArray();
                for (Long id : fileIds) ids.put(id);
                payload.put("file_ids", ids);

                URL url = new URL(BASE_URL + "/file/download");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(payload.toString().getBytes("UTF-8"));
                os.close();

                int code = conn.getResponseCode();
                if (code != 200) throw new IOException("Failed download: " + code);

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) response.append(line);
                in.close();

                JSONObject res = new JSONObject(response.toString());
                JSONArray filesJson = res.getJSONArray("files");

                List<FileDownloadItem> fileList = new java.util.ArrayList<>();
                for (int i = 0; i < filesJson.length(); i++) {
                    JSONObject f = filesJson.getJSONObject(i);
                    long id = f.getLong("id");
                    String name = f.getString("fileName");
                    byte[] data = Base64.decode(f.getString("file"), Base64.NO_WRAP);
                    fileList.add(new FileDownloadItem(id, name, data));
                }

                callback.onSuccess(fileList);

            } catch (Exception e) {
                Log.e(TAG, "Download error", e);
                callback.onError(e);
            }
        }).start();
    }

}

