package com.example.smartlockerandroid.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.smartlockerandroid.data.model.Courier;

import java.util.List;

/**
 * @author itschathurangaj on 5/5/23
 */
@Dao
public interface CourierDao {
    @Insert
    void insert(Courier courier);

    @Update
    void update(Courier courier);

    @Transaction
    default void updateCouriers(List<Courier> couriers) {
        for (Courier courier : couriers) {
            update(courier);
        }
    }

    @Delete
    void delete(Courier courier);

    @Query("DELETE FROM courier_table")
    void deleteAllCouriers();

    @Query("DELETE from courier_table WHERE courier_id= :userId")
    void deleteCourier(Long userId);

    @Query("SELECT * FROM courier_table WHERE status = 'ACTIVE' ORDER BY position ASC")
    LiveData<List<Courier>> getAllActiveCouriers();

    @Query("SELECT * FROM courier_table ORDER BY position ASC")
    LiveData<List<Courier>> getAllCouriers();

    @Query("SELECT * FROM courier_table WHERE courier_id = :id")
    LiveData<Courier> getCourierById(Long id);

    @Query("SELECT * FROM courier_table WHERE courier_id = :id LIMIT 1")
    Courier findCourierById(Long id);

    @Query("SELECT * FROM courier_table WHERE name = :name LIMIT 1")
    Courier findCourierByName(String name);
}
