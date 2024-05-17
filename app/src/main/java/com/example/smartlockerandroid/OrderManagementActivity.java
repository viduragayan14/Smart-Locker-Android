package com.example.smartlockerandroid;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlockerandroid.adapters.OrderListAdapter;
import com.example.smartlockerandroid.data.enums.BayDoorStatus;
import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.enums.OrderStatus;
import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.OrderHistory;
import com.example.smartlockerandroid.data.model.PickupLog;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.model.relation.OrderWithCourierAndBays;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.data.viewmodel.LogViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderHistoryViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderViewModel;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.serialimpl.SerialHelperImpl;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.List;
import java.util.Objects;

public class OrderManagementActivity extends AppCompatActivity implements OrderListAdapter.OnItemClickListener, UdpServerThread.MessageReceived {
    Long previousOrderId = 0L;
    private ImageView btnClose;
    private RecyclerView mRecyclerView;
    private ConstraintLayout superParent;
    private RecyclerView.LayoutManager mLayoutManager;
    private OrderViewModel orderViewModel;
    private OrderHistoryViewModel orderHistoryViewModel;
    private BayViewModel bayViewModel;
    private LinearLayout bottom;
    private RelativeLayout btnClear, btnReload;
    private PreferenceViewModel preferenceViewModel;
    private SerialHelperImpl serialHelper;
    private Preference preference;
    private CountDownTimer timer;
    UdpServerThread bh;
    private UdpHelper helper;
    private LogViewModel logViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        setContentView(R.layout.activity_order_management_r);
        helper = new UdpHelper();
        bh = UdpServerThread.getInstance(OrderManagementActivity.this, this);
        bh.setViewModelStoreOwner(OrderManagementActivity.this, this);
        init();
        closeActivity();
    }

    private void init() {
        btnClose = findViewById(R.id.imageView_close_order_management);
        btnClear = findViewById(R.id.btn_clear_order_management);
        btnReload = findViewById(R.id.btn_reload_order_management);
        bottom = findViewById(R.id.bottom);
        bottom.setVisibility(View.GONE);

        mRecyclerView = findViewById(R.id.rv_order_list_order_management);
        superParent = findViewById(R.id.super_parent);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //get serial instance
        serialHelper = SerialHelperImpl.getInstance(this);
        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
        preference = preferenceViewModel.getPreference();
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderHistoryViewModel = new ViewModelProvider(this).get(OrderHistoryViewModel.class);
        bayViewModel = new ViewModelProvider(this).get(BayViewModel.class);
        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);

        superParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                timer.start();
            }
        });

        orderViewModel.getAllOrders().observe(this, orderWithCourierAndBays -> {

            OrderListAdapter orderListAdapter = new OrderListAdapter(orderWithCourierAndBays, orderViewModel, bayViewModel, preference, serialHelper, OrderManagementActivity.this, 2);
            orderListAdapter.setOnItemClickListener(this);
            //orderListAdapter.setOrderList(orderWithCourierAndBays);
            mRecyclerView.setAdapter(orderListAdapter);
        });
    }

    private void closeActivity() {
        btnClose.setOnClickListener(view -> {
            this.finish();
        });
    }

    @Override
    public void onItemClick(OrderWithCourierAndBays order, Handler handler, Runnable updateTimeRunnable) {
        //show clear and reload buttons only when the order is in loaded or in overdue state

        btnClear.setVisibility(View.GONE);
        btnReload.setVisibility(View.GONE);
        if (order.getOrder().getStatus().equals(OrderStatus.LOADED) || order.getOrder().getStatus().equals(OrderStatus.OVERDUE)) {
            btnClear.setVisibility(View.VISIBLE);
            btnReload.setVisibility(View.VISIBLE);
        }
        // todo : remove if need
        if (order.getOrder().getStatus().equals(OrderStatus.INCOMPLETE)) {
            btnClear.setVisibility(View.VISIBLE);
        }

        Long currentOrderId = order.getOrder().getOrderId();
        //same item has  selected
        if (Objects.equals(previousOrderId, currentOrderId)) {
            if (bottom.getVisibility() == View.VISIBLE) {
                hideOrderDetails();
            } else {
                showOrderDetails(order, handler, updateTimeRunnable);
            }
        } else {
            //new item has selected
            previousOrderId = currentOrderId;
            showOrderDetails(order, handler, updateTimeRunnable);
        }
    }

    private void showOrderDetails(OrderWithCourierAndBays order, Handler handler, Runnable updateTimeRunnable) {
        bottom.setVisibility(View.VISIBLE);
        bottom.setAlpha(0.0f);
        bottom.animate()
                .alpha(1.0f)
                .setDuration(1000)
                .start();


        handleClearOrder(order, handler, updateTimeRunnable);
        handleReloadOrder(order);
    }

    private void hideOrderDetails() {
        bottom.animate()
                .alpha(0.0f)
                .setDuration(500)
                .withEndAction(() -> bottom.setVisibility(View.GONE))
                .start();
    }

    //cancel order
    private void handleClearOrder(OrderWithCourierAndBays order, Handler handler, Runnable updateTimeRunnable) {
        btnClear.setOnClickListener(view -> {
            if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                if (bh.heartBeatBool) {
                    handleDialog(order, handler, updateTimeRunnable);
                } else {
                    Toast.makeText(this, "Device Not Connected", Toast.LENGTH_SHORT).show();
                }
            }else{
                handleDialog(order, handler, updateTimeRunnable);
            }
            //make order status as canceled

        });
    }

    private void handleDialog(OrderWithCourierAndBays order, Handler handler, Runnable updateTimeRunnable) {
        Dialog dialog = new Dialog(OrderManagementActivity.this);
        dialog.setContentView(R.layout.delete_order_confirm_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RelativeLayout btnDelete = dialog.findViewById(R.id.btn_delete_order);
        RelativeLayout btnCancel = dialog.findViewById(R.id.btn_cancel);

        btnCancel.setOnClickListener(viewCancel -> {
            dialog.dismiss();
        });

        btnDelete.setOnClickListener(viewYes -> {

            handler.removeCallbacks(updateTimeRunnable);
            dialog.dismiss();
            if (order.getOrder().getStatus() == OrderStatus.OVERDUE) {
                order.getOrder().setStatus(OrderStatus.LATE);
            } else {
                order.getOrder().setStatus(OrderStatus.CANCELED);
            }
            order.getOrder().setCanceledAt(System.currentTimeMillis());

            OrderHistory orderHistory = new OrderHistory();
            orderHistory.setOrderId(order.getOrder().getOrderId());
            orderHistory.setLoadedByName(order.getOrder().getLoadedByName());
            orderHistory.setBarcode(order.getOrder().getBarcode());
            orderHistory.setOrderNumber(order.getOrder().getOrderNumber());
            orderHistory.setCourierId(order.getOrder().getCourierId());
            orderHistory.setNoOfBays(order.getOrder().getNoOfBays());
            orderHistory.setStatus(order.getOrder().getStatus());
            orderHistory.setLoadedAt(order.getOrder().getLoadedAt());
            orderHistory.setReloadedAt(order.getOrder().getReloadedAt());
            orderHistory.setCanceledAt(order.getOrder().getCanceledAt());
            orderHistory.setUpdatedAt(order.getOrder().getUpdatedAt());
            orderHistory.setPickedUpAt(order.getOrder().getPickedUpAt());
            orderHistory.setLoadedBy(order.getOrder().getLoadedBy());
            orderHistory.setReloadedBy(order.getOrder().getReloadedBy());
            orderHistory.setCanceledBy(order.getOrder().getCanceledBy());


            orderViewModel.delete(order.getOrder());
            orderHistoryViewModel.insert(orderHistory);


            handleCanceledOrderBayAction(order.getBays(), order.getOrder().getOrderId());

            //close order details view
            bottom.setVisibility(View.GONE);


        });
        dialog.show();

    }

    private void handleReloadOrder(OrderWithCourierAndBays order) {
        btnReload.setOnClickListener(view -> {
            if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                if (bh.heartBeatBool) {
                    order.getOrder().setReloadedAt(System.currentTimeMillis());
                    orderViewModel.update(order.getOrder());
                    handleReloadOrderBayAction(order.getBays(), order.getOrder().getOrderId());
                } else {
                    Toast.makeText(OrderManagementActivity.this, "Device not Connected", Toast.LENGTH_SHORT).show();
                }
            }else{
                order.getOrder().setReloadedAt(System.currentTimeMillis());
                orderViewModel.update(order.getOrder());
                handleReloadOrderBayAction(order.getBays(), order.getOrder().getOrderId());
            }
        });
    }

    private void handleCanceledOrderBayAction(List<Bay> bays, Long orderId) {

//        if (bh.heartBeatBool) {
            StringBuilder bayIds = new StringBuilder();
            for (Bay bay : bays) {
                bayIds.append(bay.getCalibratedId().toString()).append(",");
//                bay.setDoorStatus(BayDoorStatus.OPEN);
//                bay.setLed(preference.getBayColorWhenReloading());
//                //open bay
//                serialHelper.executeBayAction(bay);
//
//                //update bay
//                //TODO : how much time should cancelling led should light - dialog
                bay.setStatus(BayStatus.AVAILABLE);
                bay.setDoorStatus(BayDoorStatus.CLOSE);
//                bay.setLed(preference.getBayColorWhenEmpty());
                bayViewModel.update(bay);
                serialHelper.executeBayAction(bay);
            }


            if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {

//            String dataToBeSent = "c" + orderId.toString() + "%*" + bayIds + "*@";
//            Log.e("Data Sent", "" + dataToBeSent);
//            bh.send(dataToBeSent);

                if (preference.getClientIp() != null) {
                    try {
                        Log.e("More Logs", "Ip Address is Not NUll");
                        InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());

                        JSONObject object = new JSONObject();
                        try {
                            object.put("type", "remove");
                            object.put("orderId", orderId.toString());
                            object.put("bayIds", bayIds);
                        } catch (JSONException e) {
                            Log.e("Exception", "" + e.getMessage());
                        }

                        helper.sendMessage(object, ipAddress);

                    } catch (Exception e) {
                        Log.e("Exception", "" + e.getMessage());
                    }

                } else {

                    Log.e("More Logs", "Ip Address is NUll");
                }
            }
