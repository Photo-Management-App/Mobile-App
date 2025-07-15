package com.example.photoflow.ui.settings;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.photoflow.R;
import com.example.photoflow.data.Result;
import com.example.photoflow.data.SettingsDataSource;
import com.example.photoflow.data.SettingsRepository;

public class SettingsViewModel extends ViewModel {
    
    private final SettingsRepository settingsRepository;
    private boolean isUploading;
    private final Context context;
    private final MutableLiveData<SettingFormState> formState = new MutableLiveData<>();
    private final MutableLiveData<SettingsResult> settingsResult = new MutableLiveData<>();

    public SettingsViewModel(Context context) {
        this.settingsRepository = new SettingsRepository(context);
        this.context = context;
        isUploading = false;
    }

    public LiveData<SettingFormState> getFormState() {
        return formState;
    }

    public LiveData<SettingsResult> getSettingsResult() {
        return settingsResult;
    }

    public void onEmailChanged(String email){
        if(email == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            formState.setValue(new SettingFormState(R.string.invalid_email));
        } else {
            formState.setValue(new SettingFormState(true));
        }
    }

    public void updateEmail(String email) {
        Log.d("SettingsViewModel", "updateEmail called with: " + email);

        if (email == null || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.d("SettingsViewModel", "Invalid email format.");
            settingsResult.setValue(new SettingsResult(String.valueOf(R.string.invalid_email)));
        } else {
            context.getSharedPreferences("user_prefs", MODE_PRIVATE)
                    .edit()
                    .putString("email", email)
                    .apply();

            Log.d("SettingsViewModel", "Email saved in SharedPreferences: " + email);

            settingsRepository.updateEmail(email, new SettingsDataSource.SettingsCallback<Boolean>() {
                @Override
                public void onSuccess(Result<Boolean> result) {
                    Log.d("SettingsViewModel", "Email update successful.");
                    settingsResult.setValue(new SettingsResult(true));
                }

                @Override
                public void onError(Result.Error error) {
                    Log.e("SettingsViewModel", "Email update failed: " + error.getError().getMessage());
                    settingsResult.setValue(new SettingsResult(error.getError().getMessage()));
                }
            });
        }
    }


    public void updateProfilePicture() {
        // Logic to update profile picture
        // Assume success for this example
        settingsResult.setValue(new SettingsResult(true));
    }

}
