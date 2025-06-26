package com.example.photoflow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import android.app.Activity;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.photoflow.data.FileDataSource;
import com.example.photoflow.data.FileRepository;
import com.example.photoflow.data.LoginDataSource;
import com.example.photoflow.data.LoginRepository;
import com.example.photoflow.data.Result;
import com.example.photoflow.databinding.ActivityMainBinding;
import com.example.photoflow.ui.login.LoginActivity;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private boolean isFabMenuVisible = false;
    private View fabMenuView;
    private FileRepository fileRepository;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private String base64EncodedFile;
    private boolean isUploading = false;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        
        // Initialize FileDataSource
        FileDataSource fileDataSource = new FileDataSource(this);

        // Initialize FileRepository singleton
        fileRepository = FileRepository.getInstance(fileDataSource, this);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] bytes = baos.toByteArray();
                            String base64EncodedFile = Base64.encodeToString(bytes, Base64.DEFAULT);

                            Log.e("encoded file", base64EncodedFile);

                            isUploading = true;

                            fileRepository.upload(
                                    base64EncodedFile,
                                    "file_name.jpg",
                                    "Title",
                                    "Description",
                                    "Coordinates",
                                    new FileDataSource.FileCallback() {
                                        @Override
                                        public void onSuccess(Result<Boolean> result) {
                                            isUploading = false;
                                            Log.d("MainActivity", "Upload successful");
                                        }

                                        @Override
                                        public void onError(Result.Error error) {
                                            isUploading = false;
                                            Log.e("MainActivity", "Upload failed: " + error.getError().getMessage());
                                        }
                                    }
                            );

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }

    private void toggleFabMenu() {
        FrameLayout container = findViewById(R.id.fab_menu_container);

        if (!isFabMenuVisible) {
            fabMenuView = LayoutInflater.from(this).inflate(R.layout.fab_menu, container, false);
            container.addView(fabMenuView);
            container.setVisibility(View.VISIBLE);
            isFabMenuVisible = true;

            binding.appBarMain.fab.setImageResource(R.drawable.ic_close);

            fabMenuView.findViewById(R.id.button_photo).setOnClickListener(v -> {
                if (isUploading) {
                    Log.d("MainActivity", "Upload already in progress, ignoring click");
                    return;
                }
                Log.d("MainActivity", "FAB clicked, launching image picker");
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            });

            fabMenuView.findViewById(R.id.button_album).setOnClickListener(v -> {
                // Go to profile - placeholder
                closeFabMenu();
            });

            fabMenuView.findViewById(R.id.button_camera).setOnClickListener(v -> {
                closeFabMenu();
            });

            fabMenuView.findViewById(R.id.button_close).setOnClickListener(v -> closeFabMenu());

            // Dismiss FAB menu when clicking outside
            container.setOnClickListener(v -> closeFabMenu());

        } else {
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
            LoginRepository.getInstance(new LoginDataSource(getApplicationContext()), getApplicationContext()).logout();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
