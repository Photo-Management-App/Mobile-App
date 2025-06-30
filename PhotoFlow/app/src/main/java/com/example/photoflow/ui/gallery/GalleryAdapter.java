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
import com.example.photoflow.data.model.PhotoItem;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {

    public interface OnPhotoClickListener {
        void onPhotoClick(PhotoItem item);
    }

    private final List<PhotoItem> photoItems;
    private final OnPhotoClickListener listener;

    public GalleryAdapter(List<PhotoItem> photoItems, OnPhotoClickListener listener) {
        this.photoItems = photoItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        PhotoItem item = photoItems.get(position);
        Bitmap bitmap = item.getBitmap();

        holder.photoImageView.setImageBitmap(bitmap);
        holder.photoImageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPhotoClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photoItems.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }
    }
}
