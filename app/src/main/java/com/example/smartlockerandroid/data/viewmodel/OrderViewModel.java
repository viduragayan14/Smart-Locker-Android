package com.example.smartlockerandroid.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smartlockerandroid.data.model.Order;
import com.example.smartlockerandroid.data.model.relation.OrderWithBays;
import com.example.smartlockerandroid.data.model.relation.OrderWithCourierAndBays;
import com.example.smartlockerandroid.data.repository.OrderRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author itschathurangaj on 5/7/23
 */
public class OrderViewModel extends AndroidViewModel {
    private OrderRepository orderRepository;

    public OrderViewModel(@NonNull Application application) {
        super(application);
        orderRepository = new OrderRepository(application);
    }

    public LiveData<Long> insert(Order order) {
        return orderRepository.insert(order);
    }

    public void update(Order order) {
        orderRepository.update(order);
    }

    public void delete(Order order) {
        orderRepository.delete(order);
    }

    public LiveData<OrderWithBays> getOrderByCourierAndOrderNumber(Long courierId, String orderNumber) {
        return orderRepository.getOrderByCourierAndOrderNumber(courierId, orderNumber);
    }

    public LiveData<Order> getOrderByIdLiveData(Long orderId) {
        return orderRepository.getOrderByIdLiveData(orderId);
    }

    public LiveData<List<OrderWithCourierAndBays>> getAllOrders() {
        return orderRepository.getAllOrders();
    }

    public LiveData<List<OrderWithCourierAndBays>> getOrderHistory() {
        return orderRepository.getOrderHistory();
    }

    public Order getOrderById(Long orderId) {
        try {
            return orderRepository.getOrderById(orderId);
        } catch (ExecutionException | InterruptedException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return null;
    }

    public Order findOrderByCourierAndOrderNumber(Long courierId, String orderNumber) {
        try {
            return orderRepository.findOrderByCourierAndOrderNumber(courierId, orderNumber);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
