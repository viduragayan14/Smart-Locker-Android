package com.example.smartlockerandroid.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.smartlockerandroid.data.SmartLockerDatabase;
import com.example.smartlockerandroid.data.dao.CourierDao;
import com.example.smartlockerandroid.data.model.Courier;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author itschathurangaj on 5/5/23
 */
public class CourierRepository {
    private CourierDao courierDao;
    private LiveData<List<Courier>> allCouriers;
    private LiveData<List<Courier>> allActiveCouriers;

    public CourierRepository(Application application) {
        SmartLockerDatabase database = SmartLockerDatabase.getDatabase(application);
        courierDao = database.courierDao();
        allCouriers = courierDao.getAllCouriers();
        allActiveCouriers = courierDao.getAllActiveCouriers();
    }

    public LiveData<List<Courier>> getAllCouriers() {
        return allCouriers;
    }

    public LiveData<List<Courier>> getAllActiveCouriers() {
        return allActiveCouriers;
    }

    public void insert(Courier courier) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            courierDao.insert(courier);
        });
    }

    public void update(Courier courier) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            courierDao.update(courier);
        });

    }

    public void updateList(List<Courier> courier) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            courierDao.updateCouriers(courier);
        });

    }

    public void delete(Courier courier) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            courierDao.delete(courier);
        });
    }

    public LiveData<Courier> getCourierById(Long id) {
        return courierDao.getCourierById(id);
    }

    public Courier findCourierById(Long id) throws ExecutionException, InterruptedException {
        Future<Courier> courierFuture = SmartLockerDatabase.databaseWriteExecutor.submit(() -> courierDao.findCourierById(id));
        return courierFuture.get();
    }

    public Courier findCourierByName(String name) throws ExecutionException, InterruptedException {
        Future<Courier> courierFuture = SmartLockerDatabase.databaseWriteExecutor.submit(() -> courierDao.findCourierByName(name));
        return courierFuture.get();
    }

    public void deleteCourier(Long userId) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            courierDao.deleteCourier(userId);
        });
    }
}
