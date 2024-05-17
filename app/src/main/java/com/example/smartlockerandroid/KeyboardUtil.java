package com.example.smartlockerandroid;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

/**
 * 密码输入键盘
 */
public class KeyboardUtil {

    private static final int KEYCODE_ABC = 9001;
    private static final int KEYCODE_SYMBOL = 9002;
    private static final int KEYCODE_NUM = 9003;
    private static final int KEYCODE_CONFIRM = 786;
    private static final int KEYCODE_CANCEL = 785;
    CustomKeys customKeys;
    private LinearLayout mLinearLayout;
    private Context mContext;
    private Activity mActivity;
    private KeyboardView mKeyboardView;
    private EditText mEdit;
    /**
     * 英文键盘
     */
    private Keyboard english_keyboard;
    /**
     * 数字键盘
     */
    private Keyboard number_keyboard;

    /**
     * 软键盘切换判断
     */
//    private boolean isChange = true;
    /**
     * 符号键盘
     */
    private Keyboard symbol_keyboard;
    /**
     * 字母大小写切换
     */
    private boolean isCapital = true;
    /**
     * 默认键盘 true english   false number
     */
    private boolean isNumber;
    private Editable editable;
    private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
        @Override
        public void swipeUp() {
        }

        @Override
        public void swipeRight() {
        }

        @Override
        public void swipeLeft() {
        }

        @Override
        public void swipeDown() {
        }

        @Override
        public void onText(CharSequence text) {
            int start = mEdit.getSelectionStart();
            editable.insert(start, text);
        }

        @Override
        public void onRelease(int primaryCode) {
        }

        @Override
        public void onPress(int primaryCode) {
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {

            int start = mEdit.getSelectionStart();

            switch (primaryCode) {
                // 英文大小写切换-1
                case Keyboard.KEYCODE_SHIFT:
                    shiftEnglish();
                    mKeyboardView.setKeyboard(english_keyboard);
                    break;
                // 回退-5
                case Keyboard.KEYCODE_DELETE:
                    if (editable != null && editable.length() > 0 && start > 0) {
                        editable.delete(start - 1, start);
                    }
                    break;
                // 英文键盘与数字键盘切换-123123
                case KeyboardUtil.KEYCODE_ABC:
                    shiftEnglish();
                    mKeyboardView.setKeyboard(english_keyboard);
                    break;
                case KeyboardUtil.KEYCODE_NUM:
                    mKeyboardView.setKeyboard(number_keyboard);
                    break;
                case KeyboardUtil.KEYCODE_SYMBOL:
                    mKeyboardView.setKeyboard(symbol_keyboard);
                    break;
                case KeyboardUtil.KEYCODE_CONFIRM:
                    customKeys.onConfirmPressed();

                    break;
                case KeyboardUtil.KEYCODE_CANCEL:
                    customKeys.onCancelPressed();
                    break;
                default:
                    editable.insert(start, Character.toString((char) primaryCode));
                    break;

            }

        }
    };

    public KeyboardUtil(Activity activity, EditText edit, KeyboardView keyboard_view, LinearLayout linearLayout, boolean isNumber, CustomKeys customKeys) {
        mLinearLayout = linearLayout;
        mActivity = activity;
        mContext = activity;
        mEdit = edit;
        this.isNumber = isNumber;
        this.customKeys = customKeys;
        english_keyboard = new Keyboard(mContext, R.xml.symbol_abc);
        number_keyboard = new Keyboard(mContext, R.xml.symbol_num);
        symbol_keyboard = new Keyboard(mContext, R.xml.symbol_symbol);
        mKeyboardView = keyboard_view;


        if (isNumber) {
            List<Keyboard.Key> keyList = english_keyboard.getKeys();
            for (Keyboard.Key key : keyList) {
                if (key.label != null && isKey(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase(Locale.ROOT);
                    key.codes[0] = key.codes[0] - 32;
                }
            }
            mKeyboardView.setKeyboard(english_keyboard);
        } else {
            mKeyboardView.setKeyboard(number_keyboard);
        }
        editable = mEdit.getText();
        mKeyboardView.setEnabled(true);
        mKeyboardView.setPreviewEnabled(false);
        mKeyboardView.setOnKeyboardActionListener(listener);
    }

    /**
     * 从控件所在位置移动到控件的底部
     *
     * @return
     */
    public static TranslateAnimation moveToViewBottom() {
        TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        mHiddenAction.setDuration(300);
        return mHiddenAction;
    }

    /**
     * 英文键盘大小写切换
     */
    public void shiftEnglish() {
        List<Keyboard.Key> keyList = english_keyboard.getKeys();
        for (Keyboard.Key key : keyList) {
            if (key.label != null && isKey(key.label.toString())) {
                if (isCapital) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                } else {

                    key.label = key.label.toString().toUpperCase(Locale.ROOT);
                    key.codes[0] = key.codes[0] - 32;

                }
            }
        }
        isCapital = !isCapital;
    }

    public void shiftCaps() {

        List<Keyboard.Key> keyList = english_keyboard.getKeys();
        for (Keyboard.Key key : keyList) {
            if (key.label != null && isKey(key.label.toString())) {
                if (isCapital) {
                    Toast.makeText(mContext, "Called", Toast.LENGTH_SHORT).show();
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                } else {
                    Toast.makeText(mContext, "Not Called", Toast.LENGTH_SHORT).show();
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
            isCapital = !isCapital;
        }
//        isCapital = !isCapital;
    }

    /**
     * 判断此key是否正确，且存在
     *
     * @param key
     * @return
     */
    private boolean isKey(String key) {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        if (lowercase.indexOf(key.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }

    /**
     * 软键盘展示状态
     */
    public boolean isShow() {
        return mLinearLayout.getVisibility() == View.VISIBLE;
    }

    /**
     * 软键盘展示
     */
    public void showKeyboard() {
        if (!isShow()) {
            mLinearLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 软键盘隐藏
     */
    public void hideKeyboard() {
        if (isShow()) {
            mLinearLayout.setVisibility(View.GONE);
            mLinearLayout.setAnimation(moveToViewBottom());
        }
    }

    public void changeFocus(EditText newEditText) {
        mEdit = newEditText;
        editable = mEdit.getText();
        forbidSoftInputMethod();
    }

    /**
     * 禁掉系统软键盘
     */
    public void forbidSoftInputMethod() {
        mActivity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        String methodName = null;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }
        if (methodName == null) {
            mEdit.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName,
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(mEdit, false);
            } catch (NoSuchMethodException e) {
                mEdit.setInputType(InputType.TYPE_NULL);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public interface CustomKeys {
        void onConfirmPressed();

        void onCancelPressed();
    }
}
