package com.example.smartlockerandroid.data.enums;

/**
 * @author itschathurangaj on 5/16/23
 */
public enum CourierStatus {
    ACTIVE(true),
    INACTIVE(false);

    private final boolean value;

    CourierStatus(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }
}
