package com.example.photoflow.ui.upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import com.example.photoflow.MainActivity;
import com.example.photoflow.R;
import com.example.photoflow.data.util.TokenManager;
import com.example.photoflow.databinding.ActivityUploadFileBinding;

public class FileUploadActivity extends AppCompatActivity {

    private FileUploadViewModel uploadViewModel;
    private ActivityUploadFileBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        String token = TokenManager.loadToken(this);

        binding = ActivityUploadFileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String imagePath = getIntent().getStringExtra("imagePath");
        String coordinates = getIntent().getStringExtra("coords");
        Log.d("Coordinates", "Received coordinates: " + coordinates);
        String base64EncodedFile = null;

        if (imagePath != null) {
            File imageFile = new File(imagePath);
            try {
                FileInputStream fis = new FileInputStream(imageFile);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                fis.close();

                byte[] imageBytes = bos.toByteArray();
                base64EncodedFile = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                binding.imageView.setImageBitmap(bitmap);

                // Optionally delete the temporary file
                imageFile.delete();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to read image file", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            Toast.makeText(this, "No image provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        uploadViewModel = new ViewModelProvider(this,
                new FileUploadViewModelFactory(base64EncodedFile, getApplicationContext()))
                .get(FileUploadViewModel.class);

        final ImageView imageView = binding.imageView;
        final EditText fileNameEditText = binding.fileName;
        final EditText descriptionEditText = binding.description;
        final EditText tagsEditText = binding.tags;
        final Button uploadButton = binding.uploadButton;
        final ProgressBar loadingProgressBar = binding.loading;

        // Set the image from the intent

        uploadViewModel.getFormState().observe(this, new Observer<FileUploadFormState>() {
            @Override
            public void onChanged(FileUploadFormState formState) {
                if (formState == null) {
                    return;
                }
                uploadButton.setEnabled(formState.isDataValid());
                if (formState.getFileNameError() != null) {
                    fileNameEditText.setError(getString(formState.getFileNameError()));
                }
                if( formState.getTagsError() != null) {
                    tagsEditText.setError(getString(formState.getTagsError()));
                }
            }
        });

        uploadViewModel.getUploadResult().observe(this, new Observer<FileUploadResult>() {
            @Override
            public void onChanged(@Nullable FileUploadResult uploadResult) {
                if (uploadResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (uploadResult.getError() != null) {
                    Toast.makeText(FileUploadActivity.this, uploadResult.getError(), Toast.LENGTH_SHORT).show();
                } else if (uploadResult.getSuccess() != null) {
                    updateUiWithUser();
                }
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                uploadViewModel.uploadDataChanged(
                        fileNameEditText.getText().toString(), tagsEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        };

        fileNameEditText.addTextChangedListener(afterTextChangedListener);
        descriptionEditText.addTextChangedListener(afterTextChangedListener);
        tagsEditText.addTextChangedListener(afterTextChangedListener); 

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                uploadViewModel.upload(
                        fileNameEditText.getText().toString(),
                        descriptionEditText.getText().toString(), tagsEditText.getText().toString(), coordinates);
            }
        });

    }

    private void updateUiWithUser() {
        Toast.makeText(getApplicationContext(), "Upload successful", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(FileUploadActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
