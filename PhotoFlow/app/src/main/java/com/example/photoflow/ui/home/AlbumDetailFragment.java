package com.example.photoflow.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoflow.R;
import com.example.photoflow.data.model.PhotoItem;
import com.example.photoflow.ui.gallery.GalleryAdapter;

import java.util.ArrayList;

public class AlbumDetailFragment extends Fragment {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private ProgressBar progressBar;
    private View emptyView;
    private Button buttonAddPhoto;
    private long albumId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_album_detail, container, false);

        recyclerView = root.findViewById(R.id.galleryRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        progressBar = root.findViewById(R.id.loading);
        emptyView = root.findViewById(R.id.emptyView);
        buttonAddPhoto = root.findViewById(R.id.buttonAddPhoto);

        progressBar.setVisibility(View.VISIBLE);

        Bundle args = getArguments();
        if (args != null) {
            ArrayList<PhotoItem> photoItems = (ArrayList<PhotoItem>) args.getSerializable("photoItems");
            albumId = args.getLong("albumId", -1);
            Log.d("AlbumDetailFragment", "Album ID: " + albumId);

            if (photoItems != null && !photoItems.isEmpty()) {
                buttonAddPhoto.setVisibility(View.GONE);
                emptyView.setVisibility(View.GONE);

                galleryAdapter = new GalleryAdapter(photoItems, item -> {
                    if (item == null) {
                        // Null means "Add Photo" button clicked
                        openChoosePhotoFragment();
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("photoItem", item);
                        NavController navController = Navigation.findNavController(requireActivity(),
                                R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.nav_photo_detail, bundle);
                    }
                }, true); // Show "Add Photo" button

                recyclerView.setAdapter(galleryAdapter);
            } else {
                // Empty album, show centered Add Photo button and message
                emptyView.setVisibility(View.VISIBLE);
                buttonAddPhoto.setVisibility(View.VISIBLE);

                buttonAddPhoto.setOnClickListener(v -> openChoosePhotoFragment());
            }

            progressBar.setVisibility(View.GONE);
        }

        return root;
    }

    private void openChoosePhotoFragment() {
        Bundle bundle = new Bundle();
        bundle.putLong("albumId", albumId);
        NavController navController = Navigation.findNavController(requireActivity(),
                R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.nav_choose_photo_fragment, bundle);
    }
}

