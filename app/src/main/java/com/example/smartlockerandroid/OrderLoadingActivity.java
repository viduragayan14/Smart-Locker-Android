package com.example.smartlockerandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartlockerandroid.adapters.PagerAdapter;
import com.example.smartlockerandroid.data.enums.Flow;
import com.example.smartlockerandroid.data.enums.UserRole;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.data.viewmodel.CourierViewModel;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.data.viewmodel.UserViewModel;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class OrderLoadingActivity extends AppCompatActivity implements UdpServerThread.MessageReceived {
    ImageView homeLogo;
    String currentUser;
    private ImageView btnSettings, btnOrderManagement, logOut;

    View v;
    private Flow flow;
    private UserRole currentUserRole;
    //    private CountDownTimer timer;
    private TextView title, message;
    String barcode = "";
    private PreferenceViewModel preferenceViewModel;
    Preference p;
    String qrcode = "";

    int called = 0;

    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_loading);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        Bundle extras = getIntent().getExtras();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        UdpServerThread bh = UdpServerThread.getInstance(OrderLoadingActivity.this, this);
        bh.setViewModelStoreOwner(OrderLoadingActivity.this,this);

        flow = (Flow) extras.getSerializable("flow");
        currentUser = extras.getString("currentUser");
        currentUserRole = (UserRole) extras.getSerializable("currentUserRole");

        init();
        handleSettings();
        handleOrderManagement();
    }

    @Override
    public void onMessageReceived2(String message) {

    }

    private void init() {

        BayViewModel bayViewModel = new ViewModelProvider(this).get(BayViewModel.class);
        Integer noOfBaysAvailable = bayViewModel.getNumberOfAvailableBays();


        btnSettings = findViewById(R.id.btn_settings_select_courier);

        btnOrderManagement = findViewById(R.id.btn_order_management_select_courier);
        logOut = findViewById(R.id.log_out);
        logOut.setOnClickListener(view -> {
            super.onBackPressed();
            finish();
        });

        ViewPager2 vp = findViewById(R.id.introViewPager);
        TabLayout tb = findViewById(R.id.into_tab_layout);
        PagerAdapter adapter = new PagerAdapter(OrderLoadingActivity.this);
        CourierViewModel courierViewModel = new ViewModelProvider(this).get(CourierViewModel.class);
        adapter.setFlow(flow);
        adapter.setUserName(currentUser);
        adapter.setNoOfBaysAvailable(noOfBaysAvailable);
        courierViewModel.getActiveCouriers().observe(this, couriers -> {
            adapter.setFullList(couriers);
            vp.setAdapter(adapter);
            new TabLayoutMediator(tb, vp, (tab, position) -> {
            }).attach();

        });


    }


    private void handleSettings() {
        if (flow.equals(Flow.LOADING_FLOW)) {
            if (!currentUserRole.equals(UserRole.STAFF)) {
                btnSettings.setVisibility(View.VISIBLE);
                btnSettings.setOnClickListener(view -> {
                    Intent intent = new Intent(this, PreferencesActivity.class);
                    startActivity(intent);
                });
            } else {
                btnSettings.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void handleOrderManagement() {
        if (flow.equals(Flow.LOADING_FLOW)) {
            if (!currentUserRole.equals(UserRole.STAFF)) {
                btnOrderManagement.setVisibility(View.VISIBLE);
                btnOrderManagement.setOnClickListener(view -> {
                    Intent intent = new Intent(this, OrderManagementActivity.class);
                    startActivity(intent);
                });
            } else {
                btnOrderManagement.setVisibility(View.VISIBLE);
                btnOrderManagement.setOnClickListener(view -> {
                    Intent intent = new Intent(this, OrderManagementActivity.class);
                    startActivity(intent);
                });
            }
        }
    }
}