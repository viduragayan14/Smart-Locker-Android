package com.example.smartlockerandroid.data.enums.converter;

import androidx.room.TypeConverter;

import com.example.smartlockerandroid.data.enums.KeyboardInputMethod;

/**
 * @author itschathurangaj on 6/27/23
 */
public class KeyboardInputMethodConverter {
    @TypeConverter
    public static KeyboardInputMethod fromString(String value) {
        return value == null ? null : KeyboardInputMethod.valueOf(value);
    }

    @TypeConverter
    public static String toString(KeyboardInputMethod value) {
        return value == null ? null : value.name();
    }
}
