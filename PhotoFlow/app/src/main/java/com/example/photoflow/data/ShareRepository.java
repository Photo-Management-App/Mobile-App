package com.example.photoflow.data;

import android.content.Context;

import com.example.photoflow.R;

public class ShareRepository
{

    private static volatile ShareRepository instance;

    private final ShareDataSource shareDataSource;
    private final Context context;

    // private constructor : singleton access
    public ShareRepository(ShareDataSource shareDataSource, Context context) {
        this.shareDataSource = shareDataSource;
        this.context = context.getApplicationContext(); // Use app context to avoid leaks
    }

    public static ShareRepository getInstance(Context context) {
        if (instance == null) {
            String baseUrl = context.getString(R.string.base_url);
            ShareDataSource shareDataSource = new ShareDataSource(context);
            instance = new ShareRepository(shareDataSource, context);
        }
        return instance;
    }

    public void shareFile(long photoId, ShareDataSource.ShareCallback<String> callback) {
        shareDataSource.shareFile(photoId, new ShareDataSource.ShareCallback<String>() {
            @Override
            public void onSuccess(Result<String> result) {
                callback.onSuccess(result);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

}
