package com.example.smartlockerandroid.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.smartlockerandroid.data.model.User;

import java.util.List;

/**
 * @author itschathurangaj on 6/9/23
 */
@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM user_table WHERE user_role !='SUPER_ADMIN'")
    LiveData<List<User>> getAllUsers();

    @Query("SELECT * FROM user_table WHERE username= :username LIMIT 1")
    User findUserByUsername(String username);

    @Query("SELECT * FROM user_table WHERE password= :pinCode AND status = 1 LIMIT 1")
    User findUserByPinCode(String pinCode);
    @Query("SELECT * FROM user_table WHERE barcode= :qrcode AND status = 1 LIMIT 1")
    User findUserByQrcode(String qrcode);

    @Query("DELETE from user_table WHERE user_id= :userId")
    void deleteUser(Long userId);
}
