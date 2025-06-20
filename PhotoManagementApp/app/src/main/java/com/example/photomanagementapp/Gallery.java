package com.example.photomanagementapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photomanagementapp.databinding.ActivityGalleryBinding;

import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {

    private ActivityGalleryBinding binding;
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("Gallery", "onCreate called");
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        String albumName = getIntent().getStringExtra("albumName");
        binding.toolbarLayout.setTitle(albumName != null ? albumName : "Gallery");

        recyclerView = findViewById(R.id.recyclerViewPhotos);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        List<Integer> photoResIds = getIntent().getIntegerArrayListExtra("photos");
        if (photoResIds == null) {
            photoResIds = new ArrayList<>();
        }

        photoAdapter = new PhotoAdapter(photoResIds, photoResId -> {
            Intent intent = new Intent(Gallery.this, PhotoDetailActivity.class);
            intent.putExtra(PhotoDetailActivity.EXTRA_PHOTO_RES_ID, photoResId);

            // Dummy metadata (replace with real data later)
            intent.putExtra(PhotoDetailActivity.EXTRA_DESC, "This is a beautiful photo.");
            intent.putExtra(PhotoDetailActivity.EXTRA_TAGS, "#nature #sunset");

            startActivity(intent);
        });

        recyclerView.setAdapter(photoAdapter);
    }
}
