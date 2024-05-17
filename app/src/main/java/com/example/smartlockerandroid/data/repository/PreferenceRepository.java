package com.example.smartlockerandroid.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.smartlockerandroid.data.SmartLockerDatabase;
import com.example.smartlockerandroid.data.dao.PreferenceDao;
import com.example.smartlockerandroid.data.model.Preference;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author itschathurangaj on 5/16/23
 */
public class PreferenceRepository {
    private PreferenceDao preferenceDao;

    public PreferenceRepository(Application application) {
        SmartLockerDatabase database = SmartLockerDatabase.getDatabase(application);
        preferenceDao = database.preferenceDao();
    }

    public void insert(Preference preference) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            preferenceDao.insert(preference);
        });
    }

    public void update(Preference preference) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            preferenceDao.update(preference);
        });
    }

    public LiveData<String> getWelcomeText() {
        return preferenceDao.getWelcomeText();
    }

    public LiveData<Preference> getPreferenceLiveData() {
        return preferenceDao.getPreferenceLiveData();
    }

    ;

    public Preference getPreference() throws ExecutionException, InterruptedException {
        Future<Preference> preferenceFuture = SmartLockerDatabase.databaseWriteExecutor.submit(() -> preferenceDao.getPreference());
        return preferenceFuture.get();
    }
}
