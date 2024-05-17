package com.example.smartlockerandroid.data.enums;

/**
 * @author itschathurangaj on 5/6/23
 */
public enum BayDoorStatus {
    OPEN(241),
    CLOSE(240);

    private Integer value;

    private BayDoorStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
