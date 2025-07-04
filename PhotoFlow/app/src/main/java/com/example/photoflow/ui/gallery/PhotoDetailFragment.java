package com.example.photoflow.ui.gallery;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.photoflow.R;
import com.example.photoflow.data.FileDataSource;
import com.example.photoflow.data.FileRepository;
import com.example.photoflow.data.Result;
import com.example.photoflow.data.model.PhotoItem;

public class PhotoDetailFragment extends Fragment {

    private PhotoItem photoItem;
    private Context context;

    public PhotoDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = requireContext();
        return inflater.inflate(R.layout.fragment_photo_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            photoItem = (PhotoItem) getArguments().getSerializable("photoItem");

            ImageView photoView = view.findViewById(R.id.detailImageView);
            TextView fileNameView = view.findViewById(R.id.detailFileName);
            TextView createdAtView = view.findViewById(R.id.detailCreatedAt);
            TextView tagsView = view.findViewById(R.id.detailTags);
            ImageButton deleteButton = view.findViewById(R.id.deleteButton);

            if (photoItem != null) {
                if (photoItem.getBitmap() != null) {
                    photoView.setImageBitmap(photoItem.getBitmap());
                }
                fileNameView.setText("Title: " + photoItem.getFileName());
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

                deleteButton.setOnClickListener(v -> {
                    long photoId = photoItem.getId();

                    FileDataSource fileDataSource = new FileDataSource(context);
                    FileRepository fileRepository = FileRepository.getInstance(fileDataSource, context);
                    fileRepository.deleteFile(photoId, new FileDataSource.FileCallback<Boolean>() {
                        @Override
                        public void onSuccess(Result<Boolean> result) {
                            if (result instanceof Result.Success) {
                                Boolean success = ((Result.Success<Boolean>) result).getData();
                                if (success) {
                                    requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(context, "Photo deleted successfully", Toast.LENGTH_SHORT).show();
                                        requireActivity().getSupportFragmentManager().popBackStack();
                                    });
                                } else {
                                    requireActivity().runOnUiThread(() -> {
                                        Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            } else {
                                // This case probably won't happen since success callback should only be Success
                                requireActivity().runOnUiThread(() -> {
                                    Toast.makeText(context, "Unknown result type", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }

                        @Override
                        public void onError(Result.Error error) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(context, "Error deleting file: " + error.getError().getMessage(), Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                });

            }
        }
    }
}

