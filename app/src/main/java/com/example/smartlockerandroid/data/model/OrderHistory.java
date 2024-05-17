package com.example.smartlockerandroid.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.smartlockerandroid.data.enums.OrderStatus;
import com.example.smartlockerandroid.data.enums.converter.OrderStatusConverter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author itschathurangaj on 5/6/23
 */
@Entity(tableName = "order_history_table",
        foreignKeys = {
                @ForeignKey(entity = Courier.class,
                        parentColumns = "courier_id",
                        childColumns = "courier_id"),
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "loaded_by"),
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "reloaded_by"),
                @ForeignKey(entity = User.class,
                        parentColumns = "user_id",
                        childColumns = "canceled_by")
        },
        indices = {
                @Index("courier_id"),
                @Index("loaded_by"),
                @Index("reloaded_by"),
                @Index("canceled_by")
        }
)
@TypeConverters({OrderStatusConverter.class})
public class OrderHistory {
    @PrimaryKey
    @ColumnInfo(name = "order_id")
    private Long orderId;
    private String barcode;
    @ColumnInfo(name = "order_number")
    private String orderNumber;
    @ColumnInfo(name = "courier_id")
    private Long courierId;
    @ColumnInfo(name = "no_of_bays")
    private Integer noOfBays;
    private OrderStatus status;
    @ColumnInfo(name = "loaded_at") //, defaultValue = "CURRENT_TIMESTAMP"
    private Long loadedAt;
    @ColumnInfo(name = "reloaded_at")
    private Long reloadedAt;
    @ColumnInfo(name = "canceled_at")
    private Long canceledAt;
    @ColumnInfo(name = "updated_at") //, defaultValue = "(strftime('%s', 'now') * 1000)"
    private Long updatedAt;
    @ColumnInfo(name = "picked_up_at")
    private Long pickedUpAt;
    @ColumnInfo(name = "loaded_by")
    private Long loadedBy;
    @ColumnInfo(name = "loaded_by_name")
    private String loadedByName;
    @ColumnInfo(name = "reloaded_by")
    private Long reloadedBy;
    @ColumnInfo(name = "canceled_by")
    private Long canceledBy;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getLoadedByName() {
        return loadedByName;
    }

    public void setLoadedByName(String loadedByName) {
        this.loadedByName = loadedByName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getCourierId() {
        return courierId;
    }

    public void setCourierId(Long courierId) {
        this.courierId = courierId;
    }

    public Integer getNoOfBays() {
        return noOfBays;
    }

    public void setNoOfBays(Integer noOfBays) {
        this.noOfBays = noOfBays;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Long getLoadedAt() {
        return loadedAt;
    }

    public void setLoadedAt(Long loadedAt) {
        this.loadedAt = loadedAt;
    }

    public Long getReloadedAt() {
        return reloadedAt;
    }

    public void setReloadedAt(Long reloadedAt) {
        this.reloadedAt = reloadedAt;
    }

    public Long getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(Long canceledAt) {
        this.canceledAt = canceledAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getPickedUpAt() {
        return pickedUpAt;
    }

    public void setPickedUpAt(Long pickedUpAt) {
        this.pickedUpAt = pickedUpAt;
    }

    public Long getLoadedBy() {
        return loadedBy;
    }

    public void setLoadedBy(Long loadedBy) {
        this.loadedBy = loadedBy;
    }

    public Long getReloadedBy() {
        return reloadedBy;
    }

    public void setReloadedBy(Long reloadedBy) {
        this.reloadedBy = reloadedBy;
    }

    public Long getCanceledBy() {
        return canceledBy;
    }

    public void setCanceledBy(Long canceledBy) {
        this.canceledBy = canceledBy;
    }

    public String convertDateToString(Long timestamp) {
        return new SimpleDateFormat("EEE dd MMM hh:mm a", Locale.US).format(new Date(timestamp));
    }

    public String getLoadedAtFormattedDate() {
        return convertDateToString(loadedAt);
    }
}
