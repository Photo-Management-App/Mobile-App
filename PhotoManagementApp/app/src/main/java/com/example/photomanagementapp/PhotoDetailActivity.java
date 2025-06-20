package com.example.photomanagementapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PhotoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PHOTO_RES_ID = "photoResId";
    public static final String EXTRA_DESC = "description";
    public static final String EXTRA_TAGS = "tags";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        ImageView photoView = findViewById(R.id.imageViewDetail);
        TextView descView = findViewById(R.id.textViewDescription);
        TextView tagsView = findViewById(R.id.textViewTags);

        int resId = getIntent().getIntExtra(EXTRA_PHOTO_RES_ID, 0);
        String desc = getIntent().getStringExtra(EXTRA_DESC);
        String tags = getIntent().getStringExtra(EXTRA_TAGS);

        if (resId != 0) {
            photoView.setImageResource(resId);
        }
        descView.setText(desc != null ? desc : "No description");
        tagsView.setText(tags != null ? "Tags: " + tags : "No tags");
    }
}
