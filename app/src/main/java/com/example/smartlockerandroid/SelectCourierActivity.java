package com.example.smartlockerandroid;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smartlockerandroid.adapters.PagerAdapter;
import com.example.smartlockerandroid.data.enums.Flow;
import com.example.smartlockerandroid.data.enums.UserRole;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.model.User;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.data.viewmodel.CourierViewModel;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.data.viewmodel.UserViewModel;
import com.example.smartlockerandroid.utils.UdpServerThread;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

public class SelectCourierActivity extends AppCompatActivity implements UdpServerThread.MessageReceived {
    //    RecyclerView recyclerView;
//    GridLayoutManager layoutManager;
    ImageView homeLogo;
    String currentUser;
    private ImageView btnSettings, btnOrderManagement, logOut;

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

    private Handler handler;
    private boolean isRunning = false;
    UdpServerThread bh;
    View bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        Bundle extras = getIntent().getExtras();
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        bh = UdpServerThread.getInstance(SelectCourierActivity.this, this);
        bh.setViewModelStoreOwner(SelectCourierActivity.this, this);
        if (extras != null) {
            Serializable serializableObject = extras.getSerializable("flow");
            if (serializableObject != null) {
                setContentView(R.layout.activity_select_courier_loading);
                flow = (Flow) extras.getSerializable("flow");
                currentUser = extras.getString("currentUser");
                currentUserRole = (UserRole) extras.getSerializable("currentUserRole");
            } else {
                setContentView(R.layout.activity_select_courier);
                flow = Flow.PICKUP_FLOW;

            }
        } else {
            setContentView(R.layout.activity_select_courier);
            flow = Flow.PICKUP_FLOW;

        }

        init();
        handleSettings();
        handleOrderManagement();
        handler = new Handler();
        startThread();
    }


    private void init() {

        BayViewModel bayViewModel = new ViewModelProvider(this).get(BayViewModel.class);
        Integer noOfBaysAvailable = bayViewModel.getNumberOfAvailableBays();
        bar = findViewById(R.id.heart_beat_status);
        if (flow.equals(Flow.PICKUP_FLOW)) {
            homeLogo = findViewById(R.id.imgVw_home_logo);
            title = findViewById(R.id.tv_courier_title_main);
            message = findViewById(R.id.tv_courier_title);
            homeLogo.setOnClickListener(view -> {
                Intent i = new Intent(this, LoadingAreaActivity.class);
                startActivity(i);
            });
            preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
            preferenceViewModel.getPreferenceLiveData().observe(SelectCourierActivity.this, preference -> {
                this.p = preference;
                if (p.getWelcomeText() != null) {
                    title.setText(p.getWelcomeText());
                }
                if (p.getWelcomeMessage() != null) {
                    message.setText(p.getWelcomeMessage());
                }
                if (p.getHomeLogoData() != null) {
                    homeLogo.setImageBitmap(BitmapFactory.decodeByteArray(preference.getHomeLogoData(), 0, preference.getHomeLogoData().length));
                }
            });

        } else {

            btnSettings = findViewById(R.id.btn_settings_select_courier);

            btnOrderManagement = findViewById(R.id.btn_order_management_select_courier);
            logOut = findViewById(R.id.log_out);
            logOut.setOnClickListener(view -> {
                super.onBackPressed();
                finish();
            });
        }

        ViewPager2 vp = findViewById(R.id.introViewPager);
        TabLayout tb = findViewById(R.id.into_tab_layout);
        PagerAdapter adapter = new PagerAdapter(SelectCourierActivity.this);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (flow.equals(Flow.LOADING_FLOW)) {
//            timer();
//            timer.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (flow.equals(Flow.LOADING_FLOW)) {
//            timer.cancel();
//        }
    }

    void timer() {
//        timer = new CountDownTimer(20000, 1000) {
//            public void onTick(long millisUntilFinished) {
//            }
//
//            public void onFinish() {
//                Intent intent = new Intent(SelectCourierActivity.this, SelectCourierActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();
//            }
//        };
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        Log.e("Barcode", String.valueOf(e.getAction()));
        if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() != KeyEvent.KEYCODE_ENTER) {
            Log.i("TAG", "dispatchKeyEvent: " + e);
            char pressedKey = (char) e.getUnicodeChar();
            qrcode += pressedKey;
        }
        //if (e.getAction() == KeyEvent.ACTION_DOWN && barcode.length()==4) {
        if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

            qrcode = qrcode.replaceAll("\\s", "");
            User user = getUserQrcode(qrcode);
            if (user != null) {
                Intent intent = new Intent(SelectCourierActivity.this, SelectCourierActivity.class);
                intent.putExtra("flow", Flow.LOADING_FLOW);
                intent.putExtra("currentUser", user.getUsername());
                intent.putExtra("currentUserRole", user.getUserRole());
                startActivity(intent);

            } else {
                Toast.makeText(this, "Incorrect credentials, Try again!" + qrcode, Toast.LENGTH_SHORT).show();
            }

            qrcode = "";
            return true;
        } else {
            return false;
        }


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



    private User getUserQrcode(String qrcode) {
        try {
            return userViewModel.findUserByQrcode(qrcode);
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(this, "Something went wrong, try again!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onMessageReceived2(String message) {

    }
}