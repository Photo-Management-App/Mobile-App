package com.example.photoflow.data.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {

    public static Bitmap decodeBase64ToBitmap(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static Bitmap decodeBase64ToBitmapWithRotation(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        try {
            // Wrap in stream and get EXIF orientation
            ByteArrayInputStream exifStream = new ByteArrayInputStream(decodedBytes);
            ExifInterface exif = new ExifInterface(exifStream);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            exifStream.close();

            return rotateBitmapIfRequired(bitmap, orientation);
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap; // Return unrotated if error
        }
    }

    private static Bitmap rotateBitmapIfRequired(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    // Convert bitmap to file in cache directory
    public static String saveBitmapToCache(Bitmap bitmap, Context context, String filename) {
    File cacheDir = context.getCacheDir();
    File file = new File(cacheDir, filename);
    try (FileOutputStream fos = new FileOutputStream(file)) {
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        return file.getAbsolutePath();
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    }
}


}
