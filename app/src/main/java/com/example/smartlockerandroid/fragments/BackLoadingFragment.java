package com.example.smartlockerandroid.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.smartlockerandroid.data.model.PickupLog;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.LogViewModel;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.databinding.FragmentBackLoadingBinding;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.NetworkScanner;
import com.example.smartlockerandroid.utils.UdpHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;
import com.hoho.android.usbserial.driver.UsbSerialDriver;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class BackLoadingFragment extends Fragment implements BackLoadingHelper.MessageReceived, UdpServerThread.MessageReceived {
    private FragmentBackLoadingBinding binding;
    TextView receiveEd;
    private PreferenceViewModel preferenceViewModel;
    Preference preference;
    Context context;
    //    BackLoadingHelper bh;
    UdpServerThread ut;
    private final Handler handler = new Handler();
    private final boolean isButtonEnabled = true;

    private LogViewModel logViewModel;
    private UdpHelper helper;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBackLoadingBinding.inflate(inflater, container, false);

        setTouchListenerRecursive(binding.getRoot());
//        bh = BackLoadingHelper.getInstance(context, this);
        ut = UdpServerThread.getInstance(context, this);
//        bh.setViewModelStoreOwner(context);
        ut.setViewModelStoreOwner(context, this);
//        bh.setMessageReceived(this);
        List<String> configNames = new ArrayList<>();
        configNames.add(BackLoadingHelper.FRONT_CONFIG);
        configNames.add(BackLoadingHelper.BACK_CONFIG);
        ArrayAdapter<String> configAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, configNames);
        logViewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(LogViewModel.class);

        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);
        preference = preferenceViewModel.getPreference();
        ProgressBar progressBar = binding.progressBar;
        NetworkScanner n = new NetworkScanner();
        binding.syncBtn.setOnClickListener(view -> {
            helper = new UdpHelper();
            progressBar.setVisibility(View.VISIBLE); // Show progress bar

            logViewModel.getPickupLogs().observe((LifecycleOwner) context, list -> {
                if (list != null && list.size() > 0) {
                    AtomicInteger remainingMessages = new AtomicInteger(list.size());
                    AtomicInteger sentMessages = new AtomicInteger(0);

                    for (PickupLog p : list) {
                        try {
                            InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());
                            JSONObject pingJson = new JSONObject();
                            pingJson.put("type", p.getType());
                            pingJson.put("orderId", p.getOrderId());
                            pingJson.put("bayIds", p.getBayIds());

                            helper.sendMessage(pingJson, ipAddress);
                            sentMessages.incrementAndGet();

                            p.setStatus(true);
                            logViewModel.update(p);
                        } catch (Exception e) {
                            Log.e("Exception 123",""+e.getMessage());
                        }
                    }

                    // Check if all messages are sent
                    if (sentMessages.get() == remainingMessages.get()) {
                        // All messages sent, hide progress bar
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    // No messages to send, hide progress bar
                    progressBar.setVisibility(View.GONE);
                }
            });
        });
        binding.btnScan.setOnClickListener(view -> {
            helper = new UdpHelper();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", "scan");
                jsonObject.put("message", "Scan");
            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
            }

            helper.scanNetwork(jsonObject);
        });

        binding.ipAddress.setText(preference.getClientIp());


        Log.e("Backloading Connected ", "" + preference.getConnected());
        binding.switchEnableBackLoading.setChecked(preference.getConnected());
        if (preference.getConnected()) {
//            setSpinners(bh.getDrivers());
        }
        binding.switchEnableBackLoading.setOnCheckedChangeListener((compoundButton, b) -> {
            Log.e("Backloading Connected ", "" + b);
            if (b) {
//                bh.connect();
                preference.setConnected(true);
                preferenceViewModel.update(preference);
//                setSpinners(bh.getDrivers());
            } else {
//                bh.disconnect();
                preference.setConnected(false);
                preferenceViewModel.update(preference);
            }
        });


        receiveEd = binding.receiveMessage;

        configAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.configSpinner.setAdapter(configAdapter);


        binding.configSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Handle item selection here
                String selectedConfig = (String) parentView.getItemAtPosition(position);
                preference.setConfig(selectedConfig);
                preferenceViewModel.update(preference);
                binding.configSpinner.setSelection(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing when nothing is selected
            }
        });

        setSpinnerSelection(preference.getConfig(), configAdapter);


        binding.btnSend.setOnClickListener(view -> {
            if (ut.heartBeatBool) {
                helper = new UdpHelper();
                if (preference.getConfig().equals(BackLoadingHelper.BACK_CONFIG)) {
                    try {
                        InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());
                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put("type", "message");
                        jsonObject.put("message", binding.sendMessage.getText().toString());
                        helper.sendMessage(jsonObject, ipAddress);
                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                    }


                } else {
                    if (preference.getClientIp() != null) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("type", "message");
                            jsonObject.put("message", binding.sendMessage.getText().toString());
                            InetAddress ipAddress = InetAddress.getByName(preference.getClientIp());
                            helper.sendMessageToClient(jsonObject, ipAddress);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                    } else {
                        Log.e("Something", "IP Address Null");
                    }
                }


//            clientThread.sendMessage(binding.sendMessage.getText().toString());
//            if (isButtonEnabled) {
//                Log.e("called", "called");
//                binding.btnSend.setEnabled(false);
//                handler.postDelayed(() -> {
//                    binding.btnSend.setEnabled(true);
//                    isButtonEnabled = true;
//                }, 2000);
//                bh.send(("+" + binding.sendMessage.getText()) + "+@");
//                isButtonEnabled = false;
//            }
            } else {
                Toast.makeText(context, "Device not Connected", Toast.LENGTH_SHORT).show();
            }
        });


        return binding.getRoot();

    }

    private void setSpinnerSelection(String valueToSelect, ArrayAdapter<String> configAdapter) {
        int index = configAdapter.getPosition(valueToSelect);
        Log.e("Config Log", String.valueOf(index));
        binding.configSpinner.setSelection(Math.max(index, 0));
    }


    private void setTouchListenerRecursive(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                setTouchListenerRecursive(child);
            }
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Get the currently focused view, if any
                    View focusedView = requireActivity().getCurrentFocus();

                    // If the focused view is an EditText, hide the keyboard
                    if (focusedView instanceof EditText) {
                        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
    }



    @Override
    public void onMessageReceived(String message) {
        int IdStartIndex = 1;
        int IdEndIndex = message.indexOf('+', IdStartIndex);
        String r_message = message.substring(IdStartIndex, IdEndIndex);
        receiveEd.setText(r_message);
    }

    @Override
    public void onMessageReceived2(String message) {
        receiveEd.setText(message);
//        preference.setClientIp(hostAddress);
//        Log.e("Sender's IP Address", "" + hostAddress);
//        preferenceViewModel.update(preference);
    }
}



