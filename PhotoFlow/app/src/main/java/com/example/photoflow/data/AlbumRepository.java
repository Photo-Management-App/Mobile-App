package com.example.photoflow.data;

import android.content.Context;

import com.example.photoflow.data.model.AlbumItem;
import com.example.photoflow.data.model.PhotoItem;

import java.util.List;

public class AlbumRepository {

    private static volatile AlbumRepository instance;

    private final AlbumDataSource albumDataSource;
    private final Context context;

    // private constructor : singleton access
    public AlbumRepository(AlbumDataSource albumDataSource, Context context) {
        this.albumDataSource = albumDataSource;
        this.context = context.getApplicationContext(); // Use app context to avoid leaks
    }

    public static AlbumRepository getInstance(AlbumDataSource albumDataSource, Context context) {
        if (instance == null) {
            instance = new AlbumRepository(albumDataSource, context);
        }
        return instance;
    }

    public void addAlbum(String title, Long coverId, AlbumDataSource.AlbumCallback<Boolean> callback) {
        albumDataSource.addAlbum(title, coverId, new AlbumDataSource.AlbumCallback<Boolean>() {
            @Override
            public void onSuccess(Result<Boolean> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void getAlbumItems(AlbumDataSource.AlbumCallback<List<AlbumItem>> callback) {
        albumDataSource.getAlbumItems(new AlbumDataSource.AlbumCallback<List<AlbumItem>>() {
            @Override
            public void onSuccess(Result<List<AlbumItem>> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void getPhotoItems(long albumId, AlbumDataSource.AlbumCallback<List<PhotoItem>> callback) {
        albumDataSource.getPhotoItems(albumId, new AlbumDataSource.AlbumCallback<List<PhotoItem>>() {
            @Override
            public void onSuccess(Result<List<PhotoItem>> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }


}
