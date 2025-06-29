package com.example.photoflow.ui.gallery;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoflow.R;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {

    private final List<Bitmap> photoPaths;

    public GalleryAdapter(List<Bitmap> photoPaths) {
        this.photoPaths = photoPaths;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Bitmap bitmap = photoPaths.get(position);
        holder.photoImageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return photoPaths.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }
    }
}
