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

    EditText usernameEditText, passwordEditText;

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

        findViewById(R.id.btnLogin).setOnClickListener(v -> {

            usernameEditText = findViewById(R.id.usernameEditText);
            passwordEditText = findViewById(R.id.passwordEditText);

            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            new RegisterTask().execute(username, password);

        });

        // Go to login activity on btnLogin click
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

            try {
                URL url = new URL("http://ec2-13-60-9-150.eu-north-1.compute.amazonaws.com:8000/register");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                // Create JSON object with login and password
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("login", login);
                jsonParam.put("password", password);

                // Send JSON data to server
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                // Read server response
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
                    } else if (jsonResponse.has("token")) {
                        // Optionally store token here if you want
                        return "Register successful";
                    } else {
                        return "Register failed: unknown response";
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