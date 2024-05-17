package com.example.smartlockerandroid;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartlockerandroid.data.enums.CourierStatus;
import com.example.smartlockerandroid.data.enums.KeyboardInputMethod;
import com.example.smartlockerandroid.data.model.Courier;
import com.example.smartlockerandroid.data.viewmodel.CourierViewModel;
import com.example.smartlockerandroid.databinding.FragmentCourierSettingsBinding;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.ImageSelectedCallback;
import com.example.smartlockerandroid.utils.UdpServerThread;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CourierSettingsActivity extends AppCompatActivity implements ImageSelectedCallback, UdpServerThread.MessageReceived {
    private FragmentCourierSettingsBinding binding;
    private CourierViewModel courierViewModel;
    private EditText etCourierName, edHeading;
    private ImageView imageViewCourierLogo;
    private TextView btnDelete, textSave;
    private RelativeLayout btnAdd, btnLogo;
    private SwitchCompat switchKeyboardType;
    private String flow;
    private Long currentCourierId;
    private String courierName;
    private boolean isKeyboardAlphaNumeric;
    private Courier courierToUpdate;
    private byte[] courierLogoData;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private Dialog dialog;
    private RelativeLayout btnDialogCancel, btnDialogYes;

//    private KeyboardView customKeyboard;
//    private KeyboardUtil keyboardUtilUserName;
//    private RelativeLayout superParent;
//    private LinearLayout keyboardParent;
    int size = 0;
    UdpServerThread bh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        binding = FragmentCourierSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }


    private void init() {
        bh = UdpServerThread.getInstance(CourierSettingsActivity.this, this);
        bh.setViewModelStoreOwner(CourierSettingsActivity.this,this);
        courierViewModel = new ViewModelProvider(this).get(CourierViewModel.class);
        courierViewModel.getCouriers().observe(CourierSettingsActivity.this, couriers -> {
            size = couriers.size();
        });
        etCourierName = binding.etCourierNameCourierSettings;
        edHeading = binding.etHeading;

        binding.rootView.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Get the currently focused view, if any
                View focusedView = getCurrentFocus();

                // If the focused view is an EditText, hide the keyboard
                if (focusedView instanceof EditText) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }
            }
            return false;
        });
        binding.rootView2.setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Get the currently focused view, if any
                View focusedView = getCurrentFocus();

                // If the focused view is an EditText, hide the keyboard
                if (focusedView instanceof EditText) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }
            }
            return false;
        });

        imageViewCourierLogo = binding.imageViewLogoCourierSettings;
        switchKeyboardType = binding.switchKeyboardTypeCourierSettings;
        btnLogo = binding.btnSelectLogoCourierSettings;
        btnAdd = binding.btnAddCourierSettings;
        textSave = binding.textSave;
        btnDelete = binding.btnDeleteCourierSettings;

        binding.back.setOnClickListener(view -> {
            super.onBackPressed();
        });


//        customKeyboard = findViewById(R.id.kv_keyboard);
//        keyboardParent = findViewById(R.id.ll_keyboard_father);
//        superParent = findViewById(R.id.view_root);

        btnDelete.setVisibility(View.GONE);

//        keyboardUtilUserName = new KeyboardUtil(this, etCourierName, customKeyboard, keyboardParent, true, this);
//        keyboardUtilUserName.forbidSoftInputMethod();
//        keyboardUtilUserName.showKeyboard();

//        etCourierName.setOnTouchListener((view, motionEvent) -> {
//            if (!keyboardUtilUserName.isShow()) {
//                keyboardUtilUserName.showKeyboard();
//            }
//            keyboardUtilUserName.changeFocus(etCourierName);
//            return false;
//        });
//
//
//        edHeading.setOnTouchListener((view, motionEvent) -> {
//            if (!keyboardUtilUserName.isShow()) {
//                keyboardUtilUserName.showKeyboard();
//            }
//            keyboardUtilUserName.changeFocus(edHeading);
//            return false;
//        });

        flow = getIntent().getStringExtra("flow");
        if (flow.equals("UPDATE_COURIER")) {
            btnDelete.setVisibility(View.VISIBLE);
            textSave.setText("UPDATE");
            currentCourierId = getIntent().getLongExtra("courierId", -1);
            getAndSetCourierData();
        }
        handleSwitchInput();
        handleDialog();
        handleBtnAddLogo();
        handleAddEditCourier();

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        validateSelectedImage(uri, this);
                    }
                });
    }


    private void handleAddEditCourier() {
        btnAdd.setOnClickListener(view -> {
            Courier courier = new Courier((long) size + 1, "callinorder", "Online Order", CourierStatus.ACTIVE, KeyboardInputMethod.NUMERIC, CourierSettingsActivity.this, R.drawable.online_order, size);
            courier.setName(etCourierName.getText().toString());
            courier.setLogoData(courierLogoData);
            courier.setHeading(edHeading.getText().toString());

            if (isKeyboardAlphaNumeric) {
                courier.setKeyboardInputMethod(KeyboardInputMethod.ALPHANUMERIC);
            } else {
                courier.setKeyboardInputMethod(KeyboardInputMethod.NUMERIC);
            }

            if (flow.equals("UPDATE_COURIER")) {
                courier.setStatus(courierToUpdate.getStatus());
                courier.setCourierId(currentCourierId);
                courier.setPosition(courierToUpdate.getPosition());
                courierViewModel.update(courier);
            }
            if (flow.equals("CREATE_COURIER")) {
                courier.setStatus(CourierStatus.ACTIVE);
                courierViewModel.insert(courier);
            }
            //return to parent fragment
            super.onBackPressed();
        });
    }

    private void getAndSetCourierData() {
        courierToUpdate = courierViewModel.findCourierById(currentCourierId);
        if (courierToUpdate != null) {
            courierName = courierToUpdate.getName();
            isKeyboardAlphaNumeric = courierToUpdate.getKeyboardInputMethod().getValue();
            courierLogoData = courierToUpdate.getLogoData();

            etCourierName.setText(courierName);
            edHeading.setText(courierToUpdate.getHeading().toString());
            switchKeyboardType.setChecked(isKeyboardAlphaNumeric);
            if (courierToUpdate.getLogoData() != null) {
                imageViewCourierLogo.setImageBitmap(BitmapFactory.decodeByteArray(courierToUpdate.getLogoData(), 0, courierToUpdate.getLogoData().length));
            }else{
                imageViewCourierLogo.setImageDrawable(getResources().getDrawable(R.drawable.default_courier_logo));
            }
        }
    }

    private void handleSwitchInput() {

        switchKeyboardType.setOnCheckedChangeListener((compoundButton, b) -> isKeyboardAlphaNumeric = b);
    }

    private void handleBtnAddLogo() {
        btnLogo.setOnClickListener(view -> {
            imagePickerLauncher.launch("image/*");
        });
    }


    private void validateSelectedImage(Uri selectedImageUri, ImageSelectedCallback callback) {
        // Check file format
        ContentResolver contentResolver = CourierSettingsActivity.this.getContentResolver();
        String mimeType = contentResolver.getType(selectedImageUri);
        if (mimeType != null && !mimeType.equals("image/png") && !mimeType.equals("image/jpeg")) {

            Toast.makeText(CourierSettingsActivity.this, "Invalid file format!", Toast.LENGTH_LONG).show();
            return;
        }

        //Check image resolution
        try {
            InputStream inputStream = contentResolver.openInputStream(selectedImageUri);

            // Decode the image file to get its dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);

            // Check if the image resolution exceeds the maximum allowed
            if (options.outWidth > 200 || options.outHeight > 200) {
                Toast.makeText(CourierSettingsActivity.this, "Invalid image resolution!", Toast.LENGTH_LONG).show();
                return;
            }

            // Invoke the callback with the image URI
            if (callback != null) {
                callback.onImageSelected(bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteCourier() {
        courierViewModel.deleteCourier(currentCourierId);
        //return to parent fragment
        super.onBackPressed();
    }

    private void handleDialog() {
        dialog = new Dialog(CourierSettingsActivity.this);
        btnDelete.setOnClickListener(view -> {
            dialog.setContentView(R.layout.dialog_delete_app_layout);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            btnDialogCancel = dialog.findViewById(R.id.btn_dialog_cancel);
            btnDialogYes = dialog.findViewById(R.id.btn_dialog_yes);

            btnDialogCancel.setOnClickListener(viewCancel -> {
                dialog.dismiss();
            });

            btnDialogYes.setOnClickListener(viewYes -> {
                dialog.dismiss();
                handleDeleteCourier();
            });
            dialog.show();
        });
    }

    @Override
    public void onImageSelected(Bitmap bitmap) {
        //set image resource
        imageViewCourierLogo.setImageBitmap(bitmap);

        //save image in the db
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        courierLogoData = stream.toByteArray();
    }

    @Override
    public void onMessageReceived2(String message) {

    }

//    @Override
//    public void onConfirmPressed() {
//        if (etCourierName.hasFocus()) {
//            edHeading.requestFocus();
//            keyboardUtilUserName.changeFocus(edHeading);
//        } else if (edHeading.hasFocus()) {
//            keyboardUtilUserName.hideKeyboard();
//        }
//    }
//
//    @Override
//    public void onCancelPressed() {
//        keyboardUtilUserName.hideKeyboard();
//    }
}