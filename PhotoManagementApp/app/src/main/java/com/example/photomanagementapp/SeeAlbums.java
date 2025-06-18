package com.example.photomanagementapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photomanagementapp.databinding.ActivitySeeAlbumsBinding;

import java.util.ArrayList;
import java.util.List;

public class SeeAlbums extends AppCompatActivity {

    private ActivitySeeAlbumsBinding binding;
    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;

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

        // Load sample images from drawable
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.screenshot);
        imageList.add(R.drawable.dragon);
        imageList.add(R.drawable.rect77);
        imageList.add(R.drawable.donut);
        Log.d("SeeAlbums", "Image list size: " + imageList.size());
        Log.e("works2","works2");


        photoAdapter = new PhotoAdapter(imageList);
        recyclerView.setAdapter(photoAdapter);
    }
}
