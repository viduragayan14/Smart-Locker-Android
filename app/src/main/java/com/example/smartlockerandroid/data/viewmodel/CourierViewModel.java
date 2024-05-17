package com.example.smartlockerandroid.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smartlockerandroid.data.model.Courier;
import com.example.smartlockerandroid.data.repository.CourierRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author itschathurangaj on 5/6/23
 */
public class CourierViewModel extends AndroidViewModel {
    private final LiveData<List<Courier>> couriers;
    private final LiveData<List<Courier>> activeCouriers;
    private CourierRepository courierRepository;

    public CourierViewModel(@NonNull Application application) {
        super(application);
        courierRepository = new CourierRepository(application);
        couriers = courierRepository.getAllCouriers();
        activeCouriers = courierRepository.getAllActiveCouriers();
    }

    public LiveData<List<Courier>> getCouriers() {
        return couriers;
    }

    public LiveData<List<Courier>> getActiveCouriers() {
        return activeCouriers;
    }

    public void insert(Courier courier) {
        courierRepository.insert(courier);
    }

    public void update(Courier courier) {
        courierRepository.update(courier);
    }
    public void updateList(List<Courier> courier) {
        courierRepository.updateList(courier);
    }

    public LiveData<Courier> getCourierById(Long id) {
        return courierRepository.getCourierById(id);
    }

    public Courier findCourierById(Long id) {
        try {
            return courierRepository.findCourierById(id);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Courier findCourierByName(String name) {
        try {
            return courierRepository.findCourierByName(name);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void deleteCourier(Long userId) {
        courierRepository.deleteCourier(userId);
    }
}
