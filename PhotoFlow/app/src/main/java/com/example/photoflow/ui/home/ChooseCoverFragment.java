package com.example.photoflow.ui.home;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.photoflow.data.util.ImageUtils;
import com.example.photoflow.databinding.FragmentGalleryBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.example.photoflow.data.FileRepository;
import com.example.photoflow.ui.gallery.GalleryAdapter;
import com.example.photoflow.ui.home.AddAlbumActivity;

public class ChooseCoverFragment extends Fragment {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private FileRepository fileRepository;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        Toast.makeText(getContext(), "Choose image cover", Toast.LENGTH_SHORT).show();
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
                        progressBar.setVisibility(View.VISIBLE);
                        new AsyncTask<PhotoItem, Void, String>() {
                            @Override
                            protected String doInBackground(PhotoItem... items) {
                                return ImageUtils.saveBitmapToCache(items[0].getBitmap(), getContext(), "cover_image.png");
                            }

                            @Override
                            protected void onPostExecute(String cachedImagePath) {
                                progressBar.setVisibility(View.GONE);
                                if (cachedImagePath != null) {
                                    Intent intent = new Intent(requireContext(), AddAlbumActivity.class);
                                    intent.putExtra("imagePath", cachedImagePath);
                                    intent.putExtra("photoId", item.getId());
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getContext(), "Failed to save image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }.execute(item);
                    });


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
