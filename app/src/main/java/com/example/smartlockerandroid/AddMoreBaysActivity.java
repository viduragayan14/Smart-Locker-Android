package com.example.smartlockerandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartlockerandroid.data.model.Order;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.data.viewmodel.OrderViewModel;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

import java.util.Objects;

public class AddMoreBaysActivity extends AppCompatActivity implements UdpServerThread.MessageReceived {
    private Button btnClose, btnAdd, btnReduce, btnOpen, btnBack;
    private TextView tvNumberOfBaysSelected;
    private Long newOrderId;
    private Integer numOfBaysSelected = 0;
    private OrderViewModel orderViewModel;
    private Order currentOrder;
    private String fromActivity;
    private BayViewModel bayViewModel;
    private Integer noOfBaysAvailable;
    UdpServerThread bh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_more_bays);
        bh = UdpServerThread.getInstance(AddMoreBaysActivity.this, this);
        bh.setViewModelStoreOwner(AddMoreBaysActivity.this,this);
        newOrderId = getIntent().getLongExtra("newOrderToBeLoaded", 0);
        numOfBaysSelected = getIntent().getIntExtra("noOfBaysSelected", 0);
        fromActivity = getIntent().getStringExtra("fromActivity");

        init();
        closeActivity();
    }

    private void init() {
        bayViewModel = new ViewModelProvider(this).get(BayViewModel.class);
        noOfBaysAvailable = bayViewModel.getNumberOfAvailableBays();

        btnClose = findViewById(R.id.btn_close_add_more_bays);
        btnAdd = findViewById(R.id.btn_add_bays);
        btnReduce = findViewById(R.id.btn_reduce_bays);
        btnOpen = findViewById(R.id.btn_open_add_more_bays);
        btnBack = findViewById(R.id.btn_back_add_more_bay);
        tvNumberOfBaysSelected = findViewById(R.id.tv_selected_bays_add_more_bays);
        tvNumberOfBaysSelected.setText(String.valueOf(numOfBaysSelected));

        //don't need this btn
        btnClose.setVisibility(View.GONE);

        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
        currentOrder = orderViewModel.getOrderById(newOrderId);
        handleAddBays();
        handleReduceBays();
        handleBack();
        handleOpen();
    }

    private void closeActivity() {
        btnClose.setOnClickListener(view -> {
            this.finish();
        });
    }

    private void handleOpen() {
        btnOpen.setOnClickListener(view -> {
            //set number of bays selected
            int noOfBayToOpen = 0;
            if (Objects.equals(fromActivity, "LoadOrder")) {
                noOfBayToOpen = currentOrder.getNoOfBays() + numOfBaysSelected;
            }
            if (Objects.equals(fromActivity, "SelectBays")) {
                noOfBayToOpen = numOfBaysSelected;
            }

            //update order new values
            currentOrder.setNoOfBays(noOfBayToOpen);
            //no need to insert in new table
            orderViewModel.update(currentOrder);

            Intent intent = new Intent(this, LoadOrderActivity.class);
            intent.putExtra("noOfBaysSelected", numOfBaysSelected);
            intent.putExtra("newOrderToBeLoaded", newOrderId);
            intent.putExtra("fromActivity", "AddMoreBays");
            startActivity(intent);
        });
    }

    private void handleBack() {
        btnBack.setOnClickListener(view -> {
            finish();
        });
    }

    private void handleReduceBays() {
        btnReduce.setOnClickListener(view -> {
            if (numOfBaysSelected != 1) {
                numOfBaysSelected--;
                tvNumberOfBaysSelected.setText(String.valueOf(numOfBaysSelected));
            } else {
                Toast.makeText(view.getContext(), "You've to select at least one bay!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAddBays() {
        btnAdd.setOnClickListener(view -> {
            if (noOfBaysAvailable > numOfBaysSelected) {
                numOfBaysSelected++;
                tvNumberOfBaysSelected.setText(String.valueOf(numOfBaysSelected));
            } else {
                Toast.makeText(this, "Sorry, can't select more, Other bays are full!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onMessageReceived2(String message) {

    }
}