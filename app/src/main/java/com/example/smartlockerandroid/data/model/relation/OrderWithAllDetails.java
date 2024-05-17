package com.example.smartlockerandroid.data.model.relation;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.Courier;
import com.example.smartlockerandroid.data.model.Order;
import com.example.smartlockerandroid.data.model.OrderBay;
import com.example.smartlockerandroid.data.model.User;

import java.util.List;

/**
 * @author itschathurangaj on 7/2/23
 */
public class OrderWithAllDetails {
    @Embedded
    public Order order;

    @Relation(parentColumn = "courier_id", entityColumn = "courier_id")
    public Courier courier;

    @Relation(parentColumn = "user_id", entityColumn = "loaded_by")
    public User loadedBy;

    @Relation(parentColumn = "user_id", entityColumn = "canceled_by")
    public User canceledBy;

    @Relation(parentColumn = "user_id", entityColumn = "reloaded_by")
    public User reloadedBy;

    @Relation(
            parentColumn = "order_id",
            entityColumn = "bay_id",
            associateBy = @Junction(OrderBay.class)
    )
    private List<Bay> bays;

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    public List<Bay> getBays() {
        return bays;
    }

    public void setBays(List<Bay> bays) {
        this.bays = bays;
    }

    public User getLoadedBy() {
        return loadedBy;
    }

    public void setLoadedBy(User loadedBy) {
        this.loadedBy = loadedBy;
    }

    public User getCanceledBy() {
        return canceledBy;
    }

    public void setCanceledBy(User canceledBy) {
        this.canceledBy = canceledBy;
    }

    public User getReloadedBy() {
        return reloadedBy;
    }

    public void setReloadedBy(User reloadedBy) {
        this.reloadedBy = reloadedBy;
    }
}
