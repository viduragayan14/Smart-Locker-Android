package com.example.smartlockerandroid;

import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartlockerandroid.data.enums.Flow;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.model.User;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.data.viewmodel.UserViewModel;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

import java.util.concurrent.ExecutionException;

public class LoadingAreaActivity extends AppCompatActivity implements KeyboardUtil.CustomKeys, UdpServerThread.MessageReceived {

    //    private Button btnClose;
    private EditText etPassword;
    private ImageView close;
    private UserViewModel userViewModel;

    private KeyboardView customKeyboard;
    private KeyboardUtil keyboardUtil;
    private RelativeLayout superParent;
    private LinearLayout keyboardParent;

    private CountDownTimer timer;
    UdpServerThread bh;

    private Preference preference;
    private PreferenceViewModel preferenceViewModel;
    View bar;
    private boolean isRunning = false;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        setContentView(R.layout.activity_loading_area_r);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        bh = UdpServerThread.getInstance(LoadingAreaActivity.this, this);
        bh.setViewModelStoreOwner(LoadingAreaActivity.this,this);
        init();
        handlePasswordInput();
        handler = new Handler();
        startThread();
    }



    private void init() {
//        btnClose = findViewById(R.id.btn_close_loading_area);
        etPassword = findViewById(R.id.et_password_loading_are);
        close = findViewById(R.id.close);

        bar = findViewById(R.id.heart_beat_status);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
        preference = preferenceViewModel.getPreference();

        customKeyboard = findViewById(R.id.kv_keyboard);
        keyboardParent = findViewById(R.id.ll_keyboard_father);
        superParent = findViewById(R.id.view_root);
        keyboardUtil = new KeyboardUtil(this, etPassword, customKeyboard, keyboardParent, false, this);
//        keyboardUtil.shiftCaps(false);
        keyboardUtil.forbidSoftInputMethod();
        keyboardUtil.showKeyboard();


        etPassword.setOnTouchListener((view, motionEvent) -> {

            keyboardUtil.showKeyboard();

            return false;
        });



        close.setOnClickListener(view -> {
            if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                Intent intent = new Intent(LoadingAreaActivity.this, LoadingAreaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            } else {
                Intent intent = new Intent(LoadingAreaActivity.this, SelectCourierActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        superParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                timer.start();
                keyboardUtil.hideKeyboard();
            }
        });

    }

    private void handlePasswordInput() {
        etPassword.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                String enteredText = etPassword.getText().toString();
                //get user
                User user = getUserByBarcode(enteredText);
                if (user != null) {
                    Intent intent = new Intent(this, SelectCourierActivity.class);
                    intent.putExtra("flow", Flow.LOADING_FLOW);
                    intent.putExtra("currentUser", user.getUsername());
                    intent.putExtra("currentUserRole", user.getUserRole());
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Incorrect credentials, Try again!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        //show keyboard automatically
//        etPassword.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(etPassword, InputMethodManager.SHOW_IMPLICIT);
    }

    private User getUserByBarcode(String barcode) {
        try {
            return userViewModel.findUserByBarcode(barcode);
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(this, "Something went wrong, try again!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onConfirmPressed() {
        String enteredText = etPassword.getText().toString();
        //get user
        User user = getUserByBarcode(enteredText);
        if (user != null) {
            Intent intent = new Intent(this, SelectCourierActivity.class);
            intent.putExtra("flow", Flow.LOADING_FLOW);
            intent.putExtra("currentUser", user.getUsername());
            intent.putExtra("currentUserRole", user.getUserRole());
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Incorrect credentials, Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancelPressed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        timer();
        timer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        timer.cancel();
    }

    void timer() {
        timer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                if(preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.FRONT_CONFIG)) {
                    Intent intent = new Intent(LoadingAreaActivity.this, SelectCourierActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    private void startThread() {
        isRunning = true;
        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (bh.heartBeatBool){
                    bar.setBackgroundColor(getColor(R.color.green));
                    bar.setVisibility(View.VISIBLE);
                }else{
                    bar.setBackgroundColor(getColor(R.color.red));
                    bar.setVisibility(View.INVISIBLE);
                }
                if (isRunning) {
                    handler.postDelayed(this, 5000);
                }
            }
        };
        handler.post(task);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopThread();
    }

    private void stopThread() {
        isRunning = false;
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onMessageReceived2(String message) {

    }
}