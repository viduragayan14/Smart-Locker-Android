package com.example.smartlockerandroid.data.repository;

import android.app.Application;

import com.example.smartlockerandroid.data.SmartLockerDatabase;
import com.example.smartlockerandroid.data.dao.OrderBayDao;
import com.example.smartlockerandroid.data.model.OrderBay;

/**
 * @author itschathurangaj on 5/8/23
 */
public class OrderBayRepository {
    private OrderBayDao orderBayDao;

    public OrderBayRepository(Application application) {
        SmartLockerDatabase database = SmartLockerDatabase.getDatabase(application);
        orderBayDao = database.orderBayDao();
    }

    public void insert(OrderBay orderBay) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            orderBayDao.insert(orderBay);
        });
    }
}
