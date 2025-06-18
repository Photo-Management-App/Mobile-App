package com.example.photomanagementapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Go to register activity on btnNewHere click
        findViewById(R.id.btnNewHere).setOnClickListener(v -> {
            startActivity(new Intent(this, ActivityRegister.class));
        });

        //Go to forgot password activity on btnForgot click
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
    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL("http://ec2-13-60-9-150.eu-north-1.compute.amazonaws.com:8000/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);
            conn.setDoInput(true); // <-- Important for reading response

            // Create JSON payload
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("login", params[0]); // Note: field name should be "login" not "username"
            jsonParam.put("password", params[1]);

            // Send JSON to server
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonParam.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read response from server
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }
            reader.close();

            Log.d("LoginTask", "Server response: " + response.toString());

            // Parse JSON response
            try {
                JSONObject json = new JSONObject(response.toString());
                if (json.has("error_type")) {
                    return "Login failed: " + json.getString("message");
                } else if (json.has("token")) {
                    // You can also store token here if needed
                    return "Login successful";
                } else {
                    return "Login failed: unknown response";
                }
            } catch (Exception e) {
                return "Login failed: invalid server response";
            }

        } catch (Exception e) {
            Log.e("LoginTask", "Error", e);
            return "Exception: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
    }
}

}