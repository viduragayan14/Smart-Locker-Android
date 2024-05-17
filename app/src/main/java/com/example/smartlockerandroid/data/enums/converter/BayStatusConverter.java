package com.example.smartlockerandroid.data.enums.converter;

import androidx.room.TypeConverter;

import com.example.smartlockerandroid.data.enums.BayStatus;

/**
 * @author itschathurangaj on 5/6/23
 */
public class BayStatusConverter {
    @TypeConverter
    public static BayStatus fromString(String value) {
        return value == null ? null : BayStatus.valueOf(value);
    }

    @TypeConverter
    public static String toString(BayStatus value) {
        return value == null ? null : value.name();
    }
}
