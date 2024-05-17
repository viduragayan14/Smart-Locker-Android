package com.example.smartlockerandroid.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.smartlockerandroid.data.SmartLockerDatabase;
import com.example.smartlockerandroid.data.dao.PickupLogDao;
import com.example.smartlockerandroid.data.model.PickupLog;
import com.example.smartlockerandroid.data.model.User;

import java.util.List;

/**
 * @author itschathurangaj on 6/9/23
 */
public class LogRepository {
    private final PickupLogDao pickupLogDao;

    public LogRepository(Application application) {
        SmartLockerDatabase database = SmartLockerDatabase.getDatabase(application);
        pickupLogDao = database.logDao();
    }

    public void insert(PickupLog pickupLog) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            pickupLogDao.insert(pickupLog);
        });
    }

    public void update(PickupLog pickupLog) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            pickupLogDao.update(pickupLog);
        });
    }

    public void delete(PickupLog pickupLog) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            pickupLogDao.delete(pickupLog);
        });
    }
    public LiveData<List<PickupLog>> getPickUpLogs() {
        return pickupLogDao.getAllLogs();
    }
    public List<PickupLog> getPickUpLogs2() {
        return pickupLogDao.getAllLogs2();
    }
}
