package com.example.smartlockerandroid.data.enums.converter;

import androidx.room.TypeConverter;

import com.example.smartlockerandroid.data.enums.CourierStatus;

/**
 * @author itschathurangaj on 5/16/23
 */
public class CourierStatusConverter {
    @TypeConverter
    public static CourierStatus fromString(String value) {
        return value == null ? null : CourierStatus.valueOf(value);
    }

    @TypeConverter
    public static String toString(CourierStatus value) {
        return value == null ? null : value.name();
    }
}
