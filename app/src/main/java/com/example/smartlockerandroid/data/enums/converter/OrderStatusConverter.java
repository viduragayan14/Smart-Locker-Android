package com.example.smartlockerandroid.data.enums.converter;

import androidx.room.TypeConverter;

import com.example.smartlockerandroid.data.enums.OrderStatus;

/**
 * @author itschathurangaj on 5/6/23
 */
public class OrderStatusConverter {
    @TypeConverter
    public static OrderStatus fromString(String value) {
        return value == null ? null : OrderStatus.valueOf(value);
    }

    @TypeConverter
    public static String toString(OrderStatus value) {
        return value == null ? null : value.name();
    }
}
