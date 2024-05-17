package com.example.smartlockerandroid.data.model.relation;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.Courier;
import com.example.smartlockerandroid.data.model.Order;
import com.example.smartlockerandroid.data.model.OrderBay;

import java.util.List;

/**
 * @author itschathurangaj on 6/30/23
 */
public class OrderWithCourierAndBays {
    @Embedded
    public Order order;

    @Relation(parentColumn = "courier_id", entityColumn = "courier_id")
    public Courier courier;

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
}
