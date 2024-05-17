package com.example.smartlockerandroid.data.model.relation;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.smartlockerandroid.data.model.Courier;
import com.example.smartlockerandroid.data.model.Order;

/**
 * @author itschathurangaj on 5/6/23
 */
public class OrderWithCourier {
    @Embedded
    public Order order;
    @Relation(parentColumn = "courier_id", entityColumn = "id")
    public Courier courier;
}
