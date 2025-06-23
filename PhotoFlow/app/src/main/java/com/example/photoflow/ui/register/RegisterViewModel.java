package com.example.photoflow.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Patterns;

import com.example.photoflow.data.RegisterDataSource;
import com.example.photoflow.data.RegisterRepository;
import com.example.photoflow.data.RegisterResult;
import com.example.photoflow.data.Result;
import com.example.photoflow.data.model.RegisteredUser;
import com.example.photoflow.R;

public class RegisterViewModel extends ViewModel {

    private MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();
    private MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private RegisterRepository registerRepository;

    public RegisterViewModel(RegisterRepository registerRepository) {
        this.registerRepository = registerRepository;
    }

    public LiveData<RegisterFormState> getRegisterFormState() {
        return registerFormState;
    }

    public LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }

    public void register(String username, String password, String email) {
        registerRepository.register(username, password, email, new RegisterDataSource.RegisterCallback() {
            @Override
            public void onSuccess(Result<RegisteredUser> result) {
                RegisteredUser data = ((Result.Success<RegisteredUser>) result).getData();
                registerResult.postValue(new RegisterResult(new RegisteredUserView(data.getDisplayName())));
            }

            @Override
            public void onError(Result.Error error) {
                registerResult.postValue(new RegisterResult(R.string.register_failed));
            }
        });
    }

    public void registerDataChanged(String username, String password, String email) {
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_username, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password, null));
        } else if (!isEmailValid(email)) {
            registerFormState.setValue(new RegisterFormState(null, null, R.string.invalid_email));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) return false;
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private boolean isEmailValid(String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

