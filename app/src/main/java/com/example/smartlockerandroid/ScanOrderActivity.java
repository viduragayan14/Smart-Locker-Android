package com.example.smartlockerandroid;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputFilter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartlockerandroid.data.enums.BayDoorStatus;
import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.enums.Flow;
import com.example.smartlockerandroid.data.enums.KeyboardInputMethod;
import com.example.smartlockerandroid.data.enums.OrderStatus;
import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.Courier;
import com.example.smartlockerandroid.data.model.Order;
import com.example.smartlockerandroid.data.model.OrderHistory;
import com.example.smartlockerandroid.data.model.PickupLog;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.model.relation.OrderWithBays;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.data.viewmodel.CourierViewModel;
import com.example.smartlockerandroid.data.viewmodel.LogViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderHistoryViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderViewModel;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.serialimpl.SerialHelperImpl;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

import org.json.JSONObject;

import java.net.InetAddress;
import java.util.List;
import java.util.regex.Pattern;

public class ScanOrderActivity extends AppCompatActivity implements KeyboardUtil.CustomKeys, UdpServerThread.MessageReceived {
    private ImageView btnClose;
    private EditText etOrderNo;
    private Long courierId;
    private String userName;
    private Courier courier;
    private Flow flow;
    private LiveData<OrderWithBays> orderWithBaysLiveData;
    private OrderViewModel orderViewModel;
    private LogViewModel logViewModel;
    private OrderHistoryViewModel orderHistoryViewModel;
    private BayViewModel bayViewModel;
    private OrderWithBays orderWithBaysToPick;
    private CourierViewModel courierViewModel;
    private SerialHelperImpl serialHelper;
    private Dialog dialog;
    private PreferenceViewModel preferenceViewModel;
    private Preference preference;
    private ImageView courierImage;
    private TextView tv;

    // Custom Keyboard
    private KeyboardView customKeyboard;
    private KeyboardUtil keyboardUtil;
    private RelativeLayout superParent;
    private LinearLayout keyboardParent;

    private CountDownTimer timer;
    UdpServerThread bh;
    private UdpHelper helper;
    String orderNumber = "";
    private TextView timerValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scan_order);

        helper = new UdpHelper();

        bh = UdpServerThread.getInstance(ScanOrderActivity.this, this);
        bh.setViewModelStoreOwner(ScanOrderActivity.this, this);
        //get intent extras
        Bundle extras = getIntent().getExtras();
        courierId = extras.getLong("courierId", 0);
        userName = extras.getString("userName");
        flow = (Flow) extras.getSerializable("flow");
        Courier c = (Courier) getIntent().getSerializableExtra("courier");

        //get serial instance
        serialHelper = SerialHelperImpl.getInstance(this);

        courierImage = findViewById(R.id.im_courier);

        customKeyboard = findViewById(R.id.kv_keyboard);
        keyboardParent = findViewById(R.id.ll_keyboard_father);
        superParent = findViewById(R.id.view_root);
        etOrderNo = findViewById(R.id.et_order_num_scan_order);
        tv = findViewById(R.id.tv_card_desc);
        timerValue = findViewById(R.id.imgVw_timer);
        TextView title = findViewById(R.id.title);
        ImageView close = findViewById(R.id.btn_close_scan_order2);
        close.setOnClickListener(view -> {
            Intent intent = new Intent(ScanOrderActivity.this, SelectCourierActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        tv.setText(c.getHeading().toString());

        if (flow.equals(Flow.LOADING_FLOW)) {
            title.setText("Load-In Order");
        } else {
            title.setText("Order Pick-Up");
        }

        if (c.getLogoData() != null) {
            courierImage.setImageBitmap(BitmapFactory.decodeByteArray(c.getLogoData(), 0, c.getLogoData().length));
        } else {
            courierImage.setImageDrawable(getResources().getDrawable(R.drawable.default_courier_logo));
        }
        keyboardUtil = new KeyboardUtil(this, etOrderNo, customKeyboard, keyboardParent, true, this);

        keyboardUtil.forbidSoftInputMethod();
        keyboardUtil.showKeyboard();

        etOrderNo.setOnTouchListener((view, motionEvent) -> {

//            keyboardUtil.shiftEnglish();
            keyboardUtil.showKeyboard();

            return false;
        });

        superParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                timer.start();
                keyboardUtil.hideKeyboard();
            }
        });

        init();
        getCourier();
        closeActivity();

        handleOrderNumberInput();
    }

    private void init() {
        btnClose = findViewById(R.id.btn_close_scan_order);


        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        orderHistoryViewModel = new ViewModelProvider(this).get(OrderHistoryViewModel.class);
        bayViewModel = new ViewModelProvider(this).get(BayViewModel.class);
        courierViewModel = new ViewModelProvider(this).get(CourierViewModel.class);
        logViewModel = new ViewModelProvider(this).get(LogViewModel.class);

        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
        preference = preferenceViewModel.getPreference();
    }

    private void getCourier() {
        courier = courierViewModel.findCourierById(courierId);
        //set order number input type according to the courier selected
        etOrderNoInputConfig();
    }

    private void etOrderNoInputConfig() {
        //set edittext input type
        if (courier.getKeyboardInputMethod().equals(KeyboardInputMethod.NUMERIC)) {
            customKeyboard.setKeyboard(new Keyboard(ScanOrderActivity.this, R.xml.symbol_num));
//            etOrderNo.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            etOrderNo.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            InputFilter alphanumericFilter = (source, start, end, dest, dStart, dEnd) -> {
                //allow alphanumeric characters only
                Pattern pattern = Pattern.compile("^[a-zA-Z\\d\\s]+$");
                // Check each character against the pattern
                for (int i = start; i < end; i++) {
                    if (!pattern.matcher(String.valueOf(source.charAt(i))).matches()) {
                        return "";
                    }
                }
                // Return null to accept the characters
                return null;
            };
            customKeyboard.setKeyboard(new Keyboard(ScanOrderActivity.this, R.xml.symbol_abc));
//            etOrderNo.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            etOrderNo.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            etOrderNo.setFilters(new InputFilter[]{alphanumericFilter});
        }


        //show keyboard automatically
