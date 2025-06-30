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
                createdAtView.setText("Created At: " + photoItem.getCreatedAt());
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
