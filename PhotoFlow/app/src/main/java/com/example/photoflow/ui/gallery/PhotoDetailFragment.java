package com.example.photoflow.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.photoflow.R;
import com.example.photoflow.data.model.PhotoItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class PhotoDetailFragment extends Fragment {

    private PhotoItem photoItem;

    public PhotoDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            photoItem = (PhotoItem) getArguments().getSerializable("photoItem");

            ImageView photoView = view.findViewById(R.id.detailImageView);
            TextView titleView = view.findViewById(R.id.detailTitle);
            TextView createdAtView = view.findViewById(R.id.detailCreatedAt);
            TextView tagsView = view.findViewById(R.id.detailTags);


            if (photoItem != null) {
                if (photoItem.getBitmap() != null) {
                    photoView.setImageBitmap(photoItem.getBitmap());
                }
                titleView.setText("Title: " + photoItem.getTitle());
                String isoDate = photoItem.getCreatedAt();

                try {
                    // Parse ISO 8601 string to Date object
                    SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                    isoFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC timezone because of 'Z'

                    Date date = isoFormat.parse(isoDate);

                    // Format Date to readable format
                    SimpleDateFormat readableFormat = new SimpleDateFormat("MMM dd, yyyy, h:mm a", Locale.getDefault());
                    readableFormat.setTimeZone(TimeZone.getDefault()); // Convert to local time zone

                    String readableDate = readableFormat.format(date);

                    createdAtView.setText("Created At: " + readableDate);

                } catch (ParseException e) {
                    e.printStackTrace();
                    createdAtView.setText("Created At: " + isoDate); // fallback to original
                }
                StringBuilder tagsBuilder = new StringBuilder("Tags: ");
                if (photoItem.getTags() != null && photoItem.getTags().length > 0) {
                    for (String tag : photoItem.getTags()) {
                        tagsBuilder.append(tag).append(", ");
                    }
                    // Remove the last comma and space
                    tagsBuilder.setLength(tagsBuilder.length() - 2);
                } else {
                    tagsBuilder.append("No tags available");
                }
                
                tagsView.setText(tagsBuilder.toString());

            }
        }
       };

}
