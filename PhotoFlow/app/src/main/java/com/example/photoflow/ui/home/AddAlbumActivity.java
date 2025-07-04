package com.example.photoflow.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoflow.MainActivity;
import com.example.photoflow.data.AlbumDataSource;
import com.example.photoflow.data.AlbumRepository;
import com.example.photoflow.data.FileDataSource;
import com.example.photoflow.data.model.AlbumItem;
import com.example.photoflow.data.model.PhotoItem;
import com.example.photoflow.data.util.TokenManager;
import com.example.photoflow.databinding.ActivityAddAlbumBinding;

import android.widget.ImageView;

public class AddAlbumActivity extends AppCompatActivity {

    private AddAlbumViewModel addAlbumViewModel;
    private ActivityAddAlbumBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String token = TokenManager.loadToken(this);

        binding = ActivityAddAlbumBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        String imagePath = getIntent().getStringExtra("imagePath");
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            binding.imageView.setImageBitmap(bitmap);
        }

        Long photoId = getIntent().getLongExtra("photoId", -1);


        addAlbumViewModel = new ViewModelProvider(this,
                new AddAlbumViewModelFactory(getApplicationContext()))
                .get(AddAlbumViewModel.class);

        final ImageView imageView = binding.imageView;
        final EditText albumTitle = binding.albumTitle;
        final Button saveButton = binding.saveButton;
        final ProgressBar progressBar = binding.progressBar;

        addAlbumViewModel.getFormState().observe(this, new Observer<AddAlbumFormState>() {
            @Override
            public void onChanged(AddAlbumFormState formState) {
                if (formState == null) {
                    return;
                }
                saveButton.setEnabled(formState.isDataValid());
                if (formState.getTitleError() != null) {
                    albumTitle.setError(getString(formState.getTitleError()));
                } else {
                    albumTitle.setError(null);
                }
            }

        });

        addAlbumViewModel.getAddAlbumResult().observe(this, new Observer<AddAlbumResult>() {
            @Override
            public void onChanged(AddAlbumResult result) {
                if (result == null) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                if (result.getError() != null) {
                    Toast.makeText(AddAlbumActivity.this, result.getError(), Toast.LENGTH_SHORT).show();
                } else if (result.getSuccess() != null) {
                    updateUiWithUser();
                }
                finish();
            }
        });

        TextWatcher afterTextChanged = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addAlbumViewModel.dataChanged(albumTitle.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        };

        albumTitle.addTextChangedListener(afterTextChanged);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = albumTitle.getText().toString();
                progressBar.setVisibility(View.VISIBLE);
                addAlbumViewModel.addAlbum(title, photoId);
            }
        });

    }

    private void updateUiWithUser() {
        Toast.makeText(getApplicationContext(), "Album added successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AddAlbumActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}