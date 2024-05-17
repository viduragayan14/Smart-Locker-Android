package com.example.smartlockerandroid.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.smartlockerandroid.data.model.OrderBay;
import com.example.smartlockerandroid.data.repository.OrderBayRepository;

/**
 * @author itschathurangaj on 5/8/23
 */
public class OrderBayViewModel extends AndroidViewModel {
    private OrderBayRepository orderBayRepository;

    public OrderBayViewModel(@NonNull Application application) {
        super(application);
        orderBayRepository = new OrderBayRepository(application);
    }

    public void insert(OrderBay orderBay) {
        orderBayRepository.insert(orderBay);
    }
}
