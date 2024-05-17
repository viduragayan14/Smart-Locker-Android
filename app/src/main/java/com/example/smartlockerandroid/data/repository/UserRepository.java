package com.example.smartlockerandroid.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.smartlockerandroid.data.SmartLockerDatabase;
import com.example.smartlockerandroid.data.dao.UserDao;
import com.example.smartlockerandroid.data.model.User;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author itschathurangaj on 6/9/23
 */
public class UserRepository {
    private UserDao userDao;

    public UserRepository(Application application) {
        SmartLockerDatabase database = SmartLockerDatabase.getDatabase(application);
        userDao = database.userDao();
    }

    public void insert(User user) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            userDao.insert(user);
        });
    }

    public void update(User user) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            userDao.update(user);
        });
    }

    public void delete(User user) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            userDao.delete(user);
        });
    }

    public LiveData<List<User>> getAllUsers() {
        return userDao.getAllUsers();
    }

    public User findUserByUsername(String username) throws ExecutionException, InterruptedException {
        Future<User> userFuture = SmartLockerDatabase.databaseWriteExecutor.submit(new Callable<User>() {
            @Override
            public User call() {
                return userDao.findUserByUsername(username);
            }
        });
        return userFuture.get();
    }

    public User findUserByPinCode(String pinCode) throws ExecutionException, InterruptedException {
        Future<User> userFuture = SmartLockerDatabase.databaseWriteExecutor.submit(() -> userDao.findUserByPinCode(pinCode));
        return userFuture.get();
    }

    public void deleteUser(Long userId) {
        SmartLockerDatabase.databaseWriteExecutor.execute(() -> {
            userDao.deleteUser(userId);
        });
    }

    public User findUserByQrcode(String qrcode) throws ExecutionException, InterruptedException {
        Future<User> userFuture = SmartLockerDatabase.databaseWriteExecutor.submit(() -> userDao.findUserByQrcode(qrcode));
        return userFuture.get();
    }
}
