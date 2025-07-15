package com.example.photoflow;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.photoflow.data.model.UserSession;
import com.example.photoflow.ui.settings.SettingsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.location.Location;

import android.app.Activity;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
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
import com.example.photoflow.data.model.LoggedInUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.widget.ImageView;

import com.example.photoflow.ui.upload.FileUploadActivity;
import android.Manifest;
import android.content.pm.PackageManager;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private boolean isFabMenuVisible = false;
    private View fabMenuView;
    private FileRepository fileRepository;
    private File cameraImageFile;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private String base64EncodedFile;
    private boolean isUploading = false;
    Bitmap bitmap;
    private Uri photoUri;
    private String coordinates;
    Bitmap profileImageBitmap;
    long photoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.CAMERA }, 1001);
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 1002);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        // FAB click opens/closes the fab menu overlay
        binding.appBarMain.fab.setOnClickListener(view -> toggleFabMenu());

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Initialize FileDataSource
        FileDataSource fileDataSource = new FileDataSource(this);

        // Initialize FileRepository singleton
        fileRepository = FileRepository.getInstance(fileDataSource, this);

        // Set the username in the nav_header_main TextView
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.usernameTextView);
        TextView emailTextView = headerView.findViewById(R.id.mailTextView);
        ImageView profileImageView = headerView.findViewById(R.id.profileImageView);
        String displayName = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("displayName", "Guest");
        String email = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getString("email", null);
        emailTextView.setText(email != null ? email : "No email");
        usernameTextView.setText(displayName);
        photoId = getSharedPreferences("user_prefs", MODE_PRIVATE)
                .getLong("profilePicId", -1);


        if (photoId != -1) {
            fileRepository.downloadFiles(photoId,
                    new FileDataSource.FileCallback<Bitmap>() {
                        @Override
                        public void onSuccess(Result<Bitmap> result) {
                            if (result instanceof Result.Success) {
                                profileImageBitmap = ((Result.Success<Bitmap>) result).getData();
                                profileImageView.setImageBitmap(profileImageBitmap);
                            } else {
                                Log.e("MainActivity", "Failed to load profile image");
                            }
                        }

                        @Override
                        public void onError(Result.Error error) {
                            Log.e("MainActivity", "Error loading profile image", error.getError());
                        }
                    });
            profileImageView.setImageBitmap(profileImageBitmap);
        } else {
            profileImageView.setImageResource(R.drawable.ic_menu_camera);
        }

        // Set up navigation
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery)
                .setOpenableLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Uri uri = null;

                        if (result.getData() != null && result.getData().getData() != null) {
                            // Picked from gallery
                            uri = result.getData().getData();
                        } else if (photoUri != null) {
                            // Captured from camera
                            uri = photoUri;
                        }

                        if (uri != null) {
                            try {
                                File cacheDir = getCacheDir();
                                File tempFile = new File(cacheDir, "temp_image.jpg");

                                FileOutputStream fos = new FileOutputStream(tempFile);

                                InputStream inputStream = getContentResolver().openInputStream(uri);
                                bitmap = BitmapFactory.decodeStream(inputStream);
                                inputStream.close();

                                InputStream exifStream = getContentResolver().openInputStream(uri);
                                ExifInterface exif = new ExifInterface(exifStream);
                                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                        ExifInterface.ORIENTATION_NORMAL);

                                Matrix matrix = new Matrix();
                                switch (orientation) {
                                    case ExifInterface.ORIENTATION_ROTATE_90:
                                        matrix.postRotate(90);
                                        break;
                                    case ExifInterface.ORIENTATION_ROTATE_180:
                                        matrix.postRotate(180);
                                        break;
                                    case ExifInterface.ORIENTATION_ROTATE_270:
                                        matrix.postRotate(270);
                                        break;
                                }

                                float[] latLong = new float[2];
                                if (exif.getLatLong(latLong)) {
                                    float latitude = latLong[0];
                                    float longitude = latLong[1];
                                    Log.d("PhotoLocation", "Latitude: " + latitude + ", Longitude: " + longitude);
                                    // You can store this into your model or pass it to upload
                                } else {
                                    Log.d("PhotoLocation", "No GPS data found in image");
                                }

                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                                        matrix, true);
                                exifStream.close();

                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                fos.flush();
                                fos.close();
                                Log.d("MainActivity", "Image saved to cache: " + tempFile.getAbsolutePath());

                                isUploading = true;
                                getCurrentLocationAndLaunchUpload(tempFile);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Action cancelled", Toast.LENGTH_SHORT).show();
                    }
                });

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
                closeFabMenu();
            });

            fabMenuView.findViewById(R.id.button_album).setOnClickListener(v -> {
                NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_choose_cover_fragment);
                closeFabMenu();
            });

            fabMenuView.findViewById(R.id.button_camera).setOnClickListener(v -> {
                try {
                    File imageFile = File.createTempFile("camera_photo", ".jpg",
                            getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                    photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", imageFile);

                    Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    iCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    activityResultLauncher.launch(iCamera);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
                }

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
        if(item.getItemId() == R.id.settings) {
            // Handle settings click
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCurrentLocationAndLaunchUpload(File tempFile) {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            coordinates = latitude + "," + longitude;
                            Log.d("PhotoLocation", "Device location: " + coordinates);

                            // Pass this to your upload activity
                            Intent intent = new Intent(MainActivity.this, FileUploadActivity.class);
                            intent.putExtra("imagePath", tempFile.getAbsolutePath());
                            intent.putExtra("coords", coordinates); // No coordinates available
                            startActivity(intent);
                        } else {
                            Log.d("PhotoLocation", "Location is null, continuing without it");
                            launchUploadWithoutLocation(tempFile);
                        }
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        launchUploadWithoutLocation(tempFile);
                    });
        } else {
            Log.d("PhotoLocation", "Permission denied for location");
            launchUploadWithoutLocation(tempFile);
        }
    }

    private void launchUploadWithoutLocation(File tempFile) {
        Intent intent = new Intent(MainActivity.this, FileUploadActivity.class);
        intent.putExtra("imagePath", tempFile.getAbsolutePath());
        startActivity(intent);
    }

}
