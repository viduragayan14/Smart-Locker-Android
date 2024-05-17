package com.example.smartlockerandroid.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartlockerandroid.data.SmartLockerDatabase;
import com.example.smartlockerandroid.data.dao.OrderHistoryDao;
import com.example.smartlockerandroid.data.model.OrderHistory;
import com.example.smartlockerandroid.data.model.relation.OrderWithBays;
import com.example.smartlockerandroid.data.model.relation.OrderWithCourierAndBays;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author itschathurangaj on 5/7/23
 */
public class OrderHistoryRepository {
    private OrderHistoryDao orderDao;

    public OrderHistoryRepository(Application application) {
        SmartLockerDatabase database = SmartLockerDatabase.getDatabase(application);
        orderDao = database.orderHistoryDao();
    }

    public LiveData<Long> insert(OrderHistory order) {
        MutableLiveData<Long> insertedId = new MutableLiveData<>();
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            insertedId.postValue(orderDao.insert(order));
        });
        return insertedId;
    }

    public void update(OrderHistory order) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            orderDao.update(order);
        });
    }

    public LiveData<OrderWithBays> getOrderByCourierAndOrderNumber(Long courierId, String orderNumber) {
        return orderDao.findByCourierAndOrderNumber(courierId, orderNumber);
    }

    public LiveData<OrderHistory> getOrderByIdLiveData(Long orderId) {
        MutableLiveData<OrderHistory> order = new MutableLiveData<>();
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            order.postValue(orderDao.findOrderById(orderId));
        });
        return order;
    }

    public LiveData<List<OrderWithCourierAndBays>> getAllOrders() {
        return orderDao.getAllOrders();
    }

    public LiveData<List<OrderWithCourierAndBays>> getOrderHistory() {
        return orderDao.getOrderHistory();
    }

    public OrderHistory getOrderById(Long orderId) throws ExecutionException, InterruptedException {
        Future<OrderHistory> orderFuture = SmartLockerDatabase.databaseWriteExecutor.submit(() -> orderDao.findOrderById(orderId));
        return orderFuture.get();
    }

    public OrderHistory findOrderByCourierAndOrderNumber(Long courierId, String orderNumber) throws ExecutionException, InterruptedException {
        Future<OrderHistory> orderFuture = SmartLockerDatabase.databaseWriteExecutor.submit(() -> orderDao.findOrderByCourierAndOrderNumber(courierId, orderNumber));
        return orderFuture.get();
    }
}
