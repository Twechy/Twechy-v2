package com.twechy.twechy_v2.Database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class DbBitmapUtility {


    public static byte[] getBytes(Bitmap bitmap, ImageView imageView) {

        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        Log.i("Debug", "PhotoUploaded");

        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {

        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

}
