package com.example.smartlockerandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

public class SettingsPasswordActivity extends AppCompatActivity implements UdpServerThread.MessageReceived {

    private Button btnClose;
    private EditText etPassword;
    UdpServerThread bh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings_password);
        bh = UdpServerThread.getInstance(SettingsPasswordActivity.this, this);
        bh.setViewModelStoreOwner(SettingsPasswordActivity.this,this);
        init();
        closeActivity();
        handlePasswordInput();
    }

    private void init() {
        btnClose = findViewById(R.id.btn_settings_password_activity);
        etPassword = findViewById(R.id.et_settings_password_activity);
    }

    private void closeActivity() {
        btnClose.setOnClickListener(view -> {
            //this.finish();
            super.onBackPressed();
        });
    }

    private void handlePasswordInput() {
        etPassword.setOnKeyListener((view, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                String enteredText = etPassword.getText().toString();
                if (enteredText.equals("1111")) {
                    Toast.makeText(this, "Password Correct!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Password Incorrect! Test password is 1111", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void onMessageReceived2(String message) {

    }
}