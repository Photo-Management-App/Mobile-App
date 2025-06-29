package com.example.photoflow.ui.gallery;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.photoflow.R;

public class PhotoDetailFragment extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_DESCRIPTION = "extra_description";
    public static final String EXTRA_COORDINATES = "extra_coordinates";
    public static final String EXTRA_CREATED_AT = "extra_created_at";
    public static final String EXTRA_BITMAP = "extra_bitmap";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        ImageView photoView = findViewById(R.id.detailImageView);
        TextView titleView = findViewById(R.id.detailTitle);
        TextView descriptionView = findViewById(R.id.detailDescription);
        TextView coordinatesView = findViewById(R.id.detailCoordinates);
        TextView createdAtView = findViewById(R.id.detailCreatedAt);

        Bitmap bitmap = getIntent().getParcelableExtra(EXTRA_BITMAP);
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String description = getIntent().getStringExtra(EXTRA_DESCRIPTION);
        String coordinates = getIntent().getStringExtra(EXTRA_COORDINATES);
        String createdAt = getIntent().getStringExtra(EXTRA_CREATED_AT);

        photoView.setImageBitmap(bitmap);
        titleView.setText("Title: " + title);
        descriptionView.setText("Description: " + description);
        coordinatesView.setText("Coordinates: " + coordinates);
        createdAtView.setText("Created At: " + createdAt);
    }


}
