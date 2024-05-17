package com.example.smartlockerandroid.utils;

import android.os.Handler;
import android.os.Looper;

public class SelectCourierThread extends Thread {

    private Handler handler;
    private boolean isRunning = false;

    @Override
    public void run() {
        Looper.prepare();
        handler = new Handler();
        isRunning = true;

        // Define the task to be executed every 5 seconds
        Runnable task = new Runnable() {
            @Override
            public void run() {
                // Do something here
                // This code will run every 5 seconds
                System.out.println("Task is running...");

                if (isRunning) {
                    // Schedule the task to run again after 5 seconds
                    handler.postDelayed(this, 5000); // 5000 milliseconds = 5 seconds
                }
            }
        };

        // Schedule the task to run for the first time
        handler.post(task);

        Looper.loop();
    }

    // Method to stop the thread
    public void stopThread() {
        isRunning = false;
        handler.getLooper().quit();
    }
}
