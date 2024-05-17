package com.example.smartlockerandroid.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.repository.BayRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author itschathurangaj on 5/7/23
 */
public class BayViewModel extends AndroidViewModel {
    private BayRepository bayRepository;

    public BayViewModel(@NonNull Application application) {
        super(application);
        bayRepository = new BayRepository(application);
    }

    public void insert(Bay bay) {
        bayRepository.insert(bay);
    }

    public void update(Bay bay) {
        bayRepository.update(bay);
    }

    public LiveData<List<Bay>> getBaysByStatus(BayStatus status) {
        return bayRepository.getBaysByStatus(status);
    }

    public List<Bay> getBaysByStatus1(BayStatus status) {
        return bayRepository.getBaysByStatus1(status);
    }

    public LiveData<Bay> getBayById(Long bayId) {
        return bayRepository.getBayById(bayId);
    }

    public LiveData<Bay> getOneAvailableBay() {
        return bayRepository.getOneAvailableBay();
    }

    public LiveData<List<Bay>> getRequiredBaysLiveData(BayStatus status, Integer numOfBaysSelected) {
        return bayRepository.getRequiredBaysLiveData(status, numOfBaysSelected);
    }

    public LiveData<List<Bay>> getAllBays() {
        return bayRepository.getAllBays();
    }

    public LiveData<List<Bay>> getAllBaysMain() {
        return bayRepository.getAllBaysMain();
    }

    public LiveData<Bay> getServiceBayLiveData() {
        return bayRepository.getServiceBayLiveData();
    }

    public Integer getNumberOfAvailableBays() {
        try {
            return bayRepository.getNumberOfAvailableBays();
        } catch (ExecutionException | InterruptedException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return null;
    }

    public List<Bay> getRequiredBays(BayStatus status, Integer numOfBaysSelected) {
        try {
            return bayRepository.getRequiredBays(status, numOfBaysSelected);
        } catch (ExecutionException | InterruptedException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return null;
    }

    //delete all bays
    public void deleteAllBays() {
        bayRepository.deleteAllBays();
    }

    public Integer getBayCount() {
        try {
            return bayRepository.getBayCount();
        } catch (ExecutionException | InterruptedException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return null;
    }

    //save list of bays
    public void addNewListOfBays(List<Bay> bays) {
        bayRepository.addNewListOfBays(bays);
    }

    public Bay getBayByCalibratedId(Integer calibratedId) {
        try {
            return bayRepository.getBayByCalibratedId(calibratedId);
        } catch (ExecutionException | InterruptedException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return null;
    }

    public Bay getServiceBay() {
        try {
            return bayRepository.getServiceBay();
        } catch (ExecutionException | InterruptedException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return null;
    }
}
