package com.example.smartlockerandroid.data.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.smartlockerandroid.data.model.User;
import com.example.smartlockerandroid.data.repository.UserRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author itschathurangaj on 6/9/23
 */
public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }

    public void insert(User user) {
        userRepository.insert(user);
    }

    public void update(User user) {
        userRepository.update(user);
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public LiveData<List<User>> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public User findUserByUsername(String username) throws ExecutionException, InterruptedException {
        return userRepository.findUserByUsername(username);
    }

    public User findUserByBarcode(String barcode) throws ExecutionException, InterruptedException {
        return userRepository.findUserByPinCode(barcode);
    }

    public User findUserByQrcode(String qrcode)throws ExecutionException, InterruptedException {
        return userRepository.findUserByQrcode(qrcode);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}
