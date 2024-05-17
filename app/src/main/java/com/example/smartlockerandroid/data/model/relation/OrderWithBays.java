package com.example.smartlockerandroid.data.model.relation;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.Order;
import com.example.smartlockerandroid.data.model.OrderBay;

import java.util.List;

/**
 * @author itschathurangaj on 5/6/23
 */
public class OrderWithBays {
    @Embedded
    private Order order;
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

    public List<Bay> getBays() {
        return bays;
    }

    public void setBays(List<Bay> bays) {
        this.bays = bays;
    }
}
