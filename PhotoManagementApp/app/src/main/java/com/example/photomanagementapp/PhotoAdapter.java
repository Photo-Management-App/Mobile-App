package com.example.photomanagementapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {

    private final List<Integer> photoResIds;
    private final OnPhotoClickListener listener;

    public interface OnPhotoClickListener {
        void onPhotoClick(int photoResId);
    }

    public PhotoAdapter(List<Integer> photoResIds, OnPhotoClickListener listener) {
        this.photoResIds = photoResIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        int resId = photoResIds.get(position);
        holder.imageView.setImageResource(resId);
        holder.imageView.setOnClickListener(v -> listener.onPhotoClick(resId));
    }

    @Override
    public int getItemCount() {
        return photoResIds.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgPhoto);
        }
    }
}

