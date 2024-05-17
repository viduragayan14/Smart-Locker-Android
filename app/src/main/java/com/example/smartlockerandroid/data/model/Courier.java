package com.example.smartlockerandroid.data.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.smartlockerandroid.data.enums.CourierStatus;
import com.example.smartlockerandroid.data.enums.KeyboardInputMethod;
import com.example.smartlockerandroid.data.enums.converter.CourierStatusConverter;
import com.example.smartlockerandroid.data.enums.converter.KeyboardInputMethodConverter;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

@Entity(tableName = "courier_table")
@TypeConverters({CourierStatusConverter.class, KeyboardInputMethodConverter.class})
public class Courier implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "courier_id")
    private Long courierId;
    @ColumnInfo(name = "logo_data")
    private byte[] logoData;
    private String code;
    @NonNull
    private String name;
    private CourierStatus status;
    @ColumnInfo(name = "keyboard_input_method")
    private KeyboardInputMethod keyboardInputMethod;
    @ColumnInfo(name = "heading")
    private String heading;
    private int position;



    public Courier(Long courierId, String code, @NonNull String name, CourierStatus status, KeyboardInputMethod keyboardInputMethod) {
        this.courierId = courierId;
        this.code = code;
        this.name = name;
        this.status = status;
        this.keyboardInputMethod = keyboardInputMethod;
    }

    public Courier(Long courierId, String code, @NonNull String name, CourierStatus status, KeyboardInputMethod keyboardInputMethod, Context context, Integer logoResourceId,int position) {
        this.courierId = courierId;
        this.code = code;
        this.name = name;
        this.status = status;
        this.keyboardInputMethod = keyboardInputMethod;
        this.position = position;
        this.heading = "Enter the last 4 digits of the order ID";
        //convert logo to byte array
        if (context != null) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), logoResourceId);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            this.logoData = stream.toByteArray();
        }
    }

    public Long getCourierId() {
        return courierId;
    }

    public void setCourierId(Long courierId) {
        this.courierId = courierId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public CourierStatus getStatus() {
        return status;
    }

    public void setStatus(CourierStatus status) {
        this.status = status;
    }

    public KeyboardInputMethod getKeyboardInputMethod() {
        return keyboardInputMethod;
    }

    public void setKeyboardInputMethod(KeyboardInputMethod keyboardInputMethod) {
        this.keyboardInputMethod = keyboardInputMethod;
    }

    public byte[] getLogoData() {
        return logoData;
    }

    public void setLogoData(byte[] logoData) {
        this.logoData = logoData;
    }


    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
