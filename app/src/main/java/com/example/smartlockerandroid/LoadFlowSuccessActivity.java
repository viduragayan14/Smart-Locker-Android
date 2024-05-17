package com.example.smartlockerandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

public class LoadFlowSuccessActivity extends AppCompatActivity implements UdpServerThread.MessageReceived {

    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    TextView tv;
    int i = 0;
    private RelativeLayout btnTryAgain;
    UdpServerThread bh;
    private PreferenceViewModel preferenceViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loading_flow_success);
        bh = UdpServerThread.getInstance(LoadFlowSuccessActivity.this, this);
        bh.setViewModelStoreOwner(LoadFlowSuccessActivity.this,this);

        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
        Preference preference = preferenceViewModel.getPreference();

        mProgressBar = findViewById(R.id.progressbar);
        ImageView close = findViewById(R.id.close);
        close.setOnClickListener(view -> {
            mCountDownTimer.cancel();
            Intent mainIntent;
            if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                mainIntent = new Intent(LoadFlowSuccessActivity.this, LoadingAreaActivity.class);

            } else {
                mainIntent = new Intent(LoadFlowSuccessActivity.this, SelectCourierActivity.class);
            }
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
        });
        tv = findViewById(R.id.seconds);
        mProgressBar.setProgress(i);
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

                Intent mainIntent;
                if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                    mainIntent = new Intent(LoadFlowSuccessActivity.this, LoadingAreaActivity.class);

                } else {
                    mainIntent = new Intent(LoadFlowSuccessActivity.this, SelectCourierActivity.class);
                }
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                finish();
            }
        };
        mCountDownTimer.start();

    }

    @Override
    public void onMessageReceived2(String message) {

    }
}