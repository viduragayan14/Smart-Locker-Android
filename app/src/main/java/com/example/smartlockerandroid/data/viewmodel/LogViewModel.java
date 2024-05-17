package com.example.smartlockerandroid.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smartlockerandroid.data.model.PickupLog;
import com.example.smartlockerandroid.data.model.User;
import com.example.smartlockerandroid.data.repository.LogRepository;

import java.util.List;

/**
 * @author itschathurangaj on 6/9/23
 */
public class LogViewModel extends AndroidViewModel {
    private LogRepository logRepository;

    public LogViewModel(@NonNull Application application) {
        super(application);
        logRepository = new LogRepository(application);
    }

    public void insert(PickupLog pickupLog) {
        logRepository.insert(pickupLog);
    }

    public void update(PickupLog pickupLog) {
        logRepository.update(pickupLog);
    }

    public void delete(PickupLog pickupLog) {
        logRepository.delete(pickupLog);
    }

    public LiveData<List<PickupLog>> getPickupLogs() {
        return logRepository.getPickUpLogs();
    }
    public List<PickupLog> getPickupLogs2() {
        return logRepository.getPickUpLogs2();
    }

}
