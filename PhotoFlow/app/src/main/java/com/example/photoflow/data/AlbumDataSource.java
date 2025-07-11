package com.example.photoflow.data;

import static com.example.photoflow.data.util.ImageUtils.decodeBase64ToBitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.util.Log;

import com.example.photoflow.R;
import com.example.photoflow.data.model.AlbumItem;
import com.example.photoflow.data.model.PhotoItem;
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
import java.util.List;

import android.os.Handler;

public class AlbumDataSource {

    private static final String TAG = "AlbumDataSource";
    private Context context;
    String baseUrl;
    private JSONArray fileIds;
    private FileRepository fileRepository;
    public interface AlbumCallback<T> {
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

    public void getAlbumItems(AlbumCallback<List<AlbumItem>> callback) {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting album items request...");
                URL url = new URL(baseUrl + "/album/list");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("token", TokenManager.loadToken(context));
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

                    List<AlbumItem> albumItems = new ArrayList<>();

                    Log.d(TAG, "Response body: " + response);
                    JSONObject json = new JSONObject(response.toString());
                    JSONArray albumsArray = json.getJSONArray("albums");
                    JSONArray coversArray = json.getJSONArray("album_cover");

                    for (int i = 0; i < albumsArray.length(); i++) {
                        JSONObject albumJson = albumsArray.getJSONObject(i);
                        JSONObject coverJson = coversArray.getJSONObject(i);

                        long id = albumJson.getLong("id");
                        String title = albumJson.getString("title");

                        Bitmap coverBitmap = null;
                        if (coverJson.has("file") && !coverJson.isNull("file")) {
                            coverBitmap = decodeBase64ToBitmap(coverJson.getString("file"));
                        }

                        AlbumItem albumItem = new AlbumItem(id, title, coverBitmap);
                        albumItems.add(albumItem);
                    }

                    // Post success callback on main thread:
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onSuccess(new Result.Success<>(albumItems));
                    });

                } else {
                    // HTTP error
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onError(new IOException("Failed with HTTP code: " + responseCode));
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Error getting album items", e);
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onError(e);
                });
            }
        }).start();
    }

    public void addPhotoToAlbum(){

    }

    public void getPhotoItems(long albumId, AlbumCallback<List<PhotoItem>> callback) {
    new Thread(() -> {
        try {
            Log.d(TAG, "Starting photo items request...");
            URL url = new URL(baseUrl + "/album/getFile");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("token", TokenManager.loadToken(context));
            jsonParam.put("album_id", albumId);
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
                JSONArray jsonArray = new JSONArray(response.toString());
                List<Long> fileIds = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    fileIds.add(jsonArray.getLong(i));
                }

                List<PhotoItem> photoItems = new ArrayList<>();
                int totalFiles = fileIds.size();
                if (totalFiles == 0) {
                    new Handler(Looper.getMainLooper())
                            .post(() -> callback.onSuccess(new Result.Success<>(photoItems)));
                    return;
                }

                final int[] completedCount = {0};

//                for (Long fileId : fileIds) {
//                    downloadFiles(fileId, new FileCallback<Bitmap>() {
//                        @Override
//                        public void onSuccess(Result<Bitmap> result) {
//                            synchronized (photoItems) {
//                                if (result instanceof Result.Success) {
//                                    Bitmap bitmap = ((Result.Success<Bitmap>) result).getData();
//                                    // You can optionally fetch metadata (like title/createdAt) separately
//                                    PhotoItem item = new PhotoItem(fileId, bitmap, "File #" + fileId, "", new String[]{});
//                                    photoItems.add(item);
//                                    Log.d(TAG, "Downloaded file with ID: " + fileId);
//                                }
//                                completedCount[0]++;
//                                if (completedCount[0] == totalFiles) {
//                                    new Handler(Looper.getMainLooper())
//                                            .post(() -> callback.onSuccess(new Result.Success<>(photoItems)));
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onError(Result.Error error) {
//                            synchronized (photoItems) {
//                                Log.e(TAG, "Error downloading file with ID: " + fileId, error.getError());
//                                completedCount[0]++;
//                                if (completedCount[0] == totalFiles) {
//                                    new Handler(Looper.getMainLooper())
//                                            .post(() -> callback.onSuccess(new Result.Success<>(photoItems)));
//                                }
//                            }
//                        }
//                    });
//                }

            } else {
                Log.e(TAG, "Failed to get photo items, response code: " + responseCode);
                new Handler(Looper.getMainLooper()).post(() -> callback
                        .onError(new Result.Error(new IOException("Failed to get photo items. Code: " + responseCode)).getError()));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting photo items", e);
            new Handler(Looper.getMainLooper())
                    .post(() -> callback.onError(new Result.Error(new IOException("Error getting photo items", e)).getError()));
        }
    }).start();
}




}
