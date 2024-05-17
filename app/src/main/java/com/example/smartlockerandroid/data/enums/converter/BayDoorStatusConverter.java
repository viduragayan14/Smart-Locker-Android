package com.example.smartlockerandroid.data.enums.converter;

import androidx.room.TypeConverter;

import com.example.smartlockerandroid.data.enums.BayDoorStatus;

/**
 * @author itschathurangaj on 5/6/23
 */
public class BayDoorStatusConverter {
    @TypeConverter
    public static BayDoorStatus fromString(String value) {
        return value == null ? null : BayDoorStatus.valueOf(value);
    }

    @TypeConverter
    public static String toString(BayDoorStatus value) {
        return value == null ? null : value.name();
    }
}
