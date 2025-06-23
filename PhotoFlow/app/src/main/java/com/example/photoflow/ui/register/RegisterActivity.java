package com.example.photoflow.ui.register;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoflow.databinding.ActivityRegisterBinding;
import com.example.photoflow.R;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory())
                .get(RegisterViewModel.class);

        registerViewModel.getRegisterFormState().observe(this, registerFormState -> {
            if (registerFormState == null) {
                return;
            }
            binding.register.setEnabled(registerFormState.isDataValid());
            if (registerFormState.getUsernameError() != null) {
                binding.username.setError(getString(registerFormState.getUsernameError()));
            }
            if (registerFormState.getPasswordError() != null) {
                binding.password.setError(getString(registerFormState.getPasswordError()));
            }
            if (registerFormState.getEmailError() != null) {
                binding.email.setError(getString(registerFormState.getEmailError()));
            }
        });

        registerViewModel.getRegisterResult().observe(this, registerResult -> {
            if (registerResult == null) {
                return;
            }
            binding.loading.setVisibility(View.GONE);
            if (registerResult.getError() != null) {
                Toast.makeText(getApplicationContext(), registerResult.getError(), Toast.LENGTH_SHORT).show();
            }
            if (registerResult.getSuccess() != null) {
                Toast.makeText(getApplicationContext(), "Please login now", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_OK);
                finish(); // Close and return to login screen
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                registerViewModel.registerDataChanged(
                        binding.username.getText().toString(),
                        binding.password.getText().toString(),
                        binding.email.getText().toString()
                );
            }
        };
        binding.username.addTextChangedListener(afterTextChangedListener);
        binding.password.addTextChangedListener(afterTextChangedListener);
        binding.email.addTextChangedListener(afterTextChangedListener);

        binding.register.setOnClickListener(v -> {
            binding.loading.setVisibility(View.VISIBLE);
            registerViewModel.register(
                    binding.username.getText().toString(),
                    binding.password.getText().toString(),
                    binding.email.getText().toString()
            );
            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED, intent);
            finish(); // Close registration activity
            
        });

        binding.login.setOnClickListener(v -> {
            // Navigate back to login screen
            Intent intent = new Intent();
            setResult(Activity.RESULT_CANCELED, intent);
            finish(); // Close registration activity
        });
    }
}

