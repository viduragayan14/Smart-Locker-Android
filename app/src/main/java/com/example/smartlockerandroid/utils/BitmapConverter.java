package com.example.smartlockerandroid.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.TypeConverter;

import java.io.ByteArrayOutputStream;

/**
 * @author itschathurangaj on 7/6/23
 */
public class BitmapConverter {
    @TypeConverter
    public static byte[] fromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @TypeConverter
    public static Bitmap toBitmap(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