//        } else {
//            PickupLog log = new PickupLog();
//            log.setType("remove");
//            log.setOrderId(orderId);
//            log.setBayIds(bayIds1.toString());
//            log.setCreatedDate(System.currentTimeMillis());
//            log.setStatus(false);
//            log.setExecutedStatus(false);
//            logViewModel.insert(log);
//            Toast.makeText(OrderManagementActivity.this, "Device not Connected", Toast.LENGTH_SHORT).show();
//        }
    }

    private void handleReloadOrderBayAction(List<Bay> bays, Long orderId) {
        StringBuilder bayIds = new StringBuilder();
        for (Bay bay : bays) {
            bayIds.append(bay.getCalibratedId().toString()).append(",");
            bay.setDoorStatus(BayDoorStatus.OPEN);
            bay.setLed(preference.getBayColorWhenReloading());
            //open bay
            serialHelper.executeBayAction(bay);

            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                bay.setDoorStatus(BayDoorStatus.CLOSE);
                bay.setLed(preference.getBayColorWhenFull());
                bayViewModel.update(bay);
                serialHelper.executeBayAction(bay);
            }, 10000);

        }
        if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
//
//            String dataToBeSent = "b" + orderId.toString() + "%*" + bayIds + "*@";
//            Log.e("Data Sent", "" + dataToBeSent);
//            bh.send(dataToBeSent);
            if (preference.getClientIp() != null) {
                try {
                    JSONObject object = new JSONObject();

                    InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());
                    object.put("type", "reloading");
                    object.put("orderId", orderId.toString());
                    object.put("bayIds", bayIds);
                    Log.e("Reloading Data Send", "" + object.toString());
                    helper.sendMessage(object, ipAddress);
                } catch (Exception e) {
                    Log.e("Exception", "" + e.getMessage());
                }
            } else {
                Log.e("Exception", "Ip Address Null");
            }
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
        timer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {

                    Intent intent = new Intent(OrderManagementActivity.this, LoadingAreaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    Intent intent = new Intent(OrderManagementActivity.this, SelectCourierActivity.class);
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