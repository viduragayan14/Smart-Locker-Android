package com.example.smartlockerandroid.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartlockerandroid.R;
import com.example.smartlockerandroid.adapters.BayListAdapter;
import com.example.smartlockerandroid.data.enums.BayDoorStatus;
import com.example.smartlockerandroid.data.enums.BayLightStatus;
import com.example.smartlockerandroid.data.enums.BayStatus;
import com.example.smartlockerandroid.data.model.Bay;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.BayViewModel;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.databinding.FragmentBayManagementBinding;
import com.example.smartlockerandroid.serialimpl.SerialHelperImpl;
import com.example.smartlockerandroid.utils.UdpServerThread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BayManagementFragment extends Fragment implements UdpServerThread.MessageReceived {
    private final List<Integer> bayCount = Arrays.asList(4, 8, 12, 16, 20, 24, 28, 32);
    private FragmentBayManagementBinding binding;
    private Spinner spinnerNoOfBays, spinnerServiceBay;
    //    private Button btnSaveNoOfBays;
//    private Button btnSaveServiceBay;
    private RelativeLayout btnSaveBayFragment;
    private RelativeLayout btnSaveBayFragment2;
    private RelativeLayout btnTestServiceBay;
    private PreferenceViewModel preferenceViewModel;
    private BayViewModel bayViewModel;
    private Dialog dialog;
    private ArrayAdapter<Integer> dropdownItems;
    private Integer selectedNoOfBays;
    private SerialHelperImpl serialHelper;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    int i = 0;
    TextView tv;
    BayListAdapter listAdapter;
    UdpServerThread bh;
    Context context;

    private final List<Bay> availableBays = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentBayManagementBinding.inflate(inflater, container, false);
        bh = UdpServerThread.getInstance(context, this);
        bh.setViewModelStoreOwner(context, this);
        View root = binding.getRoot();
        init();
        return root;
    }

    private void init() {
        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
        bayViewModel = new ViewModelProvider(this).get(BayViewModel.class);
        serialHelper = SerialHelperImpl.getInstance(getContext());
        spinnerNoOfBays = binding.spinnerNoOfBaysBaySettingsFragment;
        spinnerServiceBay = binding.spinnerControlBayBaySettingsFragment;
//        btnSaveNoOfBays = binding.btnSaveNoOfBaysBaySettingsFragment;
        btnTestServiceBay = binding.btnTestBaySettingsFragment;
//        btnSaveServiceBay = binding.btnSaveServiceBayBaySettingsFragment;
        btnSaveBayFragment = binding.btnSaveBayFragment;
        btnSaveBayFragment2 = binding.btnSaveBayFragment2;
        mRecyclerView = binding.rvBayListBayCalibration;

        mLayoutManager = new LinearLayoutManager(binding.getRoot().getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        handleComponents();


    }

    private void handleComponents() {
        //set no of bays spinner values
        dropdownItems = new ArrayAdapter<>(getContext(), R.layout.list_item_dropdown, bayCount);
        spinnerNoOfBays.setAdapter(dropdownItems);

        preferenceViewModel.getPreferenceLiveData().observe(getViewLifecycleOwner(), preference -> {
            //set no of bays spinner values
            spinnerNoOfBays.setSelection(dropdownItems.getPosition(preference.getNoOfBays()));


            //set service bay spinner values
            List<Integer> bayCount = new ArrayList<>();
            for (int i = 1; i <= preference.getNoOfBays(); i++) {
                bayCount.add(i);
            }
            ArrayAdapter<Integer> dropdownItems = new ArrayAdapter<>(getContext(), R.layout.list_item_dropdown, bayCount);
            spinnerServiceBay.setAdapter(dropdownItems);
            spinnerServiceBay.setSelection(dropdownItems.getPosition(preference.getServiceBay()));

            handleSpinnerNoOfBays();
            handleSpinnerServiceBay(preference);
            handleBtnSaveNoOfBays(preference);
            handleOpenBaysBtn();
            handleUpdateLedBtn(preference);

            bayViewModel.getAllBays().observe(getViewLifecycleOwner(), bays -> {
                listAdapter = new BayListAdapter(bays, preference, bayViewModel, serialHelper, preference.getNoOfBays());
                mRecyclerView.setAdapter(listAdapter);
            });
            bayViewModel.getAllBaysMain().observe(getViewLifecycleOwner(), bays -> {
                availableBays.clear();
                availableBays.addAll(bays);
            });
        });
    }

    private void handleBtnSaveNoOfBays(Preference preference) {
//        btnSaveNoOfBays.setOnClickListener(v -> {

//        });
    }

    private void handleOpenBaysBtn() {
        binding.btnOpenBays.setOnClickListener(view -> {
            for (Bay bay : availableBays) {
                bay.setLed(BayLightStatus.GREEN);
//                    bay.setStatus(BayStatus.AVAILABLE);
                bay.setDoorStatus(BayDoorStatus.OPEN);
                bayViewModel.update(bay);
                openBay(bay);
            }


        });
    }

    private void handleUpdateLedBtn(Preference preference) {
        binding.btnUpdateLeds.setOnClickListener(view -> {
            for (Bay bay : availableBays) {
                Log.e("Bay Status",""+bay.getBayId() + " - " +bay.getStatus());
                if (bay.getStatus() == BayStatus.AVAILABLE) {
                    bay.setLed(preference.getBayColorWhenEmpty());
                } else if (bay.getStatus() == BayStatus.OCCUPIED) {
                    bay.setLed(preference.getBayColorWhenFull());
                } else if (bay.getStatus() == BayStatus.DEACTIVATED) {
                    bay.setLed(preference.getBayColorWhenEmpty());
                } else if (bay.getStatus() == BayStatus.SERVICE_BAY) {
                    bay.setLed(BayLightStatus.GREEN);
                }

                serialHelper.executeBayAction(bay);
                bay.setDoorStatus(BayDoorStatus.CLOSE);
                bayViewModel.update(bay);

            }


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

    private void handleSpinnerNoOfBays() {
        spinnerNoOfBays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedNoOfBays = (Integer) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void handleSpinnerServiceBay(Preference preference) {
        spinnerServiceBay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleBtnSaveServiceBay((Integer) parent.getItemAtPosition(position), preference);
                handleBtnTestServiceBay((Integer) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void handleBtnSaveServiceBay(Integer newServiceBaySelected, Preference preference) {
        btnSaveBayFragment.setOnClickListener(v -> {

            setServiceBay(preference.getServiceBay(), newServiceBaySelected);
            preference.setServiceBay(newServiceBaySelected);
            preferenceViewModel.update(preference);

        });

        btnSaveBayFragment2.setOnClickListener(v -> {

            if (selectedNoOfBays == null) {
                Toast.makeText(getContext(), "Please select number of bays", Toast.LENGTH_SHORT).show();
                return;
            }
            changeNumberOfBays(preference);
        });
    }

    private void handleBtnTestServiceBay(Integer serviceBay) {
        btnTestServiceBay.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Testing service bay" + serviceBay, Toast.LENGTH_LONG).show();
            serialHelper.executeBayCalibrationTestAction(serviceBay);
        });
    }

    private void handleDialog(Preference preference) {
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_change_number_of_bays_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        mProgressBar = dialog.findViewById(R.id.progressbar);
        ImageView close = dialog.findViewById(R.id.close);
        tv = dialog.findViewById(R.id.seconds);
        close.setOnClickListener(view -> dialog.dismiss());

        mProgressBar.setProgress(i);
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
            }
        };
        mCountDownTimer.start();
        dialog.show();
    }

    private void changeNumberOfBays(Preference preference) {
        if (selectedNoOfBays == null) {
            preference.setNoOfBays(4);
        } else {
            preference.setNoOfBays(selectedNoOfBays);
        }
        preference.setServiceBay(4);
        preferenceViewModel.update(preference);

        //reset all the bays
        //add new bays
        // set service bay as bay 4
        List<Bay> newBays = new ArrayList<>();
        Bay serviceBay = new Bay(
                BayDoorStatus.CLOSE,
                BayStatus.SERVICE_BAY,
                BayLightStatus.GREEN,
                4,
                4
        );

        newBays.add(serviceBay);
        //turn on default service bay light
        serialHelper.executeChangeLedStatusAction(4, BayLightStatus.GREEN);

        for (int i = 1; i <= preference.getNoOfBays(); i++) {
            if (i == 4) {
                continue;
            }
            Bay bay = new Bay(
                    BayDoorStatus.CLOSE,
                    BayStatus.AVAILABLE,
                    BayLightStatus.OFF,
                    i,
                    i
            );

            serialHelper.executeChangeLedStatusAction(i, bay.getLed());
            newBays.add(bay);
        }

        bayViewModel.addNewListOfBays(newBays);

        listAdapter.setNoOfBays(preference.getNoOfBays());

        handleDialog(preference);
    }

    private void setServiceBay(Integer currentBayId, Integer selectedBayId) {
        if (currentBayId == selectedBayId)
            return;

        //new service bay -> selected bay
        Bay newSelectedBay = bayViewModel.getBayByCalibratedId(selectedBayId);
        newSelectedBay.setStatus(BayStatus.SERVICE_BAY);
        newSelectedBay.setLed(BayLightStatus.GREEN);
        bayViewModel.update(newSelectedBay);
        //turn on service bay light - send signal
        serialHelper.executeChangeLedStatusAction(selectedBayId, BayLightStatus.GREEN);

        //current service bay -> available bay
        Bay currentBay = bayViewModel.getBayByCalibratedId(currentBayId);
        currentBay.setStatus(BayStatus.AVAILABLE);
        currentBay.setLed(BayLightStatus.OFF);
        bayViewModel.update(currentBay);
        //turn off current bay light - send signal
        serialHelper.executeChangeLedStatusAction(currentBayId, BayLightStatus.OFF);
    }

    @Override
    public void onMessageReceived2(String message) {

    }
}