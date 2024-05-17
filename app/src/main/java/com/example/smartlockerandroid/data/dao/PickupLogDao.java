package com.example.smartlockerandroid.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.PickupLog;
import com.example.smartlockerandroid.data.model.User;

import java.util.List;

/**
 * @author itschathurangaj on 6/9/23
 */
@Dao
public interface PickupLogDao {
    @Insert
    void insert(PickupLog user);

    @Update
    void update(PickupLog user);

    @Delete
    void delete(PickupLog user);

    @Query("SELECT * FROM pickuplog WHERE status = 0")
    LiveData<List<PickupLog>> getAllLogs();

    @Query("SELECT * FROM pickuplog WHERE status = 0")
    List<PickupLog> getAllLogs2();

}
