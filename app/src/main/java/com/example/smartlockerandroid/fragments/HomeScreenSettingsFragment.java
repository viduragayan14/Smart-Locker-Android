package com.example.smartlockerandroid.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.smartlockerandroid.PreferencesActivity;
import com.example.smartlockerandroid.R;
import com.example.smartlockerandroid.data.model.Preference;
import com.example.smartlockerandroid.data.viewmodel.PreferenceViewModel;
import com.example.smartlockerandroid.databinding.FragmentHomeScreenSettingsBinding;
import com.example.smartlockerandroid.utils.BackLoadingHelper;
import com.example.smartlockerandroid.utils.ImageSelectedCallback;
import com.example.smartlockerandroid.utils.UdpServerThread;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HomeScreenSettingsFragment extends Fragment implements ImageSelectedCallback, UdpServerThread.MessageReceived {
    ActivityResultLauncher<String> imagePickerLauncher;
    private PreferenceViewModel preferenceViewModel;
    private FragmentHomeScreenSettingsBinding binding;
    private EditText etWelcomeTitle, etWelcomeMsg;
    private RelativeLayout btnSelectImage, btnSave;
    private ImageView imageViewLogo;

    //Time Settings
    private SwitchCompat switchTimeAuto;
    private EditText etTimeAndDate;
    private RelativeLayout btnSetTimeDate;
    private RelativeLayout btnSaveTimeSettings;

//    private KeyboardView customKeyboard;
//    private KeyboardUtil keyboardUtilUserName;
//    private RelativeLayout superParent;
//    private LinearLayout keyboardParent;

    Preference p;

    PreferencesActivity preferencesActivity;
    UdpServerThread bh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        preferencesActivity = (PreferencesActivity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeScreenSettingsBinding.inflate(inflater, container, false);
        bh = UdpServerThread.getInstance(preferencesActivity,  this);
        bh.setViewModelStoreOwner(preferencesActivity,this);
        View root = binding.getRoot();
        init();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        validateSelectedImage(uri, this);
                    }
                });
    }

    private void init() {
        preferenceViewModel = new ViewModelProvider(this).get(PreferenceViewModel.class);

        etWelcomeTitle = binding.etWelcomeTitleHomeScreenSettings;
        etWelcomeMsg = binding.etWelcomeMsgHomeScreenSettings;
        imageViewLogo = binding.imageViewLogoHomeScreenSettings;
        btnSelectImage = binding.btnSelectLogoHomeScreenSettings;
        btnSave = binding.btnSaveHomeScreenSettingsFragment;

        //Time Settings
        switchTimeAuto = binding.switchEnableTimeAndDateSettings;
        etTimeAndDate = binding.etManualTimeAndDateSettings;
        btnSetTimeDate = binding.btnSetManualTimeAndDateSettings;
        btnSaveTimeSettings = binding.btnSaveTimeAndDateFragment;

        setTouchListenerRecursive(binding.getRoot());

//        customKeyboard = binding.kvKeyboard;
//        keyboardParent = binding.llKeyboardFather;
//        superParent = binding.viewRoot;

        binding.exit.setOnClickListener(view -> {
            preferencesActivity.finishAffinity();
            preferencesActivity.finish();
            System.exit(0);

//            preferencesActivity.finishAndRemoveTask();
        });

//        keyboardUtilUserName = new KeyboardUtil(requireActivity(), etWelcomeTitle, customKeyboard, keyboardParent, true, this);
//        keyboardUtilUserName.forbidSoftInputMethod();
//        keyboardUtilUserName.hideKeyboard();

//        etWelcomeTitle.setOnTouchListener((view, motionEvent) -> {
//            if (!keyboardUtilUserName.isShow()) {
//                keyboardUtilUserName.showKeyboard();
//            }
//            keyboardUtilUserName.changeFocus(etWelcomeTitle);
//            return false;
//        });
//
//        etWelcomeMsg.setOnTouchListener((view, motionEvent) -> {
//            if (!keyboardUtilUserName.isShow()) {
//                keyboardUtilUserName.showKeyboard();
//            }
//            keyboardUtilUserName.changeFocus(etWelcomeMsg);
//            return false;
//        });

        btnSaveTimeSettings.setOnClickListener(view -> Toast.makeText(requireActivity(), "Functionality Remains", Toast.LENGTH_SHORT).show());

        handleComponents();
        handleSave();
    }

    private void handleSave() {
        btnSave.setOnClickListener(view -> {
            //requireActivity().finish();
            Toast.makeText(getContext(), "Home screen settings saved successfully!", Toast.LENGTH_LONG).show();
        });
    }

    private void handleComponents() {
        preferenceViewModel.getPreferenceLiveData().observe(getViewLifecycleOwner(), preference -> {
            this.p = preference;
            etWelcomeTitle.setText(preference.getWelcomeText());
            etWelcomeMsg.setText(preference.getWelcomeMessage());
            if (preference.getHomeLogoData() != null) {
                imageViewLogo.setImageBitmap(BitmapFactory.decodeByteArray(preference.getHomeLogoData(), 0, preference.getHomeLogoData().length));
            }else{
                imageViewLogo.setImageDrawable(getResources().getDrawable(R.drawable.default_courier_logo));
            }
            handleWelcomeTitle(preference);
            handleWelcomeMsg(preference);
            handleSelectImage();
        });
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

    private void handleWelcomeMsg(Preference preference) {
        etWelcomeTitle.setOnEditorActionListener(((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                preference.setWelcomeText(textView.getText().toString());
                preferenceViewModel.update(preference);

                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                return true;
            }
            return false;
        }));
    }

    private void handleWelcomeTitle(Preference preference) {
        etWelcomeMsg.setOnEditorActionListener(((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                preference.setWelcomeMessage(textView.getText().toString());
                preferenceViewModel.update(preference);

                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                return true;
            }
            return false;
        }));
    }

    private void handleSelectImage() {
        btnSelectImage.setOnClickListener(view -> {
            imagePickerLauncher.launch("image/*");
        });
    }

    private void validateSelectedImage(Uri selectedImageUri, ImageSelectedCallback callback) {
        // Check file format
        ContentResolver contentResolver = requireActivity().getContentResolver();
        String mimeType = contentResolver.getType(selectedImageUri);
        if (mimeType != null && !mimeType.equals("image/png") && !mimeType.equals("image/jpeg")) {
            //&& !mimeType.equals("image/jpeg")
            Toast.makeText(getContext(), "Invalid file format!", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getContext(), "Invalid image resolution!", Toast.LENGTH_LONG).show();
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

    @Override
    public void onImageSelected(Bitmap bitmap) {
        //set image resource
        imageViewLogo.setImageBitmap(bitmap);

        //save image in the db
        Preference preference = preferenceViewModel.getPreference();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        preference.setHomeLogoData(imageBytes);
        preferenceViewModel.update(preference);
    }

    @Override
    public void onMessageReceived2(String message) {

    }

//    @Override
//    public void onConfirmPressed() {
//        if (etWelcomeMsg.hasFocus()) {
//            p.setWelcomeMessage(etWelcomeMsg.getText().toString());
//            preferenceViewModel.update(p);
//        }
//        if (etWelcomeTitle.hasFocus()) {
//            p.setWelcomeText(etWelcomeTitle.getText().toString());
//            preferenceViewModel.update(p);
//        }
//
//        keyboardUtilUserName.hideKeyboard();
//    }
//
//    @Override
//    public void onCancelPressed() {
//        keyboardUtilUserName.hideKeyboard();
//    }
}