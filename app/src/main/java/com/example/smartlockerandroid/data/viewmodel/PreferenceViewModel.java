package com.example.smartlockerandroid.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.repository.PreferenceRepository;

import java.util.concurrent.ExecutionException;

/**
 * @author itschathurangaj on 5/16/23
 */
public class PreferenceViewModel extends AndroidViewModel {
    private PreferenceRepository preferenceRepository;

    public PreferenceViewModel(@NonNull Application application) {
        super(application);
        preferenceRepository = new PreferenceRepository(application);
    }

    public void insert(Preference preference) {
        preferenceRepository.insert(preference);
    }

    public void update(Preference preference) {
        preferenceRepository.update(preference);
    }

    public LiveData<String> getWelcomeText() {
        return preferenceRepository.getWelcomeText();
    }

    public LiveData<Preference> getPreferenceLiveData() {
        return preferenceRepository.getPreferenceLiveData();
    }

    public Preference getPreference() {
        Preference preference;
        try {
            preference = preferenceRepository.getPreference();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return preference;
    }
}
