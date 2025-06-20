package com.example.photomanagementapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photomanagementapp.databinding.ActivitySeeAlbumsBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SeeAlbums extends AppCompatActivity {

    private ActivitySeeAlbumsBinding binding;
    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("works","works");
        super.onCreate(savedInstanceState);
        binding = ActivitySeeAlbumsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        binding.toolbarLayout.setTitle(getTitle());

        recyclerView = findViewById(R.id.recyclerViewPhotos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<Album> albums = getSampleAlbums();

        albumAdapter = new AlbumAdapter(albums, album -> {
            Intent intent = new Intent(SeeAlbums.this, Gallery.class);
            intent.putIntegerArrayListExtra("photos", new ArrayList<>(album.getPhotoResIds()));
            intent.putExtra("albumName", album.getName());
            startActivity(intent);
        });

        recyclerView.setAdapter(albumAdapter);
        binding.fab.setOnClickListener(v -> {
            addAlbumToServer("test");
        });

    }

    private List<Album> getSampleAlbums() {
        List<Album> albums = new ArrayList<>();
        List<Integer> photos1 = List.of(R.drawable.donut, R.drawable.dragon);
        List<Integer> photos2 = List.of(R.drawable.screenshot, R.drawable.rect77);

        albums.add(new Album("Nature", R.drawable.donut, photos1));
        albums.add(new Album("Art", R.drawable.screenshot, photos2));
        return albums;
    }
    private final String TOKEN = "YOUR_TOKEN_HERE"; // TODO: Replace with your auth token logic

    private void addAlbumToServer(String albumTitle) {
        new Thread(() -> {
            try {
                URL url = new URL("http://ec2-13-60-9-150.eu-north-1.compute.amazonaws.com:8000/album/add");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // ✅ Wysyłamy tylko album_title
                JSONObject json = new JSONObject();
                json.put("album_title", albumTitle);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = json.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int code = conn.getResponseCode();
                Log.d("AddAlbum", "Response code: " + code);

                if (code == 200) {
                    runOnUiThread(() -> Toast.makeText(this, "Album added", Toast.LENGTH_SHORT).show());
                    // Po dodaniu albumu — opcjonalny upload zdjęcia
                    //uploadFileToServer();
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    reader.close();
                    String errorMsg = errorResponse.toString();
                    Log.e("AddAlbum", "Error response: " + errorMsg);
                    runOnUiThread(() -> Toast.makeText(this, "Failed to add album: " + errorMsg, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                Log.e("AddAlbum", "Exception: ", e);
                runOnUiThread(() -> Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }


    private void uploadFileToServer() {
        new Thread(() -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.donut);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                String base64Image = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

                JSONObject metadata = new JSONObject();
                metadata.put("file_name", "donut.png");

                JSONObject fileObject = new JSONObject();
                fileObject.put("file", base64Image);
                fileObject.put("metadata", metadata);

                JSONArray filesArray = new JSONArray();
                filesArray.put(fileObject);

                JSONObject payload = new JSONObject();
                payload.put("token", TOKEN);
                payload.put("files", filesArray);

                URL url = new URL("http://ec2-13-60-9-150.eu-north-1.compute.amazonaws.com:8000/file/upload");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(payload.toString().getBytes("utf-8"));
                }

                int responseCode = conn.getResponseCode();
                Log.d("UploadFile", "Upload response: " + responseCode);
                if (responseCode == 200) {
                    runOnUiThread(() -> Toast.makeText(this, "File uploaded", Toast.LENGTH_SHORT).show());
                } else {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    reader.close();
                    String errorMsg = errorResponse.toString();
                    Log.e("UploadFile", "Error response: " + errorMsg);
                    runOnUiThread(() -> Toast.makeText(this, "File upload failed: " + errorMsg, Toast.LENGTH_LONG).show());
                }
            } catch (Exception e) {
                Log.e("UploadFile", "Exception: ", e);
                runOnUiThread(() -> Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }



}
