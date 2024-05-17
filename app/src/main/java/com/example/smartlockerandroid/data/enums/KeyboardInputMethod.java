package com.example.smartlockerandroid.data.enums;

/**
 * @author itschathurangaj on 6/27/23
 */
public enum KeyboardInputMethod {
    ALPHANUMERIC(true),
    NUMERIC(false);

    private final boolean value;

    KeyboardInputMethod(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return this.value;
    }
}
