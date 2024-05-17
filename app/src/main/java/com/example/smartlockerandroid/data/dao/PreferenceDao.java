package com.example.smartlockerandroid.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartlockerandroid.data.model.Preference;

/**
 * @author itschathurangaj on 5/16/23
 */
@Dao
public interface PreferenceDao {
    @Insert
    void insert(Preference preference);

    @Update
    void update(Preference preference);

    @Query("SELECT welcome_text FROM preference_table")
    LiveData<String> getWelcomeText();

    @Query("SELECT * FROM preference_table WHERE preference_id = 1")
    LiveData<Preference> getPreferenceLiveData();

    @Query("SELECT * FROM preference_table WHERE preference_id = 1")
    Preference getPreference();
}
