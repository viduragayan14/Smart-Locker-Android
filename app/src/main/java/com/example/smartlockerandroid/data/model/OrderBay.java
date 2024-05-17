package com.example.smartlockerandroid.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

/**
 * @author itschathurangaj on 5/6/23
 */
@Entity(tableName = "order_bay_table", primaryKeys = {"order_id", "bay_id"}, indices = {@Index("bay_id")})
public class OrderBay {
    @ColumnInfo(name = "order_id")
    @NonNull
    private Long orderId;
    @ColumnInfo(name = "bay_id")
    @NonNull
    private Long bayId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getBayId() {
        return bayId;
    }

    public void setBayId(Long bayId) {
        this.bayId = bayId;
    }
}

