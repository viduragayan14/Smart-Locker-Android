package com.example.smartlockerandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

public class LatePickUpErrorActivity extends AppCompatActivity implements UdpServerThread.MessageReceived {

    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    TextView tv;
    int i = 0;
    private RelativeLayout btnTryAgain;
    private Preference preference;
    UdpServerThread bh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_late_pick_up_error);
        bh = UdpServerThread.getInstance(LatePickUpErrorActivity.this, this);
        bh.setViewModelStoreOwner(LatePickUpErrorActivity.this,this);
        PreferenceViewModel preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
        preference = preferenceViewModel.getPreference();
        init();
        handleTryAgain();
    }

    private void init() {

        btnTryAgain = findViewById(R.id.btn_try_again);
        mProgressBar = findViewById(R.id.progressbar);
        tv = findViewById(R.id.seconds);
        mProgressBar.setProgress(i);
        TextView warning = findViewById(R.id.tv_step1_thank_you);
        warning.setText(preference.getLatePickupWarning());
        mCountDownTimer = new CountDownTimer(5000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                i++;
                tv.setText((millisUntilFinished / 1000 + 1) + " secs left");
                mProgressBar.setProgress((int) i * 100 / (5000 / 100));

            }

            @Override
            public void onFinish() {
                //Do what you want
                i++;
                mProgressBar.setProgress(100);
                Intent intent = new Intent(LatePickUpErrorActivity.this, SelectCourierActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        };
        mCountDownTimer.start();
    }

    private void handleTryAgain() {
        btnTryAgain.setOnClickListener(view -> {
            mCountDownTimer.cancel();
            Intent intent = new Intent(LatePickUpErrorActivity.this, SelectCourierActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    @Override
    public void onMessageReceived2(String message) {

    }
}