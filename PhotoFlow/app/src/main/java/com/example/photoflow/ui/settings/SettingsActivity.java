package com.example.photoflow.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoflow.MainActivity;
import com.example.photoflow.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private SettingsViewModel setttingsViewModel;
    private ActivitySettingsBinding bindings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bindings = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(bindings.getRoot());

        setttingsViewModel = new ViewModelProvider(this, new SettingsViewModelFactory(getApplicationContext()))
                .get(SettingsViewModel.class);

        final ImageView profileImageView = bindings.profilePicture;
        final Button changePictureButton = bindings.changeProfilePicButton;
        final EditText emailEditText = bindings.emailInput;
        final Button changeEmailButton = bindings.changeEmailButton;
        final ProgressBar loadingProgressBar = bindings.loading;

        // Observe the current user data
        setttingsViewModel.getFormState().observe(this, new Observer<SettingFormState>() {
            @Override
            public void onChanged(@Nullable SettingFormState settingFormState) {
                if (settingFormState == null) {
                    return;
                }
                changeEmailButton.setEnabled(settingFormState.isDataValid());
                if (settingFormState.getEmailError() != null) {
                    emailEditText.setError(getString(settingFormState.getEmailError()));
                }
            }
        });

        setttingsViewModel.getSettingsResult().observe(this, new Observer<SettingsResult>() {
            @Override
            public void onChanged(@Nullable SettingsResult settingsResult) {
                if (settingsResult == null) {
                    Log.d("SettingsActivity", "SettingsResult is null");
                    return;
                }

                loadingProgressBar.setVisibility(View.GONE);

                if (settingsResult.getMessage() != null) {
                    Log.d("SettingsActivity", "Settings update error: " + settingsResult.getMessage());
                    Toast.makeText(SettingsActivity.this, settingsResult.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (settingsResult.getSuccess() != null) {
                    Log.d("SettingsActivity", "Settings update successful. Navigating to MainActivity...");
                    updateUiWithUser(); // this navigates and calls finish()
                }
            }
        });


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setttingsViewModel.onEmailChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        emailEditText.addTextChangedListener(afterTextChangedListener);

        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                setttingsViewModel.updateEmail(emailEditText.getText().toString());
            }
        });

        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic to change profile picture
                // For example, open a file picker or camera
                setttingsViewModel.updateProfilePicture();
            }
        });

    }

    private void updateUiWithUser() {
        // Logic to update UI with user data
        // For example, updating the profile picture and email

        Toast.makeText(this, "Settings updated successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}
