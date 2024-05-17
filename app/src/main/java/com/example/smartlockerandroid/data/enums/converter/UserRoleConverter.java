package com.example.smartlockerandroid.data.enums.converter;

import androidx.room.TypeConverter;

import com.example.smartlockerandroid.data.enums.UserRole;

/**
 * @author itschathurangaj on 6/9/23
 */
public class UserRoleConverter {
    @TypeConverter
    public static UserRole fromString(String value) {
        return value == null ? null : UserRole.valueOf(value);
    }

    @TypeConverter
    public static String toString(UserRole value) {
        return value == null ? null : value.name();
    }
}
