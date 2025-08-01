package com.example.photoflow.ui.gallery;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoflow.R;
import com.example.photoflow.data.FileDataSource;
import com.example.photoflow.data.Result;
import com.example.photoflow.data.model.PhotoItem;
import com.example.photoflow.data.model.TagItem;
import com.example.photoflow.databinding.FragmentGalleryBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.example.photoflow.data.FileRepository;

public class GalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private FileRepository fileRepository;
    private ProgressBar progressBar;
    private Spinner tagFilterSpinner;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = root.findViewById(R.id.galleryRecyclerView);
        tagFilterSpinner = root.findViewById(R.id.tagFilterSpinner);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        progressBar = root.findViewById(R.id.loading);

        FileDataSource fileDataSource = new FileDataSource(requireContext());
        fileRepository = FileRepository.getInstance(fileDataSource, requireContext());

        progressBar.setVisibility(View.VISIBLE);

        fileRepository.getTags(new FileDataSource.FileCallback<List<TagItem>>() {
            @Override
            public void onSuccess(Result<List<TagItem>> result) {
                if (result instanceof Result.Success) {
                    List<TagItem> tagItems = ((Result.Success<List<TagItem>>) result).getData();

                    List<String> tagNames = new ArrayList<>();
                    tagNames.add("All"); // Add default option
                    for (TagItem tag : tagItems) {
                        tagNames.add(tag.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            tagNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    tagFilterSpinner.setAdapter(adapter);

                    // Load photos initially (All)
                    loadPhotos(null);

                    // Set tag change listener
                    tagFilterSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position,
                                long id) {
                            String selectedTag = tagNames.get(position);
                            if (selectedTag.equals("All")) {
                                loadPhotos(null);
                            } else {
                                loadPhotos(selectedTag);
                            }
                        }

                        @Override
                        public void onNothingSelected(android.widget.AdapterView<?> parent) {
                            // Optional
                        }
                    });
                }
            }

            @Override
            public void onError(Result.Error error) {
                Log.e("Gallery", "Failed to load tags", error.getError());
            }
        });

        return root;
    }

    private void loadPhotos(String tagName) {
        progressBar.setVisibility(View.VISIBLE);

        FileDataSource.FileCallback<List<PhotoItem>> callback = new FileDataSource.FileCallback<List<PhotoItem>>() {
            @Override
            public void onSuccess(Result<List<PhotoItem>> result) {
                if (result instanceof Result.Success) {
                    List<PhotoItem> photoItems = ((Result.Success<List<PhotoItem>>) result).getData();
                    galleryAdapter = new GalleryAdapter(photoItems, item -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("photoItem", item);
                        NavController navController = Navigation.findNavController(requireActivity(),
                                R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.nav_photo_detail, bundle);
                    }, false);
                    recyclerView.setAdapter(galleryAdapter);
                } else {
                    Log.e("Gallery", "Expected Success but got different result");
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Result.Error error) {
                Log.e("Gallery", "Failed to get images", error.getError());
                progressBar.setVisibility(View.GONE);
            }
        };

        if (tagName == null) {
            fileRepository.getPhotoItems(callback);
        } else {
            fileRepository.getPhotoItemsByTag(tagName, callback);
        }
    }

}
