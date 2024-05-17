package com.example.smartlockerandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

public class PickUpFlowThankYouActivity extends AppCompatActivity implements UdpServerThread.MessageReceived {

    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    TextView tv;
    int i = 0;
    private ImageView imageView;
    UdpServerThread bh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pick_up_flow_thank_you);
        bh = UdpServerThread.getInstance(PickUpFlowThankYouActivity.this, this);
        bh.setViewModelStoreOwner(PickUpFlowThankYouActivity.this,this);
        imageView = findViewById(R.id.imgVw_success_thank_you_pickup_flow);
        ImageView close = findViewById(R.id.close);
        close.setOnClickListener(view -> {
            mCountDownTimer.cancel();
            Intent intent = new Intent(PickUpFlowThankYouActivity.this, SelectCourierActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        mProgressBar = findViewById(R.id.progressbar);
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
                Intent intent = new Intent(PickUpFlowThankYouActivity.this, SelectCourierActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        };
        mCountDownTimer.start();


        imageView.setOnClickListener(view -> {
            Intent intent = new Intent(this, SelectCourierActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onMessageReceived2(String message) {

    }
}