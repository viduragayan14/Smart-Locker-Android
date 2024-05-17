package com.example.smartlockerandroid.data.enums;

/**
 * @author itschathurangaj on 5/6/23
 */
public enum BayLightStatus {
    OFF(240),
    RED(241),
    GREEN(242),
    YELLOW(243);

    private Integer value;

    private BayLightStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
