package com.example.photoflow.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoflow.R;
import com.example.photoflow.data.AlbumDataSource;
import com.example.photoflow.data.AlbumRepository;
import com.example.photoflow.data.FileDataSource;
import com.example.photoflow.data.FileRepository;
import com.example.photoflow.data.Result;
import com.example.photoflow.data.model.AlbumItem;
import com.example.photoflow.data.model.PhotoItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private AlbumAdapter albumAdapter;
    private AlbumRepository albumRepository;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = root.findViewById(R.id.albumRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        progressBar = root.findViewById(R.id.loading);
        // Initialize the AlbumRepository
        FileDataSource fileDataSource = new FileDataSource(requireContext());
        FileRepository fileRepository = FileRepository.getInstance(fileDataSource, requireContext());
        AlbumDataSource albumDataSource = new AlbumDataSource(requireContext(), fileRepository);
        albumRepository = AlbumRepository.getInstance(requireContext());
        progressBar.setVisibility(View.VISIBLE);
        albumRepository.getAlbumItems(new AlbumDataSource.AlbumCallback<List<AlbumItem>>() {
            @Override
            public void onSuccess(Result<List<AlbumItem>> result) {
                if (result instanceof Result.Success) {
                    List<AlbumItem> albumItems = ((Result.Success<List<AlbumItem>>) result).getData();
                    albumAdapter = new AlbumAdapter(albumItems, item -> {
                        progressBar.setVisibility(View.VISIBLE);

                        albumRepository.getPhotoItems(item.getId(),
                                new AlbumDataSource.AlbumCallback<List<PhotoItem>>() {
                                    @Override
                                    public void onSuccess(Result<List<PhotoItem>> result) {
                                        if (result instanceof Result.Success) {
                                            List<PhotoItem> photoItems = ((Result.Success<List<PhotoItem>>) result)
                                                    .getData();

                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("albumItem", item);
                                            bundle.putSerializable("photoItems", new ArrayList<>(photoItems));
                                            bundle.putSerializable("albumId", item.getId());

                                            NavController navController = Navigation.findNavController(
                                                    requireActivity(), R.id.nav_host_fragment_content_main);
                                            navController.navigate(R.id.nav_album_detail_fragment, bundle); // this is
                                                                                                            // now
                                                                                                            // active
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Toast.makeText(getContext(), "Failed to load photos for album",
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                    });
                    recyclerView.setAdapter(albumAdapter);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Failed to load albums", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

        return root;
    }

}