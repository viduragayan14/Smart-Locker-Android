package com.example.smartlockerandroid.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.model.Bay;

import java.util.List;

/**
 * @author itschathurangaj on 5/6/23
 */
@Dao
public abstract class BayDao {
    @Insert
    public abstract void insert(Bay bay);

    @Update
    public abstract void update(Bay bay);

    @Delete
    public abstract void delete(Bay bay);

    @Query("SELECT * FROM bay_table WHERE status = :bayStatus")
    public abstract LiveData<List<Bay>> findBaysByStatus(BayStatus bayStatus);
    @Query("SELECT * FROM bay_table")
    public abstract LiveData<List<Bay>> getAllBaysMain();

    @Query("SELECT * FROM bay_table WHERE status = :bayStatus")
    public abstract List<Bay> findBaysByStatus1(BayStatus bayStatus);

    @Query("SELECT * FROM bay_table WHERE bay_id = :bayId")
    public abstract Bay findBayById(Long bayId);

    @Query("SELECT * FROM bay_table WHERE status = 'AVAILABLE' LIMIT 1")
    public abstract Bay findOneAvailableBay();

    @Query("SELECT * FROM bay_table WHERE status = :bayStatus LIMIT :numOfBaysSelected")
    public abstract LiveData<List<Bay>> fetchRequiredBaysLiveData(BayStatus bayStatus, Integer numOfBaysSelected);

    @Query("SELECT * FROM bay_table WHERE status != 'SERVICE_BAY' ORDER BY calibrated_id ASC")
    public abstract LiveData<List<Bay>> getAllBays();

    @Query("SELECT * FROM bay_table WHERE status = 'SERVICE_BAY' LIMIT 1")
    public abstract LiveData<Bay> getServiceBayLiveData();

    @Query("SELECT COUNT(*) FROM bay_table WHERE status = 'AVAILABLE'")
    public abstract Integer getNumberOfAvailableBays();

    @Query("SELECT * FROM bay_table WHERE status = :bayStatus LIMIT :numOfBaysSelected")
    public abstract List<Bay> fetchRequiredBays(BayStatus bayStatus, Integer numOfBaysSelected);

    //delete all bays
    @Query("DELETE FROM bay_table")
    public abstract void deleteAllBays();

    //save list of bays
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(List<Bay> bays);

    @Query("SELECT COUNT(*) FROM bay_table")
    public abstract Integer getBayCount();

    @Query("SELECT * FROM bay_table WHERE calibrated_id = :calibratedId")
    public abstract Bay findBayByCalibratedId(Integer calibratedId);

    @Query("SELECT * FROM bay_table WHERE status = 'SERVICE_BAY' LIMIT 1")
    public abstract Bay getServiceBay();

    @Transaction
    public void deleteAndInsertAll(List<Bay> bays) {
        deleteAllBays();
        insertAll(bays);
    }
}
