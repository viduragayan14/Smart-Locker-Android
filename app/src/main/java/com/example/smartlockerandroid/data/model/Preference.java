package com.example.smartlockerandroid.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.smartlockerandroid.data.enums.BayLightStatus;
import com.example.smartlockerandroid.data.enums.converter.BayLightStatusConverter;

import java.net.InetAddress;

/**
 * @author itschathurangaj on 5/16/23
 */
@Entity(tableName = "preference_table")
@TypeConverters({BayLightStatusConverter.class})
public class Preference {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "preference_id")
    private Long preferenceId;

    //Home Screen settings
    @ColumnInfo(name = "home_logo_data")
    private byte[] homeLogoData;

    @ColumnInfo(name = "welcome_text")
    private String welcomeText;

    @ColumnInfo(name = "welcome_message")
    private String welcomeMessage;

    @ColumnInfo(name = "is_app_time_auto")
    private Boolean isAppTimeAuto;

    @ColumnInfo(name = "app_time_offset")
    private Long appTimeOffset;

    //Firmware version
    @ColumnInfo(name = "firmware_version")
    private String firmwareVersion;

    //Late pick up timer
    @ColumnInfo(name = "late_pickup_timer_status")
    private Boolean latePickupTimerStatus;

    @ColumnInfo(name = "late_pickup_timer_timeout")
    private Long latePickupTimerTimeout;

    @ColumnInfo(name = "late_pickup_timer_timeout_load_in")
    private Long latePickupTimerTimeoutLoadIn;

    @ColumnInfo(name = "late_pickup_timer_timeout_load_in_enable")
    private Boolean latePickupTimerTimeoutLoadInEnable;

    @ColumnInfo(name = "late_pickup_timer_led_color")
    private BayLightStatus latePickupTimerLedColor;

    @ColumnInfo(name = "late_pickup_warning")
    private String latePickupWarning;

    //bay color settings
    @ColumnInfo(name = "bay_color_when_empty")
    private BayLightStatus bayColorWhenEmpty;

    @ColumnInfo(name = "bay_color_when_full")
    private BayLightStatus bayColorWhenFull;

    @ColumnInfo(name = "bay_color_when_reloading")
    private BayLightStatus bayColorWhenReloading;

    //open bay settings
    @ColumnInfo(name = "bay_timeout_time_1")
    private Long bayTimeoutTime1;

    @ColumnInfo(name = "bay_timeout_color_1")
    private BayLightStatus bayTimeoutColor1;

    @ColumnInfo(name = "bay_timeout_beep_status_1")
    private Boolean bayTimeoutTimeBeepStatus1;

    @ColumnInfo(name = "bay_timeout_time_2")
    private Long bayTimeoutTime2;

    @ColumnInfo(name = "bay_timeout_color_2")
    private BayLightStatus bayTimeoutColor2;

    @ColumnInfo(name = "bay_timeout_beep_status_2")
    private Boolean bayTimeoutTimeBeepStatus2;

    @ColumnInfo(name = "no_of_bays")
    private Integer noOfBays;

    @ColumnInfo(name = "service_bay")
    private Integer serviceBay;

    @ColumnInfo(name = "back_load_connected")
    private Boolean connected;

    @ColumnInfo(name = "back_load_config")
    private String config;

    @ColumnInfo(name = "client_ip")
    private String clientIp;

    @ColumnInfo(name = "server_ip")
    private String serverIp;

    public Preference(Long preferenceId,
                      String welcomeText,
                      String welcomeMessage,
                      String firmwareVersion,
                      Boolean latePickupTimerStatus,
                      Boolean latePickupTimerTimeoutLoadInEnable,
                      Long latePickupTimerTimeout,
                      Long latePickupTimerTimeoutLoadIn,
                      BayLightStatus latePickupTimerLedColor,
                      String latePickupWarning,
                      BayLightStatus bayColorWhenEmpty,
                      BayLightStatus bayColorWhenFull,
                      BayLightStatus bayColorWhenReloading,
                      Long bayTimeoutTime1,
                      BayLightStatus bayTimeoutColor1,
                      Boolean bayTimeoutTimeBeepStatus1,
                      Long bayTimeoutTime2,
                      BayLightStatus bayTimeoutColor2,
                      Boolean bayTimeoutTimeBeepStatus2,
                      Integer noOfBays,
                      Integer serviceBay,
                      Boolean connected,String config,String clientIp,String serverIp
    ) {
        this.preferenceId = preferenceId;
        this.welcomeText = welcomeText;
        this.welcomeMessage = welcomeMessage;
        this.firmwareVersion = firmwareVersion;
        this.latePickupTimerStatus = latePickupTimerStatus;
        this.latePickupTimerTimeout = latePickupTimerTimeout;
        this.latePickupTimerTimeoutLoadIn = latePickupTimerTimeoutLoadIn;
        this.latePickupTimerTimeoutLoadInEnable = latePickupTimerTimeoutLoadInEnable;
        this.latePickupTimerLedColor = latePickupTimerLedColor;
        this.latePickupWarning = latePickupWarning;
        this.bayColorWhenEmpty = bayColorWhenEmpty;
        this.bayColorWhenFull = bayColorWhenFull;
        this.bayColorWhenReloading = bayColorWhenReloading;
        this.bayTimeoutTime1 = bayTimeoutTime1;
        this.bayTimeoutColor1 = bayTimeoutColor1;
        this.bayTimeoutTimeBeepStatus1 = bayTimeoutTimeBeepStatus1;
        this.bayTimeoutTime2 = bayTimeoutTime2;
        this.bayTimeoutColor2 = bayTimeoutColor2;
        this.bayTimeoutTimeBeepStatus2 = bayTimeoutTimeBeepStatus2;
        this.noOfBays = noOfBays;
        this.serviceBay = serviceBay;
        this.connected = connected;
        this.config = config;
        this.clientIp = clientIp;
        this.serverIp =  serverIp;
    }

    public Long getPreferenceId() {
        return preferenceId;
    }

    public void setPreferenceId(Long preferenceId) {
        this.preferenceId = preferenceId;
    }

    public String getWelcomeText() {
        return welcomeText;
    }

    public void setWelcomeText(String welcomeText) {
        this.welcomeText = welcomeText;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }

    public Boolean getLatePickupTimerStatus() {
        return latePickupTimerStatus;
    }

    public void setLatePickupTimerStatus(Boolean latePickupTimerStatus) {
        this.latePickupTimerStatus = latePickupTimerStatus;
    }

    public Long getLatePickupTimerTimeout() {
        return latePickupTimerTimeout;
    }

    public void setLatePickupTimerTimeout(Long latePickupTimerTimeout) {
        this.latePickupTimerTimeout = latePickupTimerTimeout;
    }

    public BayLightStatus getLatePickupTimerLedColor() {
        return latePickupTimerLedColor;
    }

    public void setLatePickupTimerLedColor(BayLightStatus latePickupTimerLedColor) {
        this.latePickupTimerLedColor = latePickupTimerLedColor;
    }

    public String getLatePickupWarning() {
        return latePickupWarning;
    }

    public void setLatePickupWarning(String latePickupWarning) {
        this.latePickupWarning = latePickupWarning;
    }

    public BayLightStatus getBayColorWhenEmpty() {
        return bayColorWhenEmpty;
    }

    public void setBayColorWhenEmpty(BayLightStatus bayColorWhenEmpty) {
        this.bayColorWhenEmpty = bayColorWhenEmpty;
    }

    public BayLightStatus getBayColorWhenFull() {
        return bayColorWhenFull;
    }

    public void setBayColorWhenFull(BayLightStatus bayColorWhenFull) {
        this.bayColorWhenFull = bayColorWhenFull;
    }

    public BayLightStatus getBayColorWhenReloading() {
        return bayColorWhenReloading;
    }

    public void setBayColorWhenReloading(BayLightStatus bayColorWhenReloading) {
        this.bayColorWhenReloading = bayColorWhenReloading;
    }

    public Long getBayTimeoutTime1() {
        return bayTimeoutTime1;
    }

    public void setBayTimeoutTime1(Long bayTimeoutTime1) {
        this.bayTimeoutTime1 = bayTimeoutTime1;
    }

    public BayLightStatus getBayTimeoutColor1() {
        return bayTimeoutColor1;
    }

    public void setBayTimeoutColor1(BayLightStatus bayTimeoutColor1) {
        this.bayTimeoutColor1 = bayTimeoutColor1;
    }

    public Boolean getBayTimeoutTimeBeepStatus1() {
        return bayTimeoutTimeBeepStatus1;
    }

    public void setBayTimeoutTimeBeepStatus1(Boolean bayTimeoutTimeBeepStatus1) {
        this.bayTimeoutTimeBeepStatus1 = bayTimeoutTimeBeepStatus1;
    }

    public Long getBayTimeoutTime2() {
        return bayTimeoutTime2;
    }

    public void setBayTimeoutTime2(Long bayTimeoutTime2) {
        this.bayTimeoutTime2 = bayTimeoutTime2;
    }

    public BayLightStatus getBayTimeoutColor2() {
        return bayTimeoutColor2;
    }

    public void setBayTimeoutColor2(BayLightStatus bayTimeoutColor2) {
        this.bayTimeoutColor2 = bayTimeoutColor2;
    }

    public Boolean getBayTimeoutTimeBeepStatus2() {
        return bayTimeoutTimeBeepStatus2;
    }

    public void setBayTimeoutTimeBeepStatus2(Boolean bayTimeoutTimeBeepStatus2) {
        this.bayTimeoutTimeBeepStatus2 = bayTimeoutTimeBeepStatus2;
    }

    public Boolean getAppTimeAuto() {
        return isAppTimeAuto;
    }

    public void setAppTimeAuto(Boolean appTimeAuto) {
        isAppTimeAuto = appTimeAuto;
    }

    public Long getAppTimeOffset() {
        return appTimeOffset;
    }

    public void setAppTimeOffset(Long appTimeOffset) {
        this.appTimeOffset = appTimeOffset;
    }

    public byte[] getHomeLogoData() {
        return homeLogoData;
    }

    public void setHomeLogoData(byte[] homeLogoData) {
        this.homeLogoData = homeLogoData;
    }

    public Integer getNoOfBays() {
        return noOfBays;
    }

    public void setNoOfBays(Integer noOfBays) {
        this.noOfBays = noOfBays;
    }

    public Integer getServiceBay() {
        return serviceBay;
    }

    public void setServiceBay(Integer serviceBay) {
        this.serviceBay = serviceBay;
    }

    public Boolean getConnected() {
        return connected;
    }

    public void setConnected(Boolean connected) {
        this.connected = connected;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public Long getLatePickupTimerTimeoutLoadIn() {
        return latePickupTimerTimeoutLoadIn;
    }

    public void setLatePickupTimerTimeoutLoadIn(Long latePickupTimerTimeoutLoadIn) {
        this.latePickupTimerTimeoutLoadIn = latePickupTimerTimeoutLoadIn;
    }

    public Boolean getLatePickupTimerTimeoutLoadInEnable() {
        return latePickupTimerTimeoutLoadInEnable;
    }

    public void setLatePickupTimerTimeoutLoadInEnable(Boolean latePickupTimerTimeoutLoadInEnable) {
        this.latePickupTimerTimeoutLoadInEnable = latePickupTimerTimeoutLoadInEnable;
    }
}
