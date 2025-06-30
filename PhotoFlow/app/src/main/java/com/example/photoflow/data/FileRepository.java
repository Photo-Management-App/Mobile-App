package com.example.photoflow.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;

import com.example.photoflow.data.model.PhotoItem;
import com.example.photoflow.data.util.TokenManager;

import java.util.List;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class FileRepository {

    private static volatile FileRepository instance;

    private FileDataSource dataSource;
    private final Context context;

    // private constructor : singleton access
    private FileRepository(FileDataSource dataSource, Context context) {
        this.dataSource = dataSource;
        this.context = context.getApplicationContext(); // Use app context to avoid leaks
    }

    public static FileRepository getInstance(FileDataSource dataSource , Context context) {
        if(instance == null){
            instance = new FileRepository(dataSource, context);
        }
        return instance;
    }

    // Async upload with callback
    public void upload(String base64EncodedFile, String file_name, String title, String description, String coordinates, FileDataSource.FileCallback<Boolean> callback) {
        dataSource.upload(base64EncodedFile, file_name, title, description, coordinates, new FileDataSource.FileCallback<Boolean>() {

            @Override
            public void onSuccess(Result<Boolean> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Result.Error error) {
                callback.onError(error);
            }
        });
    }

    public void getPhotoItems(FileDataSource.FileCallback<List<PhotoItem>> callback) {
        dataSource.getPhotoItems(new FileDataSource.FileCallback<List<PhotoItem>>() {
            @Override
            public void onSuccess(Result<List<PhotoItem>> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Result.Error error) {
                callback.onError(error);
            }
        });
    }

}
