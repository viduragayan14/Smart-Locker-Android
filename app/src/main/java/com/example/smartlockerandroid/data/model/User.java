package com.example.smartlockerandroid.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.smartlockerandroid.data.enums.UserRole;
import com.example.smartlockerandroid.data.enums.converter.UserRoleConverter;

/**
 * @author itschathurangaj on 6/9/23
 */
@Entity(tableName = "user_table")
@TypeConverters({UserRoleConverter.class})
public class User {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    private Long userId;
    @ColumnInfo(name = "user_code")
    private String userCode;
    private String username;
    @ColumnInfo(name = "user_role")
    private UserRole userRole;
    private String password;
    private String barcode;
    private Boolean status;
    private String createdDate;

    public User() {
    }

    @Ignore
    public User(String userCode, String username, UserRole userRole, String password, String barcode, Boolean status,String createDate) {
        this.userCode = userCode;
        this.username = username;
        this.userRole = userRole;
        this.password = password;
        this.barcode = barcode;
        this.status = status;
        this.createdDate = createDate;
    }

    @Ignore
    public User(Long userId, String userCode, String username, UserRole userRole, String password, String barcode, Boolean status,String createDate) {
        this.userId = userId;
        this.userCode = userCode;
        this.username = username;
        this.userRole = userRole;
        this.password = password;
        this.barcode = barcode;
        this.status = status;
        this.createdDate = createDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
