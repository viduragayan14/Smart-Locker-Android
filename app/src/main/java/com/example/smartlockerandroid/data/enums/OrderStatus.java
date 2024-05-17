package com.example.smartlockerandroid.data.enums;

/**
 * @author itschathurangaj on 5/6/23
 */
public enum OrderStatus {
    INCOMPLETE("Incomplete"),
    BAYS_ASSIGNED("Bays Assigned"),
    LOADED("Loaded"),
    PICKED("Picked"),
    CANCELED("Canceled"),
    OVERDUE("Overdue"),
    LATE("Late");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
