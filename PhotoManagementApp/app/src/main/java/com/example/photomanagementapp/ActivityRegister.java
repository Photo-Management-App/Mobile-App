package com.example.photomanagementapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ActivityRegister extends AppCompatActivity {

    EditText usernameEditText, passwordEditText, emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        emailEditText = findViewById(R.id.emailEditText); // Make sure this exists in your XML

        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String email = emailEditText.getText().toString();

            new RegisterTask().execute(username, password, email);
        });

        findViewById(R.id.btnHaveAccount).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }

    private class RegisterTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String login = params[0];
            String password = params[1];
            String email = params[2];

            try {
                URL url = new URL("http://ec2-13-60-9-150.eu-north-1.compute.amazonaws.com:8000/register");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("login", login);
                jsonParam.put("password", password);
                jsonParam.put("email", email); // Include email

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line.trim());
                    }

                    Log.d("RegisterTask", "Server response: " + response.toString());

                    JSONObject jsonResponse = new JSONObject(response.toString());

                    if (jsonResponse.has("error_type")) {
                        return "Register failed: " + jsonResponse.getString("message");
                    } else {
                        return "Register successful";
                    }
                }
            } catch (Exception e) {
                Log.e("RegisterTask", "Error", e);
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(ActivityRegister.this, result, Toast.LENGTH_LONG).show();
        }
    }
}
