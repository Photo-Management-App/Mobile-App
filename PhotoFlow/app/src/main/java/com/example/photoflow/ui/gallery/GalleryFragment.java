package com.example.photoflow.ui.gallery;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = root.findViewById(R.id.galleryRecyclerView);

        // Grid with 2 columns
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        progressBar = root.findViewById(R.id.loading);
        // Call this when you're ready to trigger the download:
        FileDataSource fileDataSource = new FileDataSource(requireContext());
        fileRepository = FileRepository.getInstance(fileDataSource, requireContext());
        progressBar.setVisibility(View.VISIBLE);
        fileRepository.getPhotoItems(new FileDataSource.FileCallback<List<PhotoItem>>() {
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
                    // Handle unexpected case, e.g., log error or throw
                    Log.e("Gallery", "Expected Success but got different result");
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Result.Error error) {
                Log.e("Gallery", "Failed to get images", error.getError());
            }
        });

        return root;
    }

}
