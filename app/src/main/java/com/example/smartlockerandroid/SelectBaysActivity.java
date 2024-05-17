package com.example.smartlockerandroid;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartlockerandroid.data.enums.BayDoorStatus;
import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.enums.OrderStatus;
import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.Courier;
import com.example.smartlockerandroid.data.model.Order;
import com.example.smartlockerandroid.data.model.OrderBay;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.data.viewmodel.CourierViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderBayViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderViewModel;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.serialimpl.SerialHelperImpl;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.List;
import java.util.Objects;

public class SelectBaysActivity extends AppCompatActivity implements UdpServerThread.MessageReceived {
    private RelativeLayout btnContinue;
    private RelativeLayout btnCancel;
    private RelativeLayout rl1;
    private TextView currentValue;

    private TextView timerValue;
    private Long newOrderId;
    private Integer numOfBaysSelected = 0;
    private OrderViewModel orderViewModel;
    private Order currentOrder;
    private Integer noOfBaysAvailable;
    private String fromActivity;
    private List<Bay> availableBays;
    private List<Bay> availableBays2;
    private BayViewModel bayViewModel;
    private Preference preference;
    private PreferenceViewModel preferenceViewModel;
    private OrderBayViewModel orderBayViewModel;
    private CourierViewModel courierViewModel;
    private SerialHelperImpl serialHelper;
    CountDownTimer mCountDownTimer;
    int i = 0;

    private CountDownTimer timer;
    UdpServerThread bh;
    private UdpHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        helper = new UdpHelper();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_select_bays_r);

        newOrderId = getIntent().getLongExtra("newOrderToBeLoaded", 0);
        bayViewModel = new ViewModelProvider(this).get(BayViewModel.class);

        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
        preference = preferenceViewModel.getPreference();

        orderBayViewModel = new ViewModelProvider(this).get(OrderBayViewModel.class);
        courierViewModel = new ViewModelProvider(this).get(CourierViewModel.class);
        serialHelper = SerialHelperImpl.getInstance(this);

        init();
    }

    private void init() {

        bh = UdpServerThread.getInstance(SelectBaysActivity.this, this);
        bh.setViewModelStoreOwner(SelectBaysActivity.this, this);
        Dialog dialog = new Dialog(SelectBaysActivity.this);
        dialog.setContentView(R.layout.dialog_bays_full);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        ProgressBar mProgressBar = dialog.findViewById(R.id.progressbar);
        TextView tv = dialog.findViewById(R.id.seconds);
        mProgressBar.setProgress(i);


        TextView tv3 = findViewById(R.id.tv3);
        currentValue = findViewById(R.id.current_value);
        timerValue = findViewById(R.id.imgVw_timer);
        btnContinue = findViewById(R.id.btn_continue_select_bay);
        btnCancel = findViewById(R.id.btn_cancel_select_bay);
        rl1 = findViewById(R.id.rl1);


        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        BayViewModel bayViewModel = new ViewModelProvider(this).get(BayViewModel.class);

        currentOrder = orderViewModel.getOrderById(newOrderId);
        noOfBaysAvailable = bayViewModel.getNumberOfAvailableBays();

//        if (bh.heartBeatBool) {
//            if (noOfBaysAvailable > numOfBaysSelected) {
//                numOfBaysSelected = numOfBaysSelected + 1;
//                currentOrder.setNoOfBays(numOfBaysSelected);
//                currentValue.setText(numOfBaysSelected.toString());
//
//                availableBays2 = bayViewModel.getRequiredBays(BayStatus.AVAILABLE, numOfBaysSelected);
//                Bay lastBay = availableBays2.get(availableBays2.size() - 1);
//
//                if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
//
//                    try {
//                        InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());
//
//                        JSONObject object = new JSONObject();
//
//                        object.put("type", "loading");
//                        object.put("bayId", lastBay.getCalibratedId().toString());
//
//                        helper.sendMessage(object, ipAddress);
//                    } catch (Exception e) {
//                        Log.e("Exception", "" + e.getMessage());
//                    }
////                bh.send("L"+lastBay.getCalibratedId().toString()+"*@");
//
//                } else {
//
//                }
//                Bay b = availableBays2.get(availableBays2.size()-1);
//                b.setDoorStatus(BayDoorStatus.OPEN);
//                b.setLed(preference.getBayColorWhenFull());
//                b.setStatus(BayStatus.AVAILABLE);
//                bayViewModel.update(b);
//                openBay(b);
//                for (Bay bay : availableBays2) {
//
//
//                    //prepare to open the bay
////                    bay.setDoorStatus(BayDoorStatus.OPEN);
////                    bay.setLed(preference.getBayColorWhenFull());
////                    bay.setStatus(BayStatus.AVAILABLE);
////                    bayViewModel.update(bay);
//
//                    //open bay
////                    openBay(bay);
//                }
//
//            } else {
//                mProgressBar.setProgress(0);
//                i = 0;
//                mCountDownTimer = new CountDownTimer(5000, 100) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        i++;
//                        tv.setText((millisUntilFinished / 1000 + 1) + " secs left");
//                        mProgressBar.setProgress((int) i * 100 / (5000 / 100));
//
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        //Do what you want
//                        i++;
//                        mProgressBar.setProgress(100);
//                        dialog.dismiss();
//                        mProgressBar.setProgress(0);
//
//                    }
//                };
//                mCountDownTimer.start();
//                dialog.show();
//            }
//        } else {
//            Toast.makeText(SelectBaysActivity.this, "Device not Connected", Toast.LENGTH_SHORT).show();
//        }
        if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
            if (bh.heartBeatBool) {
                if (noOfBaysAvailable > numOfBaysSelected) {
                    numOfBaysSelected = numOfBaysSelected + 1;
                    currentOrder.setNoOfBays(numOfBaysSelected);
                    currentValue.setText(numOfBaysSelected.toString());

                    availableBays2 = bayViewModel.getRequiredBays(BayStatus.AVAILABLE, numOfBaysSelected);
                    Bay lastBay = availableBays2.get(availableBays2.size() - 1);
                    if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                        try {
                            InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());
                            JSONObject object = new JSONObject();

                            object.put("type", "loading");
                            object.put("bayId", lastBay.getCalibratedId().toString());

                            helper.sendMessage(object, ipAddress);
                        } catch (Exception e) {
                            Log.e("Exception", "" + e.getMessage());
                        }
//                    bh.send("L"+lastBay.getCalibratedId().toString()+"*@");

                    } else {

                    }

                    Bay b = availableBays2.get(availableBays2.size() - 1);
                    b.setDoorStatus(BayDoorStatus.OPEN);
                    b.setLed(preference.getBayColorWhenFull());
                    b.setStatus(BayStatus.AVAILABLE);
                    bayViewModel.update(b);
                    openBay(b);

                    for (Bay bay : availableBays2) {

//                        bay.setDoorStatus(BayDoorStatus.OPEN);
//                        bay.setLed(preference.getBayColorWhenFull());
//                        bay.setStatus(BayStatus.AVAILABLE);
//                        bayViewModel.update(bay);

//                        openBay(bay);
                        //open bay

                    }
                } else {
                    mProgressBar.setProgress(0);
                    i = 0;
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
                            dialog.dismiss();
                            mProgressBar.setProgress(0);
                        }
                    };
                    mCountDownTimer.start();
                    dialog.show();
                }
            } else {
                Toast.makeText(SelectBaysActivity.this, "Device not Connected", Toast.LENGTH_SHORT).show();
            }
        } else {

            if (noOfBaysAvailable > numOfBaysSelected) {
                numOfBaysSelected = numOfBaysSelected + 1;
                currentOrder.setNoOfBays(numOfBaysSelected);
                currentValue.setText(numOfBaysSelected.toString());

                availableBays2 = bayViewModel.getRequiredBays(BayStatus.AVAILABLE, numOfBaysSelected);
                Bay lastBay = availableBays2.get(availableBays2.size() - 1);
                if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                    try {
                        InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());
                        JSONObject object = new JSONObject();

                        object.put("type", "loading");
                        object.put("bayId", lastBay.getCalibratedId().toString());

                        helper.sendMessage(object, ipAddress);
                    } catch (Exception e) {
                        Log.e("Exception", "" + e.getMessage());
                    }
//                    bh.send("L"+lastBay.getCalibratedId().toString()+"*@");

                } else {

                }

                Bay b = availableBays2.get(availableBays2.size() - 1);
                b.setDoorStatus(BayDoorStatus.OPEN);
                b.setLed(preference.getBayColorWhenFull());
                b.setStatus(BayStatus.AVAILABLE);
                bayViewModel.update(b);
                openBay(b);

                for (Bay bay : availableBays2) {

//                        bay.setDoorStatus(BayDoorStatus.OPEN);
//                        bay.setLed(preference.getBayColorWhenFull());
//                        bay.setStatus(BayStatus.AVAILABLE);
//                        bayViewModel.update(bay);

//                        openBay(bay);
                    //open bay

                }
            } else {
                mProgressBar.setProgress(0);
                i = 0;
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
                        dialog.dismiss();
                        mProgressBar.setProgress(0);
                    }
                };
                mCountDownTimer.start();
                dialog.show();
            }
        }

        tv3.setOnClickListener(view -> {

            if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                if (bh.heartBeatBool) {
                    timer.cancel();
                    timer.start();
                    if (noOfBaysAvailable > numOfBaysSelected) {
                        numOfBaysSelected = numOfBaysSelected + 1;
                        currentOrder.setNoOfBays(numOfBaysSelected);
                        currentValue.setText(numOfBaysSelected.toString());

                        availableBays2 = bayViewModel.getRequiredBays(BayStatus.AVAILABLE, numOfBaysSelected);
                        Bay lastBay = availableBays2.get(availableBays2.size() - 1);
                        if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                            try {
                                InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());
                                JSONObject object = new JSONObject();

                                object.put("type", "loading");
                                object.put("bayId", lastBay.getCalibratedId().toString());

                                helper.sendMessage(object, ipAddress);
                            } catch (Exception e) {
                                Log.e("Exception", "" + e.getMessage());
                            }
//                    bh.send("L"+lastBay.getCalibratedId().toString()+"*@");

                        } else {

                        }

                        Bay b = availableBays2.get(availableBays2.size() - 1);
                        b.setDoorStatus(BayDoorStatus.OPEN);
                        b.setLed(preference.getBayColorWhenFull());
                        b.setStatus(BayStatus.AVAILABLE);
                        bayViewModel.update(b);
                        openBay(b);

                        for (Bay bay : availableBays2) {

//                        bay.setDoorStatus(BayDoorStatus.OPEN);
//                        bay.setLed(preference.getBayColorWhenFull());
//                        bay.setStatus(BayStatus.AVAILABLE);
//                        bayViewModel.update(bay);

//                        openBay(bay);
                            //open bay

                        }
                    } else {
                        mProgressBar.setProgress(0);
                        i = 0;
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
                                dialog.dismiss();
                                mProgressBar.setProgress(0);
                            }
                        };
                        mCountDownTimer.start();
                        dialog.show();
                    }
                } else {
                    Toast.makeText(SelectBaysActivity.this, "Device not Connected", Toast.LENGTH_SHORT).show();
                }
            } else {
                timer.cancel();
                timer.start();
                if (noOfBaysAvailable > numOfBaysSelected) {
                    numOfBaysSelected = numOfBaysSelected + 1;
                    currentOrder.setNoOfBays(numOfBaysSelected);
                    currentValue.setText(numOfBaysSelected.toString());

                    availableBays2 = bayViewModel.getRequiredBays(BayStatus.AVAILABLE, numOfBaysSelected);
                    Bay lastBay = availableBays2.get(availableBays2.size() - 1);
                    if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                        try {
                            InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());
                            JSONObject object = new JSONObject();

                            object.put("type", "loading");
                            object.put("bayId", lastBay.getCalibratedId().toString());

                            helper.sendMessage(object, ipAddress);
                        } catch (Exception e) {
                            Log.e("Exception", "" + e.getMessage());
                        }
//                    bh.send("L"+lastBay.getCalibratedId().toString()+"*@");

                    } else {

                    }

                    Bay b = availableBays2.get(availableBays2.size() - 1);
                    b.setDoorStatus(BayDoorStatus.OPEN);
                    b.setLed(preference.getBayColorWhenFull());
                    b.setStatus(BayStatus.AVAILABLE);
                    bayViewModel.update(b);
                    openBay(b);

                    for (Bay bay : availableBays2) {

//                        bay.setDoorStatus(BayDoorStatus.OPEN);
//                        bay.setLed(preference.getBayColorWhenFull());
//                        bay.setStatus(BayStatus.AVAILABLE);
//                        bayViewModel.update(bay);

//                        openBay(bay);
                        //open bay

                    }
                } else {
                    mProgressBar.setProgress(0);
                    i = 0;
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
                            dialog.dismiss();
                            mProgressBar.setProgress(0);
                        }
                    };
                    mCountDownTimer.start();
                    dialog.show();
                }
            }

//            if (bh.heartBeatBool) {
//
//            } else {
//                Toast.makeText(SelectBaysActivity.this, "Device not Connected", Toast.LENGTH_SHORT).show();
//            }
        });

        btnCancel.setOnClickListener(view -> {
            orderViewModel.delete(currentOrder);
            if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                if (bh.heartBeatBool) {
                    StringBuilder bayIds = new StringBuilder();
                    for (Bay bay : availableBays2) {
                        bayIds.append(bay.getCalibratedId()).append(",");
                        bay.setDoorStatus(BayDoorStatus.CLOSE);
                        bay.setLed(preference.getBayColorWhenEmpty());
                        bay.setStatus(BayStatus.AVAILABLE);
                        bayViewModel.update(bay);
                        openBay(bay);
                    }
                    if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                        try {
                            InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());
                            JSONObject object = new JSONObject();

                            object.put("type", "cancel");
                            object.put("bayIds", bayIds);

                            helper.sendMessage(object, ipAddress);
                        } catch (Exception e) {
                            Log.e("Exception", e.getMessage());
                        }
//                bh.send("T" + bayIds + "*@");
                    }
                } else {
                    Toast.makeText(SelectBaysActivity.this, "Device not Connected", Toast.LENGTH_SHORT).show();
                }
            } else {
                StringBuilder bayIds = new StringBuilder();
                for (Bay bay : availableBays2) {
                    bayIds.append(bay.getCalibratedId()).append(",");
                    bay.setDoorStatus(BayDoorStatus.CLOSE);
                    bay.setLed(preference.getBayColorWhenEmpty());
                    bay.setStatus(BayStatus.AVAILABLE);
                    bayViewModel.update(bay);
                    openBay(bay);
                }
                if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                    try {
                        InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());
                        JSONObject object = new JSONObject();

                        object.put("type", "cancel");
                        object.put("bayIds", bayIds);

                        helper.sendMessage(object, ipAddress);
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }
//                bh.send("T" + bayIds + "*@");
                }
            }

            startActivity(new Intent(this, LoadFlowErrorActivity.class));
        });

        rl1.setOnClickListener(view -> {
            timer.cancel();
            timer.start();
        });

        handleContinue();
    }


    private void handleContinue() {
        btnContinue.setOnClickListener(view -> {
            if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)){
                if (bh.heartBeatBool) {
                    timer.cancel();
                    timer.start();
                    if (numOfBaysSelected == 0) {
                        Toast.makeText(this, "Select Number of bays you need before continue!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    //update order new values
                    orderViewModel.update(currentOrder);
                    fromActivity = "AddMoreBays";
                    getAvailableBays();
                } else {
                    Toast.makeText(SelectBaysActivity.this, "Device not Connected", Toast.LENGTH_SHORT).show();
                }
            }else{
                timer.cancel();
                timer.start();
                if (numOfBaysSelected == 0) {
                    Toast.makeText(this, "Select Number of bays you need before continue!", Toast.LENGTH_LONG).show();
                    return;
                }

                //update order new values
                orderViewModel.update(currentOrder);
                fromActivity = "AddMoreBays";
                getAvailableBays();
            }

        });
    }

    private void getAvailableBays() {
        //check how many bays to open
        int noOfBayToOpen = 0;
        if (Objects.equals(fromActivity, "AddMoreBays") && !Objects.equals(currentOrder.getNoOfBays(), numOfBaysSelected)) {
            System.out.println("open more bays : " + numOfBaysSelected);
            noOfBayToOpen = numOfBaysSelected;
        } else {
            noOfBayToOpen = currentOrder.getNoOfBays();
        }

        availableBays = bayViewModel.getRequiredBays(BayStatus.AVAILABLE, noOfBayToOpen);
        assignBaysToOrder();

    }

    private void assignBaysToOrder() {
        StringBuilder bays = new StringBuilder();
        for (Bay bay : availableBays) {
            bays.append(bay.getCalibratedId().toString()).append(",");
            //prepare to open the bay
            bay.setDoorStatus(BayDoorStatus.CLOSE);
            bay.setLed(preference.getBayColorWhenFull());
            bay.setStatus(BayStatus.OCCUPIED);
            bayViewModel.update(bay);

            OrderBay orderBay = new OrderBay();
            orderBay.setOrderId(currentOrder.getOrderId());
            orderBay.setBayId(bay.getBayId());
            orderBayViewModel.insert(orderBay);


            //open bay
//            openBay(bay);
        }

        if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
            //     a/uber/%1111%*123*#admin#
            Log.e("Courier ID Log", "" + currentOrder.getCourierId());
            String courierName = "";
            Courier courierToUpdate = courierViewModel.findCourierById(currentOrder.getCourierId());
            if (courierToUpdate != null) {
                courierName = courierToUpdate.getName();
            }
            try {
                InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());
                JSONObject object = new JSONObject();

                object.put("type", "load");
                object.put("courierName", courierName);
                object.put("orderNumber", currentOrder.getOrderNumber().toString());
                object.put("orderId", currentOrder.getOrderId());
                object.put("bays", bays);
                object.put("loadedBy", currentOrder.getLoadedByName());

                helper.sendMessage(object, ipAddress);

            } catch (Exception e) {
                Log.e("Exception", "" + e.getMessage());
            }