//        etOrderNo.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(etOrderNo, InputMethodManager.SHOW_IMPLICIT);
    }

    private void closeActivity() {
        btnClose.setOnClickListener(view -> {
            super.onBackPressed();
        });
    }

    private void handleOrderNumberInput() {

        etOrderNo.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String orderNumber = etOrderNo.getText().toString();
                if (flow.equals(Flow.LOADING_FLOW)) {

                    //TODO : verify courier id with order number - can have a order with same courierId and OrderNumber
                    Order orderCheck = orderViewModel.findOrderByCourierAndOrderNumber(courierId, orderNumber);
                    if (orderCheck != null) {
                        Toast.makeText(this, "Please enter the first 4 digits of the order number again!", Toast.LENGTH_LONG).show();
                        //set etOrderNo max length to 5 to clear the input
                        //etOrderNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});

                    } else {
                        loadingFlow(courierId, orderNumber);
                    }
                } else if (flow.equals(Flow.PICKUP_FLOW)) {
                    pickupFlow(courierId, orderNumber);
                }
                return true;
            }
            return false;
        });
    }

    private void loadingFlow(Long courierId, String orderNumber) {
        if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
            if (bh.heartBeatBool) {
                //create a new order
                Order order = new Order();
                order.setCourierId(courierId);
                order.setOrderNumber(orderNumber);
                order.setStatus(OrderStatus.INCOMPLETE);
                order.setLoadedAt(System.currentTimeMillis());
                order.setUpdatedAt(System.currentTimeMillis());
                order.setLoadedByName(userName);
                //TODO : get current user and set Loaded by
                //save order

                orderViewModel.insert(order).observe(this, orderId -> {
                    //go to bay select with oderCode
                    Log.e("From Where Order ID is coming", "" + orderId);
                    Intent intent = new Intent(this, SelectBaysActivity.class);
                    intent.putExtra("newOrderToBeLoaded", orderId);
                    startActivity(intent);
                });
            } else {
                Toast.makeText(ScanOrderActivity.this, "Device not Connected", Toast.LENGTH_SHORT).show();
            }
        } else {
            Order order = new Order();
            order.setCourierId(courierId);
            order.setOrderNumber(orderNumber);
            order.setStatus(OrderStatus.INCOMPLETE);
            order.setLoadedAt(System.currentTimeMillis());
            order.setUpdatedAt(System.currentTimeMillis());
            order.setLoadedByName(userName);
            //TODO : get current user and set Loaded by
            //save order

            orderViewModel.insert(order).observe(this, orderId -> {
                //go to bay select with oderCode
                Log.e("From Where Order ID is coming", "" + orderId);
                Intent intent = new Intent(this, SelectBaysActivity.class);
                intent.putExtra("newOrderToBeLoaded", orderId);
                startActivity(intent);
            });
        }


    }

    private void pickupFlow(Long courierId, String orderNumber) {
        //get order with bays by courierId and orderNumber
        orderWithBaysLiveData = orderViewModel.getOrderByCourierAndOrderNumber(courierId, orderNumber);
        orderWithBaysLiveData.observe(this, orderWithBays -> {
            if (orderWithBays == null) {
                Intent intent = new Intent(this, PickUpFlowErrorActivity.class);
                startActivity(intent);
            } else {
                //check if order is picked or cancelled
                OrderStatus orderStatus = orderWithBays.getOrder().getStatus();
                if (orderStatus.equals(OrderStatus.LOADED)) {
                    orderWithBaysToPick = orderWithBays;
                    preparePickup();
                } else if (orderStatus.equals(OrderStatus.OVERDUE)) {
                    //Toast.makeText(this, "This order is overdue, Please contact restaurant!", Toast.LENGTH_LONG).show();
                    //show over due dialog
                    Intent intent = new Intent(this, LatePickUpErrorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "This order is either picked up or cancelled!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void preparePickup() {
        Order orderToPick = orderWithBaysToPick.getOrder();
        List<Bay> baysToOpen = orderWithBaysToPick.getBays();
        orderWithBaysLiveData.removeObservers(this);

        orderToPick.setStatus(OrderStatus.PICKED);
        orderToPick.setPickedUpAt(System.currentTimeMillis());
        orderToPick.setUpdatedAt(System.currentTimeMillis());

        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setOrderId(orderToPick.getOrderId());
        orderHistory.setLoadedByName(orderToPick.getLoadedByName());
        orderHistory.setBarcode(orderToPick.getBarcode());
        orderHistory.setOrderNumber(orderToPick.getOrderNumber());
        orderHistory.setCourierId(orderToPick.getCourierId());
        orderHistory.setNoOfBays(orderToPick.getNoOfBays());
        orderHistory.setStatus(orderToPick.getStatus());
        orderHistory.setLoadedAt(orderToPick.getLoadedAt());
        orderHistory.setReloadedAt(orderToPick.getReloadedAt());
        orderHistory.setCanceledAt(orderToPick.getCanceledAt());
        orderHistory.setUpdatedAt(orderToPick.getUpdatedAt());
        orderHistory.setPickedUpAt(orderToPick.getPickedUpAt());
        orderHistory.setLoadedBy(orderToPick.getLoadedBy());
        orderHistory.setReloadedBy(orderToPick.getReloadedBy());
        orderHistory.setCanceledBy(orderToPick.getCanceledBy());

        orderViewModel.delete(orderToPick);
        orderHistoryViewModel.insert(orderHistory);
        Intent intent = new Intent(this, PickUpFlowThankYouActivity.class);
        intent.putExtra("orderToBePicked", "matchedOrder");
        startActivity(intent);

        StringBuilder bayIds = new StringBuilder();
        for (Bay bay : baysToOpen) {
            bayIds.append(bay.getCalibratedId().toString()).append(",");
            bay.setDoorStatus(BayDoorStatus.OPEN);
            bay.setLed(preference.getBayColorWhenEmpty());
            openBay(bay);

            System.out.println(bay.getCalibratedId() + " - " + bay.getStatus());
            bay.setStatus(BayStatus.AVAILABLE);
            bayViewModel.update(bay);
        }

        if (bh.heartBeatBool) {
            if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.FRONT_CONFIG)) {
                Log.e("Front COnfig", "Reached");
                if (preference.getClientIp() != null) {
                    try {
                        Log.e("More Logs", "Ip Address is Not NUll");
                        InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());

                        JSONObject object = new JSONObject();
                        object.put("type", "pickup");
                        object.put("orderId", orderHistory.getOrderId());
                        object.put("bayIds", bayIds);
                        Log.e("Pickup Problem", "Sending Pickup JSON" + object);
                        helper.sendMessageToClient(object, ipAddress);

                    } catch (Exception e) {
                        Log.e("Exception", "" + e.getMessage());
                    }

                } else {
                    Log.e("More Logs", "Ip Address is NUll");
                }


//            String dataToBeSent = "d" + orderHistory.getOrderId() + "%*" + bayIds + "*@";
//            Log.e("Data Sent", "" + dataToBeSent);
//            bh.send(dataToBeSent);
            }
        } else {
            PickupLog log = new PickupLog();
            log.setType("pickup");
            log.setOrderId(orderHistory.getOrderId());
            log.setBayIds(bayIds.toString());
            log.setCreatedDate(System.currentTimeMillis());
            log.setStatus(false);
            log.setExecutedStatus(false);
            logViewModel.insert(log);

            Toast.makeText(ScanOrderActivity.this, "Device not Connected", Toast.LENGTH_SHORT).show();
        }
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

    private void handleDialog(Preference preference) {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_late_pickup_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        RelativeLayout btnOk = dialog.findViewById(R.id.btn_dialog_ok_late_pickup);
        TextView tvMsg = dialog.findViewById(R.id.tv_msg_late_pickup);

        tvMsg.setText(preference.getLatePickupWarning());

        btnOk.setOnClickListener(viewYes -> {
            dialog.dismiss();
            Intent intent = new Intent(ScanOrderActivity.this, SelectCourierActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Log.e("TAG", keyCode + "------------");
            if (keyboardUtil.isShow()) {
                keyboardUtil.hideKeyboard();
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onConfirmPressed() {
        String orderNumber = etOrderNo.getText().toString();
        if (flow.equals(Flow.LOADING_FLOW)) {
            Order orderCheck = orderViewModel.findOrderByCourierAndOrderNumber(courierId, orderNumber);
            if (orderCheck != null) {
                Toast.makeText(this, "Please enter the first 4 digits of the order number again!", Toast.LENGTH_LONG).show();
                //set etOrderNo max length to 5 to clear the input
                //etOrderNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
            } else {
                loadingFlow(courierId, orderNumber);
            }
        } else if (flow.equals(Flow.PICKUP_FLOW)) {
            pickupFlow(courierId, orderNumber);
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
                long seconds = millisUntilFinished / 1000;
                String remainingSeconds = String.valueOf(seconds);
                timerValue.setText(remainingSeconds);
            }

            public void onFinish() {
                if (preference.getConnected() && preference.getConfig().equalsIgnoreCase(BackLoadingHelper.BACK_CONFIG)) {
                    Intent intent = new Intent(ScanOrderActivity.this, LoadingAreaActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    Intent intent = new Intent(ScanOrderActivity.this, SelectCourierActivity.class);
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        Log.e("orderNumber", String.valueOf(e.getAction()));
        if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() != KeyEvent.KEYCODE_ENTER) {
            Log.i("TAG", "dispatchKeyEvent: " + e);
            char pressedKey = (char) e.getUnicodeChar();
            orderNumber += pressedKey;
        }
        //if (e.getAction() == KeyEvent.ACTION_DOWN && barcode.length()==4) {
        if (e.getAction() == KeyEvent.ACTION_DOWN && e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

            orderNumber = orderNumber.replaceAll("\\s", "");
            if (flow.equals(Flow.LOADING_FLOW)) {
                Order orderCheck = orderViewModel.findOrderByCourierAndOrderNumber(courierId, orderNumber);
                if (orderCheck != null) {
                    Toast.makeText(this, "Please enter the first 4 digits of the order number again!", Toast.LENGTH_LONG).show();
                    //set etOrderNo max length to 5 to clear the input
                    //etOrderNo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
                } else {
                    loadingFlow(courierId, orderNumber);
                }
            } else if (flow.equals(Flow.PICKUP_FLOW)) {
                pickupFlow(courierId, orderNumber);
            }

            orderNumber = "";
            return true;
        } else {
            return false;
        }


    }
}