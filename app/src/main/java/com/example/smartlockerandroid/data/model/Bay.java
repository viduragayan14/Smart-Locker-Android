package com.example.smartlockerandroid.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.smartlockerandroid.data.enums.BayDoorStatus;
import com.example.smartlockerandroid.data.enums.BayLightStatus;
import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.enums.converter.BayDoorStatusConverter;
import com.example.smartlockerandroid.data.enums.converter.BayLightStatusConverter;
import com.example.smartlockerandroid.data.enums.converter.BayStatusConverter;

/**
 * @author itschathurangaj on 5/6/23
 */
@Entity(tableName = "bay_table")
@TypeConverters({BayStatusConverter.class, BayLightStatusConverter.class, BayDoorStatusConverter.class})
public class Bay {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "bay_id")
    private Long bayId;
    @ColumnInfo(name = "door_status")
    private BayDoorStatus doorStatus;
    private BayStatus status;
    private BayLightStatus led;
    @ColumnInfo(name = "default_id")
    private Integer defaultId;
    @ColumnInfo(name = "calibrated_id")
    private Integer calibratedId;

    public Bay(BayDoorStatus doorStatus, BayStatus status, BayLightStatus led, Integer defaultId, Integer calibratedId) {
        this.doorStatus = doorStatus;
        this.status = status;
        this.led = led;
        this.defaultId = defaultId;
        this.calibratedId = calibratedId;
    }

    public Long getBayId() {
        return bayId;
    }

    public void setBayId(Long bayId) {
        this.bayId = bayId;
    }

    public BayDoorStatus getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(BayDoorStatus doorStatus) {
        this.doorStatus = doorStatus;
    }

    public BayStatus getStatus() {
        return status;
    }

    public void setStatus(BayStatus status) {
        this.status = status;
    }

    public BayLightStatus getLed() {
        return led;
    }

    public void setLed(BayLightStatus led) {
        this.led = led;
    }

    public Integer getDefaultId() {
        return defaultId;
    }

    public void setDefaultId(Integer defaultId) {
        this.defaultId = defaultId;
    }

    public Integer getCalibratedId() {
        return calibratedId;
    }

    public void setCalibratedId(Integer calibratedId) {
        this.calibratedId = calibratedId;
    }
}
