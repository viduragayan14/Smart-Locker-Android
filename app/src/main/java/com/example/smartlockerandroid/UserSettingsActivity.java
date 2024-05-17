package com.example.smartlockerandroid;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartlockerandroid.data.enums.UserRole;
import com.example.smartlockerandroid.data.model.User;
import com.example.smartlockerandroid.data.viewmodel.UserViewModel;
import com.example.smartlockerandroid.databinding.FragmentUserSettingsBinding;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.UdpServerThread;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class UserSettingsActivity extends AppCompatActivity implements UdpServerThread.MessageReceived {
    RelativeLayout btnAdd;
    TextView txtAdd, btnDelete;
    private UserViewModel userViewModel;
    private FragmentUserSettingsBinding binding;
    private Spinner spinnerUserRole;
    private EditText etUsername, etPassword, etBarcode;
    private SwitchCompat switchUserStatus;
    private RelativeLayout btnDialogCancel, btnDialogYes;
    private String selectedUserRole = "STAFF";
    private String username, password, barcode;
    private Long currentUserId;
    private boolean userStatus = true;
    private String flow = "";
    private String currentUsername = "";
    private Dialog dialog;
    UdpServerThread ut;
    //Custom Keys
//    private KeyboardView customKeyboard;
//    private KeyboardUtil keyboardUtilUserName;
//    private RelativeLayout superParent;
//    private LinearLayout keyboardParent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        binding = FragmentUserSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ut = UdpServerThread.getInstance(UserSettingsActivity.this, this);
        ut.setViewModelStoreOwner(UserSettingsActivity.this,this);
        init();

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void init() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

//        customKeyboard = findViewById(R.id.kv_keyboard);
//        keyboardParent = findViewById(R.id.ll_keyboard_father);
//        superParent = findViewById(R.id.view_root);

        etUsername = binding.etUsernameAddUserFragment;
        etPassword = binding.etUserPasswordAddUserFragment;
        etBarcode = binding.etUserBarcodeAddUserFragment;
        binding.imageViewCloseOrderManagement.setOnClickListener(view -> UserSettingsActivity.super.onBackPressed());

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

//        keyboardUtilUserName = new KeyboardUtil(this, etUsername, customKeyboard, keyboardParent, true, this);
//        keyboardUtilUserName.forbidSoftInputMethod();
//        keyboardUtilUserName.showKeyboard();

//        etUsername.setOnTouchListener((view, motionEvent) -> {
//            if (!keyboardUtilUserName.isShow()) {
//                keyboardUtilUserName.showKeyboard();
//            }
//            keyboardUtilUserName.changeFocus(etUsername);
//            return false;
//        });
//
//
//        etPassword.setOnTouchListener((view, motionEvent) -> {
//            if (!keyboardUtilUserName.isShow()) {
//                keyboardUtilUserName.showKeyboard();
//            }
//            keyboardUtilUserName.changeFocus(etPassword);
//            return false;
//        });
//
//
//        etBarcode.setOnTouchListener((view, motionEvent) -> {
//            if (!keyboardUtilUserName.isShow()) {
//                keyboardUtilUserName.showKeyboard();
//            }
//            keyboardUtilUserName.changeFocus(etBarcode);
//            return false;
//        });

        spinnerUserRole = binding.spinnerUserRoleAddUserFragment;


        switchUserStatus = binding.switchUserStatusAddUserFragment;
        btnAdd = binding.btnAddAddUserFragment;
        txtAdd = binding.textSave;
        btnDelete = binding.btnDeleteAddUserFragment;

        flow = getIntent().getStringExtra("flow");
        if (flow.equals("UPDATE_USER")) {
            btnDelete.setVisibility(View.VISIBLE);
            txtAdd.setText("UPDATE");
            currentUsername = getIntent().getStringExtra("username");
            getUser();
            handleDialog();
        }

        handleAddUser();
    }

    private void handleAddUser() {
        handleSpinnerInput();
        handleSwitchInput();

        btnAdd.setOnClickListener(view -> {
            username = etUsername.getText().toString();
            password = etPassword.getText().toString();
            barcode = etBarcode.getText().toString();

            validateUserData();
        });
    }

    private void getUser() {
        try {
            User user = userViewModel.findUserByUsername(currentUsername);

            if (user != null) {
                currentUserId = user.getUserId();
                username = user.getUsername();
                selectedUserRole = user.getUserRole().toString();
                password = user.getPassword();
                barcode = user.getBarcode();
                userStatus = user.getStatus();
                setUserData();
            } else {
                Toast.makeText(UserSettingsActivity.this, "Something went wrong, Try again!", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
            }
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(UserSettingsActivity.this, "Something went wrong, Try again!", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
            e.printStackTrace();
        }
    }

    private void setUserData() {
        etUsername.setText(username);
        etPassword.setText(password);
        etBarcode.setText(barcode);
        switchUserStatus.setChecked(userStatus);
        handleSpinnerInput();
    }

    private void handleSwitchInput() {
        switchUserStatus.setOnCheckedChangeListener((compoundButton, b) -> userStatus = b);
    }

    private void handleSpinnerInput() {
        List<String> mList = Arrays.asList("STAFF", "MANAGER", "ADMIN");
        ArrayAdapter<String> dropdownItems = new ArrayAdapter<>(UserSettingsActivity.this, R.layout.list_item_dropdown, mList);
        spinnerUserRole.setAdapter(dropdownItems);

        //set current user role
        if (flow.equals("UPDATE_USER")) {
            int defaultSelectionIndex = dropdownItems.getPosition(selectedUserRole);
            spinnerUserRole.setSelection(defaultSelectionIndex);
        }

        spinnerUserRole.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUserRole = (String) adapterView.getItemAtPosition(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void validateUserData() {
        if (!isValidUsername()) {
            //etUsername.setError("This field is required");
            return;
        }
        if (!isValidPassword()) {
            //etPassword.setError("This field is required");
            return;
        }
        if (!isValidBarcode()) {
            //etPassword.setError("This field is required");
            return;
        }
        if (selectedUserRole.equals(" ")) {
            Toast.makeText(UserSettingsActivity.this, "Select a Role!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isUsernameAvailable()) {
            Toast.makeText(UserSettingsActivity.this, "Username is already in use! Try with a different username!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!isBarcodeAvailable()) {
            Toast.makeText(UserSettingsActivity.this, "Barcode is already in use! Try with a different Barcode!", Toast.LENGTH_LONG).show();
            return;
        }
        handleCreateUpdateUser();
    }

    private boolean isUsernameAvailable() {
        try {
            User user = userViewModel.findUserByUsername(username);
            if (user == null) {
                //barcode available
                return true;
            } else if (flow.equals("CREATE_USER")) {
                //barcode not available
                return false;
            } else if (flow.equals("UPDATE_USER")) {
                return Objects.equals(user.getUserId(), currentUserId);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isBarcodeAvailable() {
        try {
            User user = userViewModel.findUserByBarcode(barcode);
            if (user == null) {
                //barcode available
                return true;
            } else if (flow.equals("CREATE_USER")) {
                //barcode not available
                return false;
            } else if (flow.equals("UPDATE_USER")) {
                return Objects.equals(user.getUserId(), currentUserId);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValidUsername() {
        // Username should not be null or empty
        if (username == null || username.isEmpty()) {
            Toast.makeText(UserSettingsActivity.this, "Username should not be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
        // Username should contain only alphanumeric characters and underscores
        if (!username.matches("^\\w+$")) {
            Toast.makeText(UserSettingsActivity.this, "Username should contain only alphanumeric characters and underscores!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isValidPassword() {
        // Username should not be null or empty
        if (password == null || password.isEmpty()) {
            Toast.makeText(UserSettingsActivity.this, "Password should not be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
        // Username should contain only alphanumeric characters and underscores
        if (!password.matches("^\\w+$")) {
            Toast.makeText(UserSettingsActivity.this, "Password should contain only alphanumeric characters and underscores!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isValidBarcode() {
        // Username should not be null or empty
        if (barcode == null || barcode.isEmpty()) {
            Toast.makeText(UserSettingsActivity.this, "Barcode should not be empty!", Toast.LENGTH_LONG).show();
            return false;
        }
        // Username should contain only alphanumeric characters and underscores
        if (!barcode.matches("^\\w+$")) {
            Toast.makeText(UserSettingsActivity.this, "Barcode should contain only alphanumeric characters and underscores!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void handleCreateUpdateUser() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        String currentDate = sdf.format(currentTime);
        User newUser = new User(
                username,
                username,
                UserRole.valueOf(selectedUserRole),
                password,
                barcode,
                userStatus, currentDate
        );

        if (flow.equals("UPDATE_USER")) {
            newUser.setUserId(currentUserId);
            userViewModel.update(newUser);
        }
        if (flow.equals("CREATE_USER")) {
            userViewModel.insert(newUser);
        }
        //return to parent fragment
        super.onBackPressed();
    }

    private void handleDialog() {
        dialog = new Dialog(UserSettingsActivity.this);
        btnDelete.setOnClickListener(view -> {
            dialog.setContentView(R.layout.dialog_delete_record_layout);
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
                handleDeleteUser();
            });
            dialog.show();
        });
    }

    private void handleDeleteUser() {
        userViewModel.deleteUser(currentUserId);
        //return to parent fragment
        super.onBackPressed();
    }

    private void handleUsernameEdittext() {
        etUsername.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                spinnerUserRole.performClick();
                spinnerUserRole.setSelection(0);
                InputMethodManager imm = (InputMethodManager) UserSettingsActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);

                return true;
            }
            return false;
        });
    }

    @Override
    public void onMessageReceived2(String message) {

    }


//    @Override
//    public void onConfirmPressed() {
//        if (etUsername.hasFocus()) {
//            etPassword.requestFocus();
//            keyboardUtilUserName.changeFocus(etPassword);
//        } else if (etPassword.hasFocus()) {
//            etBarcode.requestFocus();
//            keyboardUtilUserName.changeFocus(etBarcode);
//        } else if (etBarcode.hasFocus()) {
//            keyboardUtilUserName.hideKeyboard();
//        }
//
//    }

//    @Override
//    public void onCancelPressed() {
//        keyboardUtilUserName.hideKeyboard();
//    }
}