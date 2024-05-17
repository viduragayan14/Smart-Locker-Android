package com.example.smartlockerandroid.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smartlockerandroid.data.SmartLockerDatabase;
import com.example.smartlockerandroid.data.dao.BayDao;
import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.model.Bay;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author itschathurangaj on 5/7/23
 */
public class BayRepository {
    private BayDao bayDao;

    public BayRepository(Application application) {
        SmartLockerDatabase database = SmartLockerDatabase.getDatabase(application);
        bayDao = database.bayDao();
    }

    public LiveData<List<Bay>> getBaysByStatus(BayStatus status) {
        return bayDao.findBaysByStatus(status);
    }

    public List<Bay> getBaysByStatus1(BayStatus status) {

        MutableLiveData<List<Bay>> bays = new MutableLiveData<>();
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            bays.postValue(bayDao.findBaysByStatus1(status));
        });

        return bays.getValue();
    }

    public void insert(Bay bay) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            bayDao.insert(bay);
        });
    }

    public void update(Bay bay) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            bayDao.update(bay);
        });
    }

    public LiveData<Bay> getBayById(Long bayId) {
        MutableLiveData<Bay> bay = new MutableLiveData<>();
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            bay.postValue(bayDao.findBayById(bayId));
        });
        return bay;
    }

    public LiveData<Bay> getOneAvailableBay() {
        MutableLiveData<Bay> bay = new MutableLiveData<>();
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            bay.postValue(bayDao.findOneAvailableBay());
        });
        return bay;
    }

    public LiveData<List<Bay>> getRequiredBaysLiveData(BayStatus status, Integer numOfBaysSelected) {
        return bayDao.fetchRequiredBaysLiveData(status, numOfBaysSelected);
    }

    public LiveData<List<Bay>> getAllBays() {
        return bayDao.getAllBays();
    }

    public LiveData<List<Bay>> getAllBaysMain() {
        return bayDao.getAllBaysMain();
    }

    public LiveData<Bay> getServiceBayLiveData() {
        return bayDao.getServiceBayLiveData();
    }

    public Integer getNumberOfAvailableBays() throws ExecutionException, InterruptedException {
        Future<Integer> integerFuture = SmartLockerDatabase.databaseWriteExecutor.submit(() -> bayDao.getNumberOfAvailableBays());
        return integerFuture.get();
    }

    public List<Bay> getRequiredBays(BayStatus status, Integer numOfBaysSelected) throws ExecutionException, InterruptedException {
        Future<List<Bay>> listFuture = SmartLockerDatabase.databaseWriteExecutor.submit(() -> bayDao.fetchRequiredBays(status, numOfBaysSelected));
        return listFuture.get();
    }

    //delete all bays
    public void deleteAllBays() {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            bayDao.deleteAllBays();
        });
    }

    public Integer getBayCount() throws ExecutionException, InterruptedException {
        Future<Integer> bayCount = SmartLockerDatabase.databaseWriteExecutor.submit(() -> bayDao.getBayCount());
        return bayCount.get();
    }

    //save list of bays
    public void addNewListOfBays(List<Bay> bays) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            bayDao.deleteAndInsertAll(bays);
        });
    }

    public Bay getBayByCalibratedId(Integer calibratedId) throws ExecutionException, InterruptedException {
        Future<Bay> bayFuture = SmartLockerDatabase.databaseWriteExecutor.submit(() -> bayDao.findBayByCalibratedId(calibratedId));
        return bayFuture.get();
    }

    public Bay getServiceBay() throws ExecutionException, InterruptedException {
        Future<Bay> bayFuture = SmartLockerDatabase.databaseWriteExecutor.submit(() -> bayDao.getServiceBay());
        return bayFuture.get();
    }
}
