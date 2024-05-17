package com.example.smartlockerandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartlockerandroid.data.enums.BayDoorStatus;
import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.enums.OrderStatus;
import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.Order;
import com.example.smartlockerandroid.data.model.OrderBay;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderBayViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderViewModel;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.serialimpl.SerialHelperImpl;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

import java.util.List;
import java.util.Objects;

public class LoadOrderActivity extends AppCompatActivity implements UdpServerThread.MessageReceived {
    private Button btnClose, btnAddMoreBays, btnDone, btnCancel;
    private Long newOrderId;
    private Integer numOfBaysSelected = 1;
    private OrderViewModel orderViewModel;
    private BayViewModel bayViewModel;
    private OrderBayViewModel orderBayViewModel;
    private Order currentOrder;
    private List<Bay> availableBays;
    private String fromActivity;
    private SerialHelperImpl serialHelper;
    private Integer noOfBaysAvailable;
    private PreferenceViewModel preferenceViewModel;
    private Preference preference;
    UdpServerThread bh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_load_order);
        bh = UdpServerThread.getInstance(LoadOrderActivity.this, this);
        bh.setViewModelStoreOwner(LoadOrderActivity.this,this);
        newOrderId = getIntent().getLongExtra("newOrderToBeLoaded", 0);
        numOfBaysSelected = getIntent().getIntExtra("noOfBaysSelected", 1);
        fromActivity = getIntent().getStringExtra("fromActivity");
        Toast.makeText(this, "OrderCode : " + newOrderId + " | baysToBeOpened : " + numOfBaysSelected, Toast.LENGTH_SHORT).show();

        //get serial instance
        serialHelper = SerialHelperImpl.getInstance(this);

        init();
        closeActivity();
        handleCancel();
    }

    private void init() {
        btnClose = findViewById(R.id.btn_close_load_order);
        btnAddMoreBays = findViewById(R.id.btn_add_more_bays_load_order);
        btnDone = findViewById(R.id.btn_done_load_order);
        btnCancel = findViewById(R.id.btn_cancel_load_order);

        //don't need this btn
        btnClose.setVisibility(View.GONE);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        currentOrder = orderViewModel.getOrderById(newOrderId);

        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
        preference = preferenceViewModel.getPreference();

        orderBayViewModel = new ViewModelProvider(this).get(OrderBayViewModel.class);

        bayViewModel = new ViewModelProvider(this).get(BayViewModel.class);
        noOfBaysAvailable = bayViewModel.getNumberOfAvailableBays();

        getAvailableBays();
        handleAddMoreBays();
        handleDone();
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
        for (Bay bay : availableBays) {

            //prepare to open the bay
            bay.setDoorStatus(BayDoorStatus.OPEN);
            bay.setLed(preference.getBayColorWhenFull());
            bay.setStatus(BayStatus.OCCUPIED);
            bayViewModel.update(bay);

            OrderBay orderBay = new OrderBay();
            orderBay.setOrderId(currentOrder.getOrderId());
            orderBay.setBayId(bay.getBayId());
            orderBayViewModel.insert(orderBay);

            //open bay
            openBay(bay);
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

    private void closeActivity() {
        btnClose.setOnClickListener(view -> {
            //this.finish();
            //todo
            Toast.makeText(view.getContext(), "Not Configured!", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleCancel() {
        btnCancel.setOnClickListener(view -> {
            //todo
            Toast.makeText(view.getContext(), "Not Configured!", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleDone() {
        btnDone.setOnClickListener(view -> {
            // todo: save order details in the db
            //no need to insert in new table
            currentOrder.setStatus(OrderStatus.LOADED);
            orderViewModel.update(currentOrder);

            //Toast.makeText(this, "Order Loaded Successfully! \n OrderCode : " + currentOrder.getOrderId() + " | Num of bays : " + currentOrder.getNoOfBays(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SelectCourierActivity.class));
        });
    }

    private void handleAddMoreBays() {
        btnAddMoreBays.setOnClickListener(view -> {

            if (noOfBaysAvailable > numOfBaysSelected) {
                Intent intent = new Intent(this, AddMoreBaysActivity.class);
                intent.putExtra("noOfBaysSelected", 1);
                intent.putExtra("newOrderToBeLoaded", newOrderId);
                intent.putExtra("fromActivity", "LoadOrder");
                startActivity(intent);
            } else {
                Toast.makeText(this, "Sorry, can't select more, Other bays are full!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onMessageReceived2(String message) {

    }
}