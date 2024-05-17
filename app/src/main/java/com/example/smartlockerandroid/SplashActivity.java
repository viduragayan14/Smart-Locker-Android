package com.example.smartlockerandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartlockerandroid.data.SmartLockerDatabase;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class SplashActivity extends AppCompatActivity implements UdpServerThread.MessageReceived {
    private static final long SPLASH_DURATION = 1000;
    private PreferenceViewModel preferenceViewModel;
    Preference preference;
    UdpServerThread ut;
    private UdpServerThread serverThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SmartLockerDatabase database = SmartLockerDatabase.getDatabase(SplashActivity.this);



        ut = UdpServerThread.getInstance(SplashActivity.this, this);
        ut.setViewModelStoreOwner(SplashActivity.this,this);


        startThread();
        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
        preference = preferenceViewModel.getPreference();
        preferenceViewModel.getPreferenceLiveData().observe(SplashActivity.this, preference -> {
            this.preference = preference;

            new Handler().postDelayed(() -> {

                if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                    Intent mainIntent = new Intent(SplashActivity.this, LoadingAreaActivity.class);
                    startActivity(mainIntent);

                } else {
                    Intent mainIntent = new Intent(SplashActivity.this, SelectCourierActivity.class);
                    startActivity(mainIntent);
                }

                finish();
            }, SPLASH_DURATION);
        });

        new Thread(() -> {
            if (preference.getClientIp() != null) {
                try {
                    ut.heartBeat(InetAddress.getByName(preference.getClientIp()));
                } catch (Exception e) {
                    Log.e("Exception", "" + e);
                }
            }
        }).start();

    }

//    @Override
//    public void onMessageReceived(String message) {
//
//    }

    @Override
    public void onMessageReceived2(String message) {

    }

    private void startThread(){
        Thread secondaryThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                ut.heartBeatBool = false;
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        secondaryThread.start();
    }


}