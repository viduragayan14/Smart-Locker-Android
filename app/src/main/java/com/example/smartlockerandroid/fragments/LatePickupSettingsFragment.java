package com.example.smartlockerandroid.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.smartlockerandroid.databinding.FragmentLatePickupSettingsBinding;
import com.example.smartlockerandroid.utils.UdpServerThread;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * @author itschathurangaj on 6/29/23
 */
public class LatePickupSettingsFragment extends Fragment implements UdpServerThread.MessageReceived {
    private PreferenceViewModel preferenceViewModel;
    private FragmentLatePickupSettingsBinding binding;
    private SwitchCompat switchTimerStatus;
    private EditText etTimeout, etWarning;
    private Spinner spinnerLedColor;
    private RelativeLayout btnSetTimer;
    private RelativeLayout btnSetTimer2;
    private Dialog timePickerDialog;
    private RelativeLayout btnDone;
    private RelativeLayout btnLatePickupSettingSave;
    private RelativeLayout btnOpenBaySettingSave;
    private RelativeLayout btnColorSettingSave;
    private Spinner spinnerEmpty, spinnerFull, spinnerReloading;

    private EditText etTimeout1, etTimeout2;
    private RelativeLayout btnTimer1, btnTimer2;
    private Spinner spinnerLedColor1, spinnerLedColor2;
    private SwitchCompat switchTimerStatus1, switchTimerStatus2;
    private Dialog timePickerOpenBaySettingsDialog;
//    private KeyboardView customKeyboard;
//    private KeyboardUtil keyboardUtilUserName;
//    private RelativeLayout superParent;
//    private LinearLayout keyboardParent;

    Context context;
    UdpServerThread bh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLatePickupSettingsBinding.inflate(inflater, container, false);
        bh = UdpServerThread.getInstance(context, this);
        bh.setViewModelStoreOwner(context, this);
        View root = binding.getRoot();
        //return inflater.inflate(R.layout.fragment_late_pickup_settings, container, false);
        init();
        return root;
    }

    private void init() {
        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);

        switchTimerStatus = binding.switchPreventLatePickupFragment;
        etTimeout = binding.etTimeoutLatePickupFragment;
        etWarning = binding.etLateWarningLatePickupFragment;
        spinnerLedColor = binding.spinnerLedColorLatePickupFragment;
        btnSetTimer = binding.btnTimeoutLatePickupFragment;
        btnSetTimer2 = binding.btnTimeoutLoadIn;
        btnLatePickupSettingSave = binding.btnSaveLatePickupTimerFragment;

//        customKeyboard = binding.kvKeyboard;
//        keyboardParent = binding.llKeyboardFather;
//        superParent = binding.viewRoot;
//
//        keyboardUtilUserName = new KeyboardUtil(requireActivity(), etWarning, customKeyboard, keyboardParent, true, this);
//        keyboardUtilUserName.forbidSoftInputMethod();
//        keyboardUtilUserName.hideKeyboard();

//        etWarning.setOnTouchListener((view, motionEvent) -> {
//            if (!keyboardUtilUserName.isShow()) {
//                keyboardUtilUserName.showKeyboard();
//            }
//            keyboardUtilUserName.changeFocus(etWarning);
//            return false;
//        });


        //Color Settings
        spinnerEmpty = binding.spinnerWhenEmptyBayColorSettings;
        spinnerFull = binding.spinnerWhenFullBayColorSettings;
        spinnerReloading = binding.spinnerWhenReloadingBayColorSettings;
        btnColorSettingSave = binding.btnSaveBayColorFragment;
        handleComponentsColorSettings();
        handleSaveColorSettings();

        //Open Bay Settings
        btnOpenBaySettingSave = binding.btnSaveOpenBayFragment;
        etTimeout1 = binding.etTimeout1OpenBaySettings;
        btnTimer1 = binding.btnTimeout1OpenBaySettings;
        spinnerLedColor1 = binding.spinnerTimeout1LedColorOpenBaySettings;
        switchTimerStatus1 = binding.switchBeepTimeout1OpenBaySettings;

        etTimeout2 = binding.etTimeout2OpenBaySettings;
        btnTimer2 = binding.btnTimeout2OpenBaySettings;
        spinnerLedColor2 = binding.spinnerTimeout2LedColorOpenBaySettings;
        switchTimerStatus2 = binding.switchBeepTimeout2OpenBaySettings;
        handleSaveOpenBaySettings();
        handleComponentsOpenBaySettings();

        handleComponents();
        handleSave();
    }

    private void handleSaveOpenBaySettings() {
        btnOpenBaySettingSave.setOnClickListener(view -> {
            //requireActivity().finish();
            Toast.makeText(getContext(), "Open bay settings saved successfully!", Toast.LENGTH_LONG).show();
        });
    }

    private void handleComponentsOpenBaySettings() {
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

    private void handleTimerInputOpenBay(Preference preference) {
        btnTimer1.setOnClickListener(view -> handleTimePickerDialogOpenBay(preference, "timer1"));

        btnTimer2.setOnClickListener(view -> handleTimePickerDialogOpenBay(preference, "timer2"));
    }

    private void handleTimePickerDialogOpenBay(Preference preference, String timer) {
        NumberPicker hourPicker, minutePicker;
        timePickerOpenBaySettingsDialog = new Dialog(getContext());
        timePickerOpenBaySettingsDialog.setContentView(R.layout.dialog_time_picker_layout);
        timePickerOpenBaySettingsDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timePickerOpenBaySettingsDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        timePickerOpenBaySettingsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        hourPicker = timePickerOpenBaySettingsDialog.findViewById(R.id.number_picker_hour);
        minutePicker = timePickerOpenBaySettingsDialog.findViewById(R.id.number_picker_minute);
        RelativeLayout btnDone = timePickerOpenBaySettingsDialog.findViewById(R.id.tv_done_timeout_dialog);

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
            timePickerOpenBaySettingsDialog.dismiss();
        });
        timePickerOpenBaySettingsDialog.show();
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

    private void handleSaveColorSettings() {
        btnColorSettingSave.setOnClickListener(view -> {
            //requireActivity().finish();
            Toast.makeText(getContext(), "Bay color settings saved successfully!", Toast.LENGTH_LONG).show();
        });
    }

    private void handleComponentsColorSettings() {
        //set spinner values
        List<String> ledColors = Arrays.asList("OFF", "RED", "GREEN", "YELLOW");
        ArrayAdapter<String> dropdownItems = new ArrayAdapter<>(getContext(), R.layout.list_item_dropdown, ledColors);
        spinnerEmpty.setAdapter(dropdownItems);
        spinnerFull.setAdapter(dropdownItems);
        spinnerReloading.setAdapter(dropdownItems);

        preferenceViewModel.getPreferenceLiveData().observe(getViewLifecycleOwner(), preference -> {
            spinnerEmpty.setSelection(dropdownItems.getPosition(preference.getBayColorWhenEmpty().toString()));
            spinnerFull.setSelection(dropdownItems.getPosition(preference.getBayColorWhenFull().toString()));
            spinnerReloading.setSelection(dropdownItems.getPosition(preference.getBayColorWhenReloading().toString()));

            handleSpinnerEmpty(preference);
            handleSpinnerFull(preference);
            handleSpinnerLoading(preference);
        });
    }

    private void handleSpinnerEmpty(Preference preference) {
        spinnerEmpty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preference.setBayColorWhenEmpty(
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

    private void handleSpinnerFull(Preference preference) {
        spinnerFull.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preference.setBayColorWhenFull(
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

    private void handleSpinnerLoading(Preference preference) {
        spinnerReloading.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preference.setBayColorWhenReloading(
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

    private void handleSave() {
        btnLatePickupSettingSave.setOnClickListener(view -> {
            //requireActivity().finish();
            Toast.makeText(getContext(), "Late pickup settings saved successfully!", Toast.LENGTH_LONG).show();
        });
    }

    private void handleComponents() {
        //set spinner values
        List<String> ledColors = Arrays.asList("OFF", "RED", "GREEN", "YELLOW");
        ArrayAdapter<String> dropdownItems = new ArrayAdapter<>(getContext(), R.layout.list_item_dropdown, ledColors);
        spinnerLedColor.setAdapter(dropdownItems);

        preferenceViewModel.getPreferenceLiveData().observe(getViewLifecycleOwner(), preference -> {
            switchTimerStatus.setChecked(preference.getLatePickupTimerStatus());
            etTimeout.setText(convertTimeoutValueToString(preference.getLatePickupTimerTimeout()));
            etWarning.setText(preference.getLatePickupWarning());
            spinnerLedColor.setSelection(dropdownItems.getPosition(preference.getLatePickupTimerLedColor().toString()));

            handleSwitchInput(preference);
            handleTimeoutWarningInput(preference);
            handleSinnerInput(preference);
            handleTimerInput(preference);
            handleTimerInput2(preference);
        });

    }

    private String convertTimeoutValueToString(Long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        return String.format(Locale.getDefault(), "%02d hr %02d min", hours, remainingMinutes);
    }

    private void handleSwitchInput(Preference preference) {
        switchTimerStatus.setOnCheckedChangeListener((compoundButton, b) -> {

            preference.setLatePickupTimerStatus(b);
            preferenceViewModel.update(preference);

        });

        if (preference.getLatePickupTimerTimeoutLoadInEnable()) {
            binding.switchPersonalize.setChecked(true);
            long minutes = preference.getLatePickupTimerTimeoutLoadIn() / 60000L; // Get the value in minutes
            long seconds = minutes * 60; // Convert minutes to seconds
            binding.etTimeoutLoadIn.setText(seconds + " secs");
        } else {
            binding.switchPersonalize.setChecked(false);
            binding.etTimeoutLoadIn.setText("60 secs");
        }

        binding.switchPersonalize.setOnCheckedChangeListener((compoundButton, b) -> {

            preference.setLatePickupTimerTimeoutLoadInEnable(b);
            if (!b) {
                preference.setLatePickupTimerTimeoutLoadIn(60000L);
                binding.btnTimeoutLoadIn.setClickable(false);
                binding.btnTimeoutLoadIn.setFocusable(false);
                binding.etTimeoutLoadIn.setText("60 secs");
            } else {
                long minutes = preference.getLatePickupTimerTimeoutLoadIn() / 60000L;
                long seconds = minutes * 60; // Convert minutes to seconds
                binding.etTimeoutLoadIn.setText(seconds + " secs");
                binding.btnTimeoutLoadIn.setClickable(true);
                binding.btnTimeoutLoadIn.setFocusable(true);
            }
            preferenceViewModel.update(preference);
        });
    }

    private void handleTimeoutWarningInput(Preference preference) {
        etWarning.setOnEditorActionListener(((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                preference.setLatePickupWarning(textView.getText().toString());
                preferenceViewModel.update(preference);

                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                return true;
            }
            return false;
        }));
    }

    private void handleSinnerInput(Preference preference) {
        spinnerLedColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preference.setLatePickupTimerLedColor(
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

    private void handleTimerInput(Preference preference) {
        timePickerDialog = new Dialog(getContext());

        btnSetTimer.setOnClickListener(view -> {
            NumberPicker hourPicker, minutePicker;

            timePickerDialog.setContentView(R.layout.dialog_time_picker_layout);
            timePickerDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //timePickerDialog.setCancelable(false);
            timePickerDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            hourPicker = timePickerDialog.findViewById(R.id.number_picker_hour);
            minutePicker = timePickerDialog.findViewById(R.id.number_picker_minute);
            btnDone = timePickerDialog.findViewById(R.id.tv_done_timeout_dialog);

            hourPicker.setMaxValue(23);
            hourPicker.setMinValue(0);
            minutePicker.setMaxValue(59);
            minutePicker.setMinValue(1);

            //set default values to pickers
            long minutes = TimeUnit.MILLISECONDS.toMinutes(preference.getLatePickupTimerTimeout());
            long hours = minutes / 60;
            long remainingMinutes = minutes % 60;
            hourPicker.setValue(Math.toIntExact(hours));
            minutePicker.setValue(Math.toIntExact(remainingMinutes));

            btnDone.setOnClickListener(textView -> {
                long timerInSeconds = hourPicker.getValue() * 3600000L + minutePicker.getValue() * 60000L;
                preference.setLatePickupTimerTimeout(timerInSeconds);
                preferenceViewModel.update(preference);
                timePickerDialog.dismiss();
            });

            timePickerDialog.show();
        });
    }

    private void handleTimerInput2(Preference preference) {
        timePickerDialog = new Dialog(getContext());

        btnSetTimer2.setOnClickListener(view -> {
            NumberPicker minutePicker;

            timePickerDialog.setContentView(R.layout.dialog_time_picker_layout2);
            timePickerDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            timePickerDialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Get the minute picker from the layout
            minutePicker = timePickerDialog.findViewById(R.id.number_picker_minute);
            // Hide the hour picker (assuming its id is 'number_picker_hour')
            btnDone = timePickerDialog.findViewById(R.id.tv_done_timeout_dialog);


            minutePicker.setMinValue(1); // Set minimum value to 1
            minutePicker.setMaxValue(5); // Set maximum value to 5

            String[] displayedValues = new String[5];
            for (int i = 0; i < displayedValues.length; i++) {
                displayedValues[i] = String.valueOf((i + 1) * 60);
            }
            minutePicker.setDisplayedValues(displayedValues);

            Log.e("=====",""+preference.getLatePickupTimerTimeout());
            long minutes = TimeUnit.MILLISECONDS.toSeconds(preference.getLatePickupTimerTimeout()) / 60;
            Log.e("-----",""+minutes);
            minutePicker.setValue((int) minutes);

            btnDone.setOnClickListener(textView -> {
                // Convert minutes to milliseconds
                long timerInSeconds = minutePicker.getValue() * 60000L;
                binding.etTimeoutLoadIn.setText(minutePicker.getValue() + " secs");
                preference.setLatePickupTimerTimeoutLoadIn(timerInSeconds);
                preferenceViewModel.update(preference);
                timePickerDialog.dismiss();
            });

            timePickerDialog.show();
        });
    }

    @Override
    public void onMessageReceived2(String message) {

    }

//    @Override
//    public void onConfirmPressed() {
//        keyboardUtilUserName.hideKeyboard();
//    }
//
//    @Override
//    public void onCancelPressed() {
//        keyboardUtilUserName.hideKeyboard();
//    }
}