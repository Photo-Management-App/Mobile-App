package com.example.photoflow.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.photoflow.R;
import com.example.photoflow.data.model.LoggedInUser;
import com.example.photoflow.data.model.PhotoItem;
import com.example.photoflow.data.util.ImageUtils;
import com.example.photoflow.data.util.TokenManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that handles authentication w/ login credentials and retrieves user
 * information.
 */
public class FileDataSource {

    private static final String TAG = "FileDataSource";
    private Context context;
    String baseUrl;
    private JSONArray fileList;
    private JSONArray fileIds = new JSONArray(); // Store file IDs for download

    public interface FileCallback<T> {
        void onSuccess(Result<T> result);

        void onError(Result.Error error);
    }

    public interface FileListCallback {
        void onSuccess(JSONArray fileList);

        void onError(Exception error);
    }

    public FileDataSource(Context context) {
        this.context = context.getApplicationContext(); // Use app context to avoid leaks
        baseUrl = context.getString(R.string.base_url);
    }

    public void upload(String base64EncodedFile, String file_name, String title, String description, String coordinates,
            String tags,
            FileCallback<Boolean> callback) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting file upload request...");
                URL url = new URL(baseUrl + "/file/upload");

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
                String[] tagItems = tags.split(",");

                for (String tag : tagItems) {
                    String trimmedTag = tag.trim();
                    if (!trimmedTag.isEmpty()) {
                        tagsArray.put(trimmedTag);
                    }
                }

                fileObject.put("tags", tagsArray);
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
                    new Handler(Looper.getMainLooper()).post(() -> callback
                            .onError(new Result.Error(new IOException("Upload failed. Code: " + responseCode))));
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception during upload", e);
                new Handler(Looper.getMainLooper())
                        .post(() -> callback.onError(new Result.Error(new IOException("Error uploading a file", e))));
            }
        }).start();
    }

    public void getPhotoItems(FileCallback<List<PhotoItem>> callback) {
        getFileList(new FileListCallback() {
            @Override
            public void onSuccess(JSONArray fileList) {
                List<PhotoItem> photoItems = new ArrayList<>();
                int totalFiles = fileList.length();
                if (totalFiles == 0) {
                    new Handler(Looper.getMainLooper())
                            .post(() -> callback.onSuccess(new Result.Success<>(photoItems)));
                    return;
                }

                final int[] completedCount = { 0 };

                try {
                    for (int i = 0; i < totalFiles; i++) {
                        JSONObject fileWrapper = fileList.getJSONObject(i);
                        JSONObject fileObject = fileWrapper.getJSONObject("file");
                        JSONArray tagsArray = fileWrapper.getJSONArray("tags");
                        long id = fileObject.getLong("id");

                        String[] tags = new String[tagsArray.length()];
                        for (int j = 0; j < tagsArray.length(); j++) {
                            tags[j] = tagsArray.getString(j);
                        }

                        downloadFiles(id, new FileCallback<Bitmap>() {
                            @Override
                            public void onSuccess(Result<Bitmap> result) {
                                synchronized (photoItems) {
                                    if (result instanceof Result.Success) {
                                        Bitmap bitmap = ((Result.Success<Bitmap>) result).getData();
                                        String title = fileObject.optString("file_name", "");
                                        String createdAt = fileObject.optString("created_at", "");
                                        Log.e("tags", Arrays.toString(tags));

                                        PhotoItem item = new PhotoItem(id, bitmap, title, createdAt, tags);
                                        photoItems.add(item);
                                        Log.d(TAG, "Downloaded file with ID: " + id + ", Title: " + title);
                                    }
                                    completedCount[0]++;
                                    if (completedCount[0] == totalFiles) {
                                        new Handler(Looper.getMainLooper())
                                                .post(() -> callback.onSuccess(new Result.Success<>(photoItems)));
                                    }
                                }
                            }

                            @Override
                            public void onError(Result.Error error) {
                                synchronized (photoItems) {
                                    Log.e(TAG, "Error downloading file with ID: " + id, error.getError());
                                    completedCount[0]++;
                                    if (completedCount[0] == totalFiles) {
                                        new Handler(Looper.getMainLooper())
                                                .post(() -> callback.onSuccess(new Result.Success<>(photoItems)));
                                    }
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing file list", e);
                    new Handler(Looper.getMainLooper())
                            .post(() -> callback
                                    .onError(new Result.Error(new IOException("Error parsing photo items", e))));
                }
            }

            @Override
            public void onError(Exception e) {
                callback.onError(new Result.Error(new IOException("Failed to fetch file list", e)));
            }
        });
    }

    private void downloadFiles(long id, FileCallback<Bitmap> callback) {

        new Thread(() -> {
            try {
                Log.d(TAG, "Starting file download request...");
                URL url = new URL(baseUrl + "/file/download");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("token", TokenManager.loadToken(context));
                JSONArray fileIds = new JSONArray();
                fileIds.put(id); // Add the specific file ID to download
                jsonParam.put("file_ids", fileIds);

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
                    Log.d(TAG, "Download response: " + response.toString());

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray filesArray = jsonResponse.getJSONArray("files");
                    if (filesArray.length() > 0) {
                        JSONObject fileObject = filesArray.getJSONObject(0);
                        String base64File = fileObject.getString("file");
                        Bitmap bitmap = ImageUtils.decodeBase64ToBitmap(base64File);
                        Log.d(TAG, "File parsed successfully. ID: " + id);

                        new Handler(Looper.getMainLooper())
                                .post(() -> callback.onSuccess(new Result.Success<>(bitmap)));
                    } else {
                        new Handler(Looper.getMainLooper())
                                .post(() -> callback.onError(new Result.Error(new IOException("No file data found."))));
                    }

                } else {
                    Log.e(TAG, "Download failed. HTTP code: " + responseCode);
                    new Handler(Looper.getMainLooper()).post(() -> callback
                            .onError(new Result.Error(new IOException("Download failed. Code: " + responseCode))));
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception during file download", e);
                new Handler(Looper.getMainLooper())
                        .post(() -> callback.onError(new Result.Error(new IOException("Error downloading files", e))));
            }
        }).start();

    }

    private void getFileList(FileListCallback callback) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting file list request...");
                URL url = new URL(baseUrl + "/file/list");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("token", TokenManager.loadToken(context));
                Log.e("token", TokenManager.loadToken(context));

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

                    // Process the response as needed
                    Log.d(TAG, "File list: " + response.toString());

                    JSONObject root = new JSONObject(response.toString());
                    JSONArray filesArray = root.getJSONArray("file");

                    for (int i = 0; i < filesArray.length(); i++) {
                        JSONObject fileWrapper = filesArray.getJSONObject(i);
                        JSONObject fileObject = fileWrapper.getJSONObject("file");
                        long id = fileObject.getLong("id");
                        String fileName = fileObject.getString("file_name");

                        Log.d("ParsedFile", "ID: " + id + ", Name: " + fileName);
                        fileIds.put(id); // Collect file IDs
                    }

                    fileList = filesArray; // Store the file list for later use
                    new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(fileList));

                } else {
                    Log.e(TAG, "File list request failed. HTTP code: " + responseCode);
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception during file list request", e);
            }
        }).start();
    }

    public void deleteFile(long fileId, FileCallback<Boolean> callback) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting file delete request...");
                URL url = new URL(baseUrl + "/file/delete");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("token", TokenManager.loadToken(context));
                jsonParam.put("file_id", fileId);

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

                    Log.d(TAG, "File deleted successfully: " + response.toString());

                    // Notify success with 'true'
                    if (callback != null) {
                        callback.onSuccess(new Result.Success<>(true));
                    }

                } else {
                    String errorMsg = "File delete failed. HTTP code: " + responseCode;
                    Log.e(TAG, errorMsg);

                    if (callback != null) {
                        callback.onError(new Result.Error(new Exception(errorMsg)));
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception during file delete", e);
                if (callback != null) {
                    callback.onError(new Result.Error(e));
                }
            }
        }).start();
    }

}
