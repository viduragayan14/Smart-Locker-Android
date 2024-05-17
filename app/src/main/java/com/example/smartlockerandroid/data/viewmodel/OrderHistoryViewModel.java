package com.example.smartlockerandroid.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smartlockerandroid.data.model.OrderHistory;
import com.example.smartlockerandroid.data.model.relation.OrderWithBays;
import com.example.smartlockerandroid.data.model.relation.OrderWithCourierAndBays;
import com.example.smartlockerandroid.data.repository.OrderHistoryRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author itschathurangaj on 5/7/23
 */
public class OrderHistoryViewModel extends AndroidViewModel {
    private OrderHistoryRepository orderRepository;

    public OrderHistoryViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderHistoryRepository(application);
    }

    public LiveData<Long> insert(OrderHistory order) {
        return orderRepository.insert(order);
    }

    public void update(OrderHistory order) {
        orderRepository.update(order);
    }

    public LiveData<OrderWithBays> getOrderByCourierAndOrderNumber(Long courierId, String orderNumber) {
        return orderRepository.getOrderByCourierAndOrderNumber(courierId, orderNumber);
    }

    public LiveData<OrderHistory> getOrderByIdLiveData(Long orderId) {
        return orderRepository.getOrderByIdLiveData(orderId);
    }

    public LiveData<List<OrderWithCourierAndBays>> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public LiveData<List<OrderWithCourierAndBays>> getOrderHistory() {
        return orderRepository.getOrderHistory();
    }

    public OrderHistory getOrderById(Long orderId) {
        try {
            return orderRepository.getOrderById(orderId);
        } catch (ExecutionException | InterruptedException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return null;
    }

    public OrderHistory findOrderByCourierAndOrderNumber(Long courierId, String orderNumber) {
        try {
            return orderRepository.findOrderByCourierAndOrderNumber(courierId, orderNumber);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
