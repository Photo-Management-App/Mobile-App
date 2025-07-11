package com.example.photoflow.ui.home;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.photoflow.R;
import com.example.photoflow.data.FileRepository;
import com.example.photoflow.data.model.AlbumItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import org.w3c.dom.Text;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private static final String TAG = "AlbumAdapter";
    private FileRepository fileRepository;

    public interface OnAlbumClickListener {
        void onAlbumClick(AlbumItem albumItem);
    }

    private List<AlbumItem> albumList;
    private OnAlbumClickListener listener;

    public AlbumAdapter(List<AlbumItem> albumList, OnAlbumClickListener listener) {
        this.albumList = albumList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AlbumItem albumItem = albumList.get(position);
        Bitmap bitmap = albumItem.getCoverImage();

        holder.albumImageView.setImageBitmap(bitmap);
        holder.albumTitleTextView.setText(albumItem.getTitle());

        holder.albumImageView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAlbumClick(albumItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView albumImageView;
        TextView albumTitleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            albumImageView = itemView.findViewById(R.id.albumImageView);
            albumTitleTextView = itemView.findViewById(R.id.albumTitleTextView);
        }
    }

}
