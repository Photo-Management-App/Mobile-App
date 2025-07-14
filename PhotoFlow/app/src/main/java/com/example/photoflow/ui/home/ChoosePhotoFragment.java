package com.example.photoflow.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoflow.R;
import com.example.photoflow.data.AlbumDataSource;
import com.example.photoflow.data.AlbumRepository;
import com.example.photoflow.data.FileDataSource;
import com.example.photoflow.data.FileRepository;
import com.example.photoflow.data.Result;
import com.example.photoflow.data.model.PhotoItem;
import com.example.photoflow.ui.gallery.GalleryAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChoosePhotoFragment extends Fragment {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private FileRepository fileRepository;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        recyclerView = root.findViewById(R.id.galleryRecyclerView);
        progressBar = root.findViewById(R.id.loading);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        galleryAdapter = new GalleryAdapter(new ArrayList<>(), item -> {}, false);
        recyclerView.setAdapter(galleryAdapter);

        Bundle args = getArguments();
        long albumId = args != null ? args.getLong("albumId", -1) : -1;

        if (albumId == -1) {
            Toast.makeText(getContext(), "Invalid album ID", Toast.LENGTH_SHORT).show();
            return root;
        }

        FileDataSource fileDataSource = new FileDataSource(requireContext());
        fileRepository = FileRepository.getInstance(fileDataSource, requireContext());
        AlbumRepository albumRepository = AlbumRepository.getInstance(requireContext());

        progressBar.setVisibility(View.VISIBLE);

        fileRepository.getPhotoItems(new FileDataSource.FileCallback<List<PhotoItem>>() {
            @Override
            public void onSuccess(Result<List<PhotoItem>> result) {
                if (result instanceof Result.Success) {
                    List<PhotoItem> photoItems = ((Result.Success<List<PhotoItem>>) result).getData();
                    galleryAdapter = new GalleryAdapter(photoItems, item -> {
                        long photoId = item.getId();
                        progressBar.setVisibility(View.VISIBLE);

                        albumRepository.addPhotoToAlbum(photoId, albumId, new AlbumDataSource.AlbumCallback<Boolean>() {
                            @Override
                            public void onSuccess(Result<Boolean> result) {
                                progressBar.setVisibility(View.GONE);
                                if (result instanceof Result.Success && ((Result.Success<Boolean>) result).getData()) {
                                    Toast.makeText(getContext(), "Photo added to album!", Toast.LENGTH_SHORT).show();
                                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                                    navController.popBackStack();
                                } else {
                                    Log.e("ChoosePhoto", "Unexpected or unsuccessful result type");
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                progressBar.setVisibility(View.GONE);
                                Log.e("ChoosePhoto", "Failed to add photo to album", e);
                                Toast.makeText(getContext(), "Failed to add photo", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }, false);

                    recyclerView.setAdapter(galleryAdapter); // set new adapter with click listener
                } else {
                    Log.e("ChoosePhoto", "Expected Success but got different result");
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Result.Error error) {
                progressBar.setVisibility(View.GONE);
                Log.e("ChoosePhoto", "Failed to get images", error.getError());
                Toast.makeText(getContext(), "Failed to load photos", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}

