package com.example.photoflow.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import com.example.photoflow.data.FileDataSource;
import com.example.photoflow.data.FileRepository;
import com.example.photoflow.data.Result;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.photoflow.MainActivity;
import com.example.photoflow.R;
import com.example.photoflow.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    private SettingsViewModel settingsViewModel;
    private FileRepository fileRepository;

    private FragmentSettingsBinding bindings;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: inflating layout");
        bindings = FragmentSettingsBinding.inflate(inflater, container, false);
        return bindings.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onViewCreated: setting up ViewModel");
        settingsViewModel = new ViewModelProvider(this, new SettingsViewModelFactory(requireContext()))
                .get(SettingsViewModel.class);

        Log.d(TAG, "onViewCreated: setting up observers");

        settingsViewModel.getFormState().observe(getViewLifecycleOwner(), settingFormState -> {
            if (settingFormState == null) return;
            bindings.changeEmailButton.setEnabled(settingFormState.isDataValid());
            if (settingFormState.getEmailError() != null) {
                bindings.emailInput.setError(getString(settingFormState.getEmailError()));
            }
        });

        settingsViewModel.getSettingsResult().observe(getViewLifecycleOwner(), settingsResult -> {
            if (settingsResult == null) {
                Log.d(TAG, "SettingsResult is null");
                return;
            }

            bindings.loading.setVisibility(View.GONE);

            if (settingsResult.getMessage() != null) {
                Log.d(TAG, "Settings update error: " + settingsResult.getMessage());
                Toast.makeText(requireContext(), settingsResult.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (Boolean.TRUE.equals(settingsResult.getSuccess())) {
                Log.d(TAG, "Settings update successful. Navigating to MainActivity...");
                updateUiWithUser();
            }
        });

        settingsViewModel.getNavigateToChoosePicture().observe(getViewLifecycleOwner(), shouldNavigate -> {
            if (Boolean.TRUE.equals(shouldNavigate)) {
                Log.d(TAG, "Navigation LiveData triggered: true");
                try {
                    NavController navController = NavHostFragment.findNavController(this);
                    navController.navigate(R.id.nav_choose_picture_fragment);
                    Log.d(TAG, "Navigation to ChoosePictureFragment successful");
                } catch (Exception e) {
                    Log.e(TAG, "Navigation failed", e);
                }
            }
        });

        bindings.emailInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                settingsViewModel.onEmailChanged(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        bindings.changeEmailButton.setOnClickListener(v -> {
            bindings.loading.setVisibility(View.VISIBLE);
            Log.d(TAG, "Email update button clicked");
            settingsViewModel.updateEmail(bindings.emailInput.getText().toString());
        });

        bindings.changeProfilePicButton.setOnClickListener(v -> {
            Log.d(TAG, "Change picture button clicked, triggering navigation LiveData");
            settingsViewModel.onChoosePictureClicked();
        });

        // Initialize FileDataSource and FileRepository
        FileDataSource fileDataSource = new FileDataSource(requireContext());
        fileRepository = FileRepository.getInstance(fileDataSource, requireContext());

// Find profile_picture ImageView
        ImageView profileImageView = bindings.getRoot().findViewById(R.id.profile_picture);

// Get photoId from SharedPreferences (same keys as MainActivity)
        long photoId = requireContext().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE)
                .getLong("profilePicId", -1);

        if (photoId != -1) {
            fileRepository.downloadFiles(photoId, new FileDataSource.FileCallback<Bitmap>() {
                @Override
                public void onSuccess(Result<Bitmap> result) {
                    if (result instanceof Result.Success) {
                        Bitmap profileImageBitmap = ((Result.Success<Bitmap>) result).getData();
                        profileImageView.setImageBitmap(profileImageBitmap);
                    } else {
                        Log.e("SettingsFragment", "Failed to load profile image");
                    }
                }

                @Override
                public void onError(Result.Error error) {
                    Log.e("SettingsFragment", "Error loading profile image", error.getError());
                }
            });
        } else {
            // Set a placeholder image if no profile pic
            profileImageView.setImageResource(R.drawable.ic_menu_camera);
        }

    }

    private void updateUiWithUser() {
        Toast.makeText(requireContext(), "Settings updated successfully", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "updateUiWithUser: navigating to MainActivity");
        Intent intent = new Intent(requireContext(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bindings = null;
    }
}

