package com.example.photoflow;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

import com.example.photoflow.data.FileRepository;
import com.example.photoflow.data.LoginDataSource;
import com.example.photoflow.data.LoginRepository;
import com.example.photoflow.data.model.FileUploadItem;
import com.example.photoflow.databinding.ActivityMainBinding;
import com.example.photoflow.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private boolean isFabMenuVisible = false;
    private View fabMenuView;

    private static final int REQUEST_IMAGE_CAPTURE = 1001;
    private Uri photoUri;
    private String currentPhotoFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
boolean canTakePhoto = intent.resolveActivity(getPackageManager()) != null;
Log.d("MainActivity", "Camera app available: " + canTakePhoto);

        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 200);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        // FAB click opens/closes the fab menu overlay
        binding.appBarMain.fab.setOnClickListener(view -> toggleFabMenu());

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Set the username in the nav_header_main TextView
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.usernameTextView);
        String displayName = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("displayName", "Guest");
        usernameTextView.setText(displayName);

        // Set up navigation
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void toggleFabMenu() {
        FrameLayout container = findViewById(R.id.fab_menu_container);

        if (!isFabMenuVisible) {
            // Inflate and show the FAB menu
            fabMenuView = LayoutInflater.from(this).inflate(R.layout.fab_menu, container, false);
            container.addView(fabMenuView);
            container.setVisibility(View.VISIBLE);
            isFabMenuVisible = true;

            binding.appBarMain.fab.setImageResource(R.drawable.ic_close);

            // Optional: set click listeners for your FAB buttons
            fabMenuView.findViewById(R.id.button_photo).setOnClickListener(v -> {
                // Go to home
                // Example: startActivity(new Intent(this, HomeActivity.class));
                closeFabMenu();
            });

            fabMenuView.findViewById(R.id.button_album).setOnClickListener(v -> {
                // Go to profile
                // Example: startActivity(new Intent(this, ProfileActivity.class));
                closeFabMenu();
            });

            fabMenuView.findViewById(R.id.button_camera).setOnClickListener(v -> {
                Log.d("MainActivity", "Camera button clicked!");
                dispatchTakePictureIntent();
                closeFabMenu();
            });



            fabMenuView.findViewById(R.id.button_close).setOnClickListener(v -> {
                // Settings
                closeFabMenu();
            });

            // Dismiss FAB menu when clicking outside
            container.setOnClickListener(v -> closeFabMenu());

        } else {
            // Hide FAB menu
            closeFabMenu();
        }
    }

    private void closeFabMenu() {
        FrameLayout container = findViewById(R.id.fab_menu_container);
        if (fabMenuView != null) {
            container.removeView(fabMenuView);
            fabMenuView = null;
        }
        container.setVisibility(View.GONE);
        isFabMenuVisible = false;

        binding.appBarMain.fab.setImageResource(android.R.drawable.ic_input_add);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            // Clear token and go to login
            LoginRepository.getInstance(new LoginDataSource(getApplicationContext()), getApplicationContext()).logout();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                if (imageBitmap != null) {
                    try {
                        // Convert bitmap to JPEG byte array
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();

                        // Prepare metadata (example)
                        JSONObject metadata = new JSONObject();
                        metadata.put("source", "camera");
                        metadata.put("timestamp", System.currentTimeMillis());

                        // Tags example
                        List<String> tags = new java.util.ArrayList<>();
                        tags.add("camera");
                        tags.add("photo");

                        FileUploadItem fileItem = new FileUploadItem(imageBytes, metadata, tags);

                        // Upload file using your FileRepository
                        FileRepository.getInstance(this).uploadFiles(
                            java.util.Collections.singletonList(fileItem),
                            new com.example.photoflow.data.FileDataSource.FileUploadCallback() {
                                @Override
                                public void onSuccess() {
                                    runOnUiThread(() -> {
                                        android.widget.Toast.makeText(MainActivity.this, "Photo uploaded!", android.widget.Toast.LENGTH_SHORT).show();
                                    });
                                }

                                @Override
                                public void onError(Exception e) {
                                    runOnUiThread(() -> {
                                        android.widget.Toast.makeText(MainActivity.this, "Upload failed: " + e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
                                    });
                                }
                            }
                        );

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // Handle error: no camera app found
        }
    }




    private byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}
