package com.example.smartlockerandroid.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.smartlockerandroid.data.model.Order;
import com.example.smartlockerandroid.data.model.relation.OrderWithBays;
import com.example.smartlockerandroid.data.model.relation.OrderWithCourierAndBays;

import java.util.List;

/**
 * @author itschathurangaj on 5/6/23
 */
@Dao
public interface OrderDao {
    @Insert
    long insert(Order order);

    @Update
    void update(Order order);
    @Delete
    void delete(Order order);

    @Transaction
    @Query("SELECT * FROM order_table WHERE courier_id = :courierId AND order_number = :orderNumber")
    LiveData<OrderWithBays> findByCourierAndOrderNumber(Long courierId, String orderNumber);

    @Query("SELECT * FROM order_table WHERE courier_id = :courierId AND order_number = :orderNumber")
    Order findOrderByCourierAndOrderNumber(Long courierId, String orderNumber);

    @Query("SELECT * FROM order_table WHERE order_id = :orderId")
    Order findOrderById(Long orderId);

    @Transaction
    @Query("SELECT * FROM order_table WHERE status NOT IN ('PICKED', 'CANCELED','INCOMPLETE','LATE') ORDER BY CASE WHEN status = 'LOADED' THEN 0 WHEN status = 'OVERDUE' THEN 1 ELSE 2 END, order_id DESC")
    LiveData<List<OrderWithCourierAndBays>> getAllOrders();

    @Transaction
    @Query("SELECT * FROM order_table WHERE status IN ('PICKED', 'CANCELED','LATE') ORDER BY CASE WHEN status = 'LOADED' THEN 0 WHEN status = 'OVERDUE' THEN 1 ELSE 2 END, order_id DESC;")
    LiveData<List<OrderWithCourierAndBays>> getOrderHistory();

    @Query("SELECT * FROM order_table")
    LiveData<List<OrderWithCourierAndBays>> getAllOrdersMain();
}
