package com.example.smartlockerandroid.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.smartlockerandroid.data.model.OrderHistory;
import com.example.smartlockerandroid.data.model.relation.OrderWithBays;
import com.example.smartlockerandroid.data.model.relation.OrderWithCourierAndBays;

import java.util.List;

/**
 * @author itschathurangaj on 5/6/23
 */
@Dao
public interface OrderHistoryDao {
    @Insert
    long insert(OrderHistory order);

    @Update
    void update(OrderHistory order);

    @Transaction
    @Query("SELECT * FROM order_history_table WHERE courier_id = :courierId AND order_number = :orderNumber")
    LiveData<OrderWithBays> findByCourierAndOrderNumber(Long courierId, String orderNumber);

    @Query("SELECT * FROM order_history_table WHERE courier_id = :courierId AND order_number = :orderNumber")
    OrderHistory findOrderByCourierAndOrderNumber(Long courierId, String orderNumber);

    @Query("SELECT * FROM order_history_table WHERE order_id = :orderId")
    OrderHistory findOrderById(Long orderId);

    @Transaction
    @Query("SELECT * FROM order_history_table WHERE status NOT IN ('PICKED', 'CANCELED','INCOMPLETE','LATE') ORDER BY CASE WHEN status = 'LOADED' THEN 0 WHEN status = 'OVERDUE' THEN 1 ELSE 2 END, order_id DESC")
    LiveData<List<OrderWithCourierAndBays>> getAllOrders();

    @Transaction
    @Query("SELECT * FROM order_history_table WHERE status IN ('PICKED', 'CANCELED','LATE') ORDER BY CASE WHEN status = 'LOADED' THEN 0 WHEN status = 'OVERDUE' THEN 1 ELSE 2 END, order_id DESC;")
    LiveData<List<OrderWithCourierAndBays>> getOrderHistory();
}
