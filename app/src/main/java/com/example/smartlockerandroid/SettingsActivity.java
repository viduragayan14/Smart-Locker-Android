package com.example.smartlockerandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartlockerandroid.data.enums.BayDoorStatus;
import com.example.smartlockerandroid.data.enums.BayLightStatus;
import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.enums.CourierStatus;
import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.Courier;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.data.viewmodel.CourierViewModel;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.serialimpl.SerialHelperImpl;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

public class SettingsActivity extends AppCompatActivity implements UdpServerThread.MessageReceived {

    private Switch uberEats, grubhub, doordash, postmates, callInOrder, onlineOrder;
    private EditText etWelcomeText;
    private Button btnClose, btnSave, btnResetBays, btnOpenServiceBay;
    private CourierViewModel courierViewModel;
    private PreferenceViewModel preferenceViewModel;
    private BayViewModel bayViewModel;
    private SerialHelperImpl serialHelper;
    UdpServerThread bh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);
        bh = UdpServerThread.getInstance(SettingsActivity.this, this);
        bh.setViewModelStoreOwner(SettingsActivity.this,this);
        //get serial instance
        serialHelper = SerialHelperImpl.getInstance(this);

        init();
        handleUberEats();
        handleGrubhub();
        handleDoordash();
        handlePostmates();
        handleCallInOrder();
        handleOnlineOrder();
        closeActivity();
        handleSave();
        handleResetBays();
        handleServiceBay();
    }

    private void init() {
        uberEats = findViewById(R.id.switch_uberEats);
        grubhub = findViewById(R.id.switch_grubhub);
        doordash = findViewById(R.id.switch_doordash);
        postmates = findViewById(R.id.switch_postmates);
        callInOrder = findViewById(R.id.switch_callInOrder);
        onlineOrder = findViewById(R.id.switch_onlineOrder);
        etWelcomeText = findViewById(R.id.et_welcome_text_settings);
        btnClose = findViewById(R.id.btn_close_settings);
        btnSave = findViewById(R.id.btn_save_settings);
        btnResetBays = findViewById(R.id.btn_reset_bays_settings);
        btnOpenServiceBay = findViewById(R.id.btn_reset_service_bay_settings);

        bayViewModel = new ViewModelProvider(this).get(BayViewModel.class);
        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
        preferenceViewModel.getWelcomeText().observe(this, text -> {
            etWelcomeText.setText(text);
        });

        courierViewModel = new ViewModelProvider(this).get(CourierViewModel.class);
        courierViewModel.getActiveCouriers().observe(this, couriers -> {
            for (Courier courier : couriers) {
                if (courier.getCourierId() == 1L)
                    uberEats.setChecked(getCourierSwitchButtonStatus(courier.getStatus()));
                if (courier.getCourierId() == 2L)
                    grubhub.setChecked(getCourierSwitchButtonStatus(courier.getStatus()));
                if (courier.getCourierId() == 3L)
                    doordash.setChecked(getCourierSwitchButtonStatus(courier.getStatus()));
                if (courier.getCourierId() == 4L)
                    postmates.setChecked(getCourierSwitchButtonStatus(courier.getStatus()));
                if (courier.getCourierId() == 5L)
                    callInOrder.setChecked(getCourierSwitchButtonStatus(courier.getStatus()));
                if (courier.getCourierId() == 6L)
                    onlineOrder.setChecked(getCourierSwitchButtonStatus(courier.getStatus()));
            }
        });
    }

    private boolean getCourierSwitchButtonStatus(CourierStatus courierStatus) {
        return CourierStatus.ACTIVE == courierStatus;
    }

    private void closeActivity() {
        btnClose.setOnClickListener(view -> {

            startActivity(new Intent(this, SelectCourierActivity.class));
        });
    }

    private void handleSave() {
        preferenceViewModel.getPreferenceLiveData().observe(this, preference -> {
            btnSave.setOnClickListener(view -> {
                preference.setWelcomeText(etWelcomeText.getText().toString());
                preferenceViewModel.update(preference);

                Intent intent = new Intent(view.getContext(), SelectCourierActivity.class);
                startActivity(intent);
            });
        });
    }

    private void handleUberEats() {
        courierViewModel.getCourierById(1L).observe(this, courier -> {
            uberEats.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        courier.setStatus(CourierStatus.ACTIVE);
                    } else {
                        courier.setStatus(CourierStatus.INACTIVE);
                    }
                    courierViewModel.update(courier);
                }
            });
        });
    }

    private void handleGrubhub() {
        courierViewModel.getCourierById(2L).observe(this, courier -> {
            grubhub.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        courier.setStatus(CourierStatus.ACTIVE);
                    } else {
                        courier.setStatus(CourierStatus.INACTIVE);
                    }
                    courierViewModel.update(courier);
                }
            });
        });
    }

    private void handleDoordash() {
        courierViewModel.getCourierById(3L).observe(this, courier -> {
            doordash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        courier.setStatus(CourierStatus.ACTIVE);
                    } else {
                        courier.setStatus(CourierStatus.INACTIVE);
                    }
                    courierViewModel.update(courier);
                }
            });
        });
    }

    private void handlePostmates() {
        courierViewModel.getCourierById(4L).observe(this, courier -> {
            postmates.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        courier.setStatus(CourierStatus.ACTIVE);
                    } else {
                        courier.setStatus(CourierStatus.INACTIVE);
                    }
                    courierViewModel.update(courier);
                }
            });
        });
    }

    private void handleCallInOrder() {
        courierViewModel.getCourierById(5L).observe(this, courier -> {
            callInOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        courier.setStatus(CourierStatus.ACTIVE);
                    } else {
                        courier.setStatus(CourierStatus.INACTIVE);
                    }
                    courierViewModel.update(courier);
                }
            });
        });
    }

    private void handleOnlineOrder() {
        courierViewModel.getCourierById(6L).observe(this, courier -> {
            onlineOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        courier.setStatus(CourierStatus.ACTIVE);
                    } else {
                        courier.setStatus(CourierStatus.INACTIVE);
                    }
                    courierViewModel.update(courier);
                }
            });
        });
    }

    public void handleResetBays() {
        bayViewModel.getAllBays().observe(this, bays -> {
            btnResetBays.setOnClickListener(view -> {
                for (Bay bay : bays) {
                    if (bay.getCalibratedId() != 4) {
                        bay.setLed(BayLightStatus.GREEN);
                        bay.setStatus(BayStatus.AVAILABLE);
                        bay.setDoorStatus(BayDoorStatus.OPEN);
                        bayViewModel.update(bay);
                        openBay(bay);
                    }
                }
            });
        });
    }

    public void handleServiceBay() {
        bayViewModel.getServiceBayLiveData().observe(this, bay -> {
            btnOpenServiceBay.setOnClickListener(view -> {
                bay.setDoorStatus(BayDoorStatus.OPEN);
                bay.setLed(BayLightStatus.GREEN);
                openBay(bay);
            });
        });
    }

    private void openBay(Bay bay) {
        String hexToSend = serialHelper.serialHexMaker(
                bay.getCalibratedId(),
                bay.getDoorStatus().getValue(),
                0,
                bay.getLed().getValue()
        );

        try {
            System.out.println("open bay with hex : " + hexToSend);
            serialHelper.getSerialHelper().sendHex(hexToSend);
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessageReceived2(String message) {

    }
}