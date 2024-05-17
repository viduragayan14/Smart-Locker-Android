package com.example.smartlockerandroid.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.smartlockerandroid.data.enums.UserRole;
import com.example.smartlockerandroid.data.enums.converter.UserRoleConverter;

/**
 * @author itschathurangaj on 6/9/23
 */
@Entity(tableName = "pickuplog")
public class PickupLog {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long id;
    @ColumnInfo(name = "type")
    private String type;
    @ColumnInfo(name = "orderId")
    private Long orderId;

    @ColumnInfo(name = "bayIds")
    private String bayIds;
    private Long createdDate;
    private boolean status;
    private boolean executedStatus;

    public PickupLog() {
    }

    @Ignore
    public PickupLog(String type, Long orderId, String bayIds,Long createDate,boolean status,boolean executedStatus) {
        this.type = type;
        this.orderId = orderId;
        this.bayIds = bayIds;
        this.createdDate = createDate;
        this.status = status;
        this.executedStatus = executedStatus;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getBayIds() {
        return bayIds;
    }

    public void setBayIds(String bayIds) {
        this.bayIds = bayIds;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isExecutedStatus() {
        return executedStatus;
    }

    public void setExecutedStatus(boolean executedStatus) {
        this.executedStatus = executedStatus;
    }
}