//            String dataToSend = "a"+courierName+"/%"+currentOrder.getOrderNumber()+"%$"+currentOrder.getOrderId()+"$*"+ bays +"*#"+currentOrder.getLoadedByName()+"#@";

//            bh.send(dataToSend);

        } else {

        }
        currentOrder.setStatus(OrderStatus.LOADED);
        orderViewModel.update(currentOrder);
        startActivity(new Intent(this, LoadFlowSuccessActivity.class));
    }

    private void openBay(Bay bay) {
        System.out.println("Open bay : " + bay.getCalibratedId());
        String hexToSend = serialHelper.serialHexMaker(
                bay.getCalibratedId(),
                bay.getDoorStatus().getValue(),
                0,
                bay.getLed().getValue()
        );

        try {
            System.out.println("open bay with hex : " + hexToSend);
            serialHelper.getSerialHelper().sendHex(hexToSend);
            Thread.sleep(500);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


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
        timer = new CountDownTimer(preference.getLatePickupTimerTimeoutLoadIn(), 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                String remainingSeconds = String.valueOf(seconds);
                timerValue.setText(remainingSeconds);

            }

            public void onFinish() {
                orderViewModel.delete(currentOrder);
                StringBuilder bayIds = new StringBuilder();
                for (Bay bay : availableBays2) {
                    bayIds.append(bay.getCalibratedId()).append(",");
                    bay.setDoorStatus(BayDoorStatus.CLOSE);
                    bay.setLed(preference.getBayColorWhenEmpty());
                    bay.setStatus(BayStatus.AVAILABLE);
                    bayViewModel.update(bay);
                    openBay(bay);
                }
                if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                    try {
                        InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());

                        JSONObject object = new JSONObject();

                        object.put("type", "cancel");
                        object.put("bayIds", bayIds);

                        helper.sendMessage(object, ipAddress);
                    } catch (Exception e) {
                        Log.e("Exception", "" + e.getMessage());
                    }
//                    bh.send("T" + bayIds + "*@");
                    Intent intent = new Intent(SelectBaysActivity.this, LoadingAreaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    Intent intent = new Intent(SelectBaysActivity.this, SelectCourierActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    @Override
    public void onMessageReceived2(String message) {

    }
}