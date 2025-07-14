package com.example.photoflow.ui.share;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.photoflow.R;
import com.example.photoflow.data.Result;
import com.example.photoflow.data.ShareDataSource;
import com.example.photoflow.data.ShareRepository;
import com.example.photoflow.data.model.PhotoItem;

public class SharedUrlFragment extends Fragment {

    private PhotoItem photoItem;
    private ShareRepository shareRepository;
    private long photoId;

    public SharedUrlFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shared_url, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            photoItem = (PhotoItem) getArguments().getSerializable("photoItem");
        }

        ImageView photoView = view.findViewById(R.id.sharedImageView);
        TextView urlView = view.findViewById(R.id.urlTextView);
        ImageView copyView = view.findViewById(R.id.copyIcon);

        shareRepository = ShareRepository.getInstance(requireContext());

        if (photoItem != null) {
            photoId = photoItem.getId();

            if (photoItem.getBitmap() != null) {
                photoView.setImageBitmap(photoItem.getBitmap());
            }

            shareRepository.shareFile(photoId, new ShareDataSource.ShareCallback<String>() {
                @Override
                public void onSuccess(Result<String> result) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (result instanceof Result.Success) {
                            String sharedUrl = ((Result.Success<String>) result).getData();
                            urlView.setText(sharedUrl);
                        } else {
                            Toast.makeText(requireContext(), "Failed to get shared URL", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
                }
            });

            copyView.setOnClickListener(v -> {
                String urlToCopy = urlView.getText().toString();
                if (!urlToCopy.isEmpty()) {
                    ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Shared URL", urlToCopy);
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(requireContext(), "URL skopiowany do schowka", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
