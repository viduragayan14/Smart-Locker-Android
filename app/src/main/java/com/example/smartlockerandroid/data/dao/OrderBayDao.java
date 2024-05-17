package com.example.smartlockerandroid.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartlockerandroid.data.model.OrderBay;
import com.example.smartlockerandroid.data.model.relation.OrderWithCourierAndBays;

import java.util.List;

/**
 * @author itschathurangaj on 5/7/23
 */
@Dao
public interface OrderBayDao {
    @Insert
    void insert(OrderBay orderBay);

    @Update
    void update(OrderBay orderBay);

    @Delete
    void delete(OrderBay orderBay);

    @Query("SELECT * FROM order_bay_table")
    LiveData<List<OrderBay>> getAllOrderBayMain();
}
