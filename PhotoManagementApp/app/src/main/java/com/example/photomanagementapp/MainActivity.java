package com.example.photomanagementapp;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Check if user is already logged in
        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedUsername = prefs.getString("username", null);
        if (savedUsername != null) {
            // User already logged in, go to albums
            Intent intent = new Intent(MainActivity.this, SeeAlbums.class);
            startActivity(intent);
            finish();
            return;
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.btnNewHere).setOnClickListener(v -> {
            startActivity(new Intent(this, ActivityRegister.class));
        });

        findViewById(R.id.btnForgot).setOnClickListener(v -> {
            startActivity(new Intent(this, ActivityForgotPassword.class));
        });

        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            usernameEditText = findViewById(R.id.usernameEditText);
            passwordEditText = findViewById(R.id.passwordEditText);

            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            new LoginTask().execute(username, password);
        });
    }

    private class LoginTask extends AsyncTask<String, Void, String> {
        String enteredUsername = "";

        @Override
        protected String doInBackground(String... params) {
            try {
                enteredUsername = params[0];
                URL url = new URL("http://ec2-13-60-9-150.eu-north-1.compute.amazonaws.com:8000/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("login", enteredUsername);
                jsonParam.put("password", params[1]);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonParam.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line.trim());
                }
                reader.close();

                Log.d("LoginTask", "Server response: " + response.toString());

                JSONObject json = new JSONObject(response.toString());
                if (json.has("error_type")) {
                    return "Login failed: " + json.getString("message");
                } else if (json.has("token")) {
                    return "Login successful";
                } else {
                    return "Login failed: unknown response";
                }

            } catch (Exception e) {
                Log.e("LoginTask", "Error", e);
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            if ("Login successful".equals(result)) {
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                prefs.edit().putString("username", enteredUsername).apply();

                Intent intent = new Intent(MainActivity.this, SeeAlbums.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
