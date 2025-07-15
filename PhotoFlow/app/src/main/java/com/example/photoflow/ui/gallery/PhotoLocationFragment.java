package com.example.photoflow.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.photoflow.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PhotoLocationFragment extends Fragment {

    private LatLng targetLocation = null;
    private GoogleMap googleMap;

    private final OnMapReadyCallback callback = map -> {
        googleMap = map;
        updateMapMarker();
    };

    private void updateMapMarker() {
        if (googleMap == null) return;

        googleMap.clear();

        if (targetLocation != null) {
            googleMap.addMarker(new MarkerOptions()
                    .position(targetLocation)
                    .title("Photo taken here"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 15f));
        } else {
            // fallback to Warsaw
            LatLng fallback = new LatLng(52.2297, 21.0122);
            googleMap.addMarker(new MarkerOptions().position(fallback).title("No location passed"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fallback, 10f));
            Toast.makeText(getContext(), "No valid coordinates, showing fallback", Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            String coordinates = getArguments().getString("coordinates");
            Log.d("PhotoLocationFragment", "Received coordinates: " + coordinates);

            if (coordinates != null && coordinates.contains(",")) {
                try {
                    String[] parts = coordinates.split(",");
                    double lat = Double.parseDouble(parts[0].trim());
                    double lon = Double.parseDouble(parts[1].trim());
                    targetLocation = new LatLng(lat, lon);
                } catch (Exception e) {
                    Log.e("PhotoLocationFragment", "Invalid coordinate format", e);
                }
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}

