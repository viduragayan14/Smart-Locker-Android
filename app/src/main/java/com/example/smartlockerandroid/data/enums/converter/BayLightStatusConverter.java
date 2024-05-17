package com.example.smartlockerandroid.data.enums.converter;

import androidx.room.TypeConverter;

import com.example.smartlockerandroid.data.enums.BayLightStatus;

/**
 * @author itschathurangaj on 5/6/23
 */
public class BayLightStatusConverter {
    @TypeConverter
    public static BayLightStatus fromString(String value) {
        return value == null ? null : BayLightStatus.valueOf(value);
    }

    @TypeConverter
    public static String toString(BayLightStatus value) {
        return value == null ? null : value.name();
    }
}
