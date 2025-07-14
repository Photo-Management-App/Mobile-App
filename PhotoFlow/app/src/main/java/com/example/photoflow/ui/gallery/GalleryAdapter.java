package com.example.photoflow.ui.gallery;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.photoflow.R;
import com.example.photoflow.data.model.PhotoItem;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.PhotoViewHolder> {

    private static final int VIEW_TYPE_PHOTO = 0;
    private static final int VIEW_TYPE_ADD_BUTTON = 1;

    public interface OnPhotoClickListener {
        void onPhotoClick(PhotoItem item);
    }

    private final List<PhotoItem> photoItems;
    private final OnPhotoClickListener listener;
    private final boolean showAddButton;

    public GalleryAdapter(List<PhotoItem> photoItems, OnPhotoClickListener listener, boolean showAddButton) {
        this.photoItems = photoItems;
        this.listener = listener;
        this.showAddButton = showAddButton;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_PHOTO) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent, false);
            return new PhotoViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_photo_button_item, parent, false);
            return new AddButtonViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_PHOTO) {
            PhotoItem item = photoItems.get(position);
            Bitmap bitmap = item.getBitmap();

            holder.photoImageView.setImageBitmap(bitmap);
            holder.photoImageView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPhotoClick(item);
                }
            });
        } else if (holder instanceof AddButtonViewHolder) {
            ((AddButtonViewHolder) holder).bind(); // Trigger the add photo callback
        }
    }

    @Override
    public int getItemCount() {
        return photoItems.size() + (showAddButton ? 1 : 0);
    }


    @Override
    public int getItemViewType(int position) {
        if (showAddButton && position == photoItems.size()) {
            return VIEW_TYPE_ADD_BUTTON;
        }
        return VIEW_TYPE_PHOTO;
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView photoImageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
        }
    }

    class AddButtonViewHolder extends PhotoViewHolder {
        Button buttonAddPhoto;

        public AddButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonAddPhoto = itemView.findViewById(R.id.buttonAddPhoto);
        }

        public void bind() {
            buttonAddPhoto.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPhotoClick(null); // null = add action
                }
            });
        }
    }

}
