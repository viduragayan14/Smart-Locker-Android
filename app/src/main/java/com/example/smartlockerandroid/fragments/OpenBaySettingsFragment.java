package com.example.smartlockerandroid.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartlockerandroid.R;
import com.example.smartlockerandroid.data.enums.BayLightStatus;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.databinding.FragmentOpenBaySettingsBinding;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class OpenBaySettingsFragment extends Fragment implements UdpServerThread.MessageReceived {
    private PreferenceViewModel preferenceViewModel;
    private FragmentOpenBaySettingsBinding binding;
    private EditText etTimeout1, etTimeout2;
    private RelativeLayout btnTimer1, btnTimer2;
    private Spinner spinnerLedColor1, spinnerLedColor2;
    private SwitchCompat switchTimerStatus1, switchTimerStatus2;
    private Dialog timePickerDialog;
    private Button btnSave;

    Context context;
    UdpServerThread bh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOpenBaySettingsBinding.inflate(inflater, container, false);
        bh =  UdpServerThread.getInstance(context,  this);
        bh.setViewModelStoreOwner(context,this);
        View root = binding.getRoot();
        init();
        return root;
    }

    private void init() {
        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);

        etTimeout1 = binding.etTimeout1OpenBaySettings;
        btnTimer1 = binding.btnTimeout1OpenBaySettings;
        spinnerLedColor1 = binding.spinnerTimeout1LedColorOpenBaySettings;
        switchTimerStatus1 = binding.switchBeepTimeout1OpenBaySettings;

        etTimeout2 = binding.etTimeout2OpenBaySettings;
        btnTimer2 = binding.btnTimeout2OpenBaySettings;
        spinnerLedColor2 = binding.spinnerTimeout2LedColorOpenBaySettings;
        switchTimerStatus2 = binding.switchBeepTimeout2OpenBaySettings;

        btnSave = binding.btnSaveOpenBayFragment;


        handleComponents();
        handleSave();
    }

    private void handleSave() {
        btnSave.setOnClickListener(view -> {
            //requireActivity().finish();
            Toast.makeText(getContext(), "Open bay settings saved successfully!", Toast.LENGTH_LONG).show();
        });
    }

    private void handleComponents() {
        //set spinner values
        List<String> ledColors = Arrays.asList("OFF", "RED", "GREEN", "YELLOW");
        ArrayAdapter<String> dropdownItems = new ArrayAdapter<>(getContext(), R.layout.list_item_dropdown, ledColors);
        spinnerLedColor1.setAdapter(dropdownItems);
        spinnerLedColor2.setAdapter(dropdownItems);

        preferenceViewModel.getPreferenceLiveData().observe(getViewLifecycleOwner(), preference -> {
            switchTimerStatus1.setChecked(preference.getBayTimeoutTimeBeepStatus1());
            etTimeout1.setText(convertTimeoutValueToString(preference.getBayTimeoutTime1()));
            spinnerLedColor1.setSelection(dropdownItems.getPosition(preference.getBayTimeoutColor1().toString()));

            switchTimerStatus2.setChecked(preference.getBayTimeoutTimeBeepStatus2());
            etTimeout2.setText(convertTimeoutValueToString(preference.getBayTimeoutTime2()));
            spinnerLedColor2.setSelection(dropdownItems.getPosition(preference.getBayTimeoutColor2().toString()));

            handleSwitchTimerStatus(preference);
            handleSpinnerLedColor(preference);
            handleTimerInputOpenBay(preference);
        });
    }

    private String convertTimeoutValueToString(Long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        return String.format(Locale.getDefault(), "%02d hr %02d min", hours, remainingMinutes);
    }

    private void handleSwitchTimerStatus(Preference preference) {
        switchTimerStatus1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preference.setBayTimeoutTimeBeepStatus1(b);
                preferenceViewModel.update(preference);
            }
        });

        switchTimerStatus2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preference.setBayTimeoutTimeBeepStatus2(b);
                preferenceViewModel.update(preference);
            }
        });

    }

    private void handleSpinnerLedColor(Preference preference) {
        spinnerLedColor1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preference.setBayTimeoutColor1(
                        BayLightStatus.valueOf(
                                (String) adapterView.getItemAtPosition(i)
                        )
                );
                preferenceViewModel.update(preference);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerLedColor2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preference.setBayTimeoutColor2(
                        BayLightStatus.valueOf(
                                (String) adapterView.getItemAtPosition(i)
                        )
                );
                preferenceViewModel.update(preference);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void handleTimerInputOpenBay(Preference preference) {
        btnTimer1.setOnClickListener(view -> handleTimePickerDialogOpenBay(preference, "timer1"));

        btnTimer2.setOnClickListener(view -> handleTimePickerDialogOpenBay(preference, "timer2"));
    }

    private void handleTimePickerDialogOpenBay(Preference preference, String timer) {
        NumberPicker hourPicker, minutePicker;
        timePickerDialog = new Dialog(getContext());
        timePickerDialog.setContentView(R.layout.dialog_time_picker_layout);
        timePickerDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timePickerDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        hourPicker = timePickerDialog.findViewById(R.id.number_picker_hour);
        minutePicker = timePickerDialog.findViewById(R.id.number_picker_minute);
        RelativeLayout btnDone = timePickerDialog.findViewById(R.id.tv_done_timeout_dialog);

        hourPicker.setMaxValue(23);
        hourPicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setMinValue(1);

        //set default values to pickers
        long minutes = 0L;
        if (timer.equals("timer1")) {
            minutes = TimeUnit.MILLISECONDS.toMinutes(preference.getBayTimeoutTime1());
        } else if (timer.equals("timer2")) {
            minutes = TimeUnit.MILLISECONDS.toMinutes(preference.getBayTimeoutTime2());
        }
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        hourPicker.setValue(Math.toIntExact(hours));
        minutePicker.setValue(Math.toIntExact(remainingMinutes));

        btnDone.setOnClickListener(textView -> {
            long timerInSeconds = hourPicker.getValue() * 3600000L + minutePicker.getValue() * 60000L;
            if (timer.equals("timer1")) {
                preference.setBayTimeoutTime1(timerInSeconds);
            } else if (timer.equals("timer2")) {
                preference.setBayTimeoutTime2(timerInSeconds);
            }
            preferenceViewModel.update(preference);
            timePickerDialog.dismiss();
        });
        timePickerDialog.show();
    }

    @Override
    public void onMessageReceived2(String message) {

    }
}