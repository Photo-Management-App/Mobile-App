package com.example.photoflow.ui.settings;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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

import com.example.photoflow.MainActivity;
import com.example.photoflow.R;
import com.example.photoflow.data.FileDataSource;
import com.example.photoflow.data.Result;
import com.example.photoflow.data.SettingsDataSource;
import com.example.photoflow.data.SettingsRepository;
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

public class ChoosePictureFragment extends Fragment {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private FileRepository fileRepository;
    private SettingsRepository settingsRepository;
    private ProgressBar progressBar;
    private SettingsViewModel settingsViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        Toast.makeText(getContext(), "Choose new profile picture", Toast.LENGTH_SHORT).show();
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        recyclerView = root.findViewById(R.id.galleryRecyclerView);

        // Grid with 2 columns
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        progressBar = root.findViewById(R.id.loading);
        // Call this when you're ready to trigger the download:
        FileDataSource fileDataSource = new FileDataSource(requireContext());
        fileRepository = FileRepository.getInstance(fileDataSource, requireContext());

        settingsRepository = SettingsRepository.getInstance(requireContext());
        SettingsDataSource DataSource = new SettingsDataSource(requireContext());
        progressBar.setVisibility(View.VISIBLE);

        settingsViewModel = new ViewModelProvider(
                requireActivity(),
                new SettingsViewModelFactory(requireContext())).get(SettingsViewModel.class);

        fileRepository.getPhotoItems(new FileDataSource.FileCallback<List<PhotoItem>>() {
            @Override
            public void onSuccess(Result<List<PhotoItem>> result) {
                if (result instanceof Result.Success) {
                    List<PhotoItem> photoItems = ((Result.Success<List<PhotoItem>>) result).getData();
                    galleryAdapter = new GalleryAdapter(photoItems, item -> {
                        progressBar.setVisibility(View.VISIBLE);

                        long photoId = item.getId();
                        settingsRepository.updateProfilePic(photoId,
                                new SettingsDataSource.SettingsCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Result<Boolean> result) {
                                        if (result instanceof Result.Success) {
                                            Log.d("ChoosePictureFragment", "Profile picture updated successfully.");
                                            settingsViewModel.setSettingsResult(new SettingsResult(true));
                                            progressBar.setVisibility(View.GONE);

                                            requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
                                                    .edit()
                                                    .putLong("photoID", photoId)
                                                    .apply();


                                            Toast.makeText(getContext(), "Profile picture updated", Toast.LENGTH_SHORT)
                                                    .show();

                                            // Navigate back to MainActivity
                                            Intent intent = new Intent(requireContext(), MainActivity.class);
                                            startActivity(intent);
                                            requireActivity().finish();
                                        } else {
                                            Log.e("ChoosePictureFragment", "Failed to update profile picture");
                                            settingsViewModel.setSettingsResult(
                                                    new SettingsResult(getString(R.string.error_updating_picture)));
                                            progressBar.setVisibility(View.GONE);
                                        }

                                        NavController navController = Navigation.findNavController(requireActivity(),
                                                R.id.nav_host_fragment_content_main);
                                        navController.popBackStack();
                                        Intent intent = new Intent(requireContext(), MainActivity.class);
                                        startActivity(intent);
                                        requireActivity().finish();
                                    }

                                    @Override
                                    public void onError(Result.Error error) {
                                        Log.e("ChoosePictureFragment", "Error updating profile picture",
                                                error.getError());
                                        settingsViewModel.setSettingsResult(
                                                new SettingsResult(String.valueOf(R.string.error_updating_picture)));
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });

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
