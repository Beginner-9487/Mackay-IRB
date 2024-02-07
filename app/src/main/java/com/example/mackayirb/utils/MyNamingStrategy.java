package com.example.mackayirb.utils;

import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.mackayirb.R;
import com.example.mackayirb.SampleGattAttributes;
import com.example.mackayirb.data.ble.BLEDataServer;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MyNamingStrategy {

    private String finalName = null;

    private String name = "";

    public String getName() {
        return name;
    }

    private int mode = MODE_NULL;

    public int getMode() {
        return mode;
    }

    private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH-mm-ss");
    private String currentTime = "";


    synchronized public String getCurrentName(BLEDataServer.BLEData bleData) {
        if (finalName == null) {
            switch (mode) {
                case MODE_NORMAL:
                    finalName = this.name;
                    break;
                case MODE_XIE_ZHI_LONG:
                    setCurrentTime();
//                    finalName = this.name + "_" + bleData.device.getName() + "_" + currentTime + "_(" + BasicResourceManager.getResources().getStringArray(R.array.DataTypes)[bleData.readLastReceivedData(SampleGattAttributes.subscribed_UUIDs.get(0))[0]] + ")";
//                    finalName = this.name + "_" + currentTime + "_(" + BasicResourceManager.getResources().getStringArray(R.array.TypeLabels)[bleData.DataBuffer.getData()[0]] + ")";
//                    finalName = this.name + "_" + bleData.device.getName() + "_" + currentTime + "_(" + BasicResourceManager.getResources().getStringArray(R.array.TypeLabels)[bleData.DataBuffer.getData()[0]] + ")";
                    finalName = this.name + "_" + bleData.device.getAddress().replace(":","-") + "_" + currentTime + "_(" + BasicResourceManager.getResources().getStringArray(R.array.TypeLabels)[bleData.DataBuffer.getData()[0]] + ")";
                    break;
                case All5s:
                    if(currentTime.equals("")) {
                        setCurrentTime();
                    }
//                    finalName = this.name + "_All_5s_" + currentTime;
//                    finalName = this.name + "_" + bleData.device.getName() + "_All_5s_" + currentTime;
                    finalName = this.name + "_" + bleData.device.getAddress().replace(":","-") + "_All_5s_" + currentTime;
                    break;
            }
        }
        return finalName;
    }

    synchronized public void refreshName() { finalName = null; }
    synchronized public String refreshName(BLEDataServer.BLEData bleData) {
        finalName = null;
        return getCurrentName(bleData);
    }
    synchronized public void next() { finalName = null; }

    public void setCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        currentTime = sdf.format(calendar.getTime());
    }

    public static final byte MODE_NULL = 0;
    public static final byte MODE_NORMAL = 1;
    public static final byte MODE_XIE_ZHI_LONG = 2;
    public static final byte All5s = 3;
    public MyNamingStrategy(@Nullable Byte Mode, @Nullable String Name) {
        byte mode = (Mode != null) ? Mode.byteValue() : MODE_NULL;
        this.setModeAndName(
            (Mode != null) ? Mode.byteValue() : MODE_NULL,
            (Name != null) ? Name : ""
        );
    }
    public MyNamingStrategy setModeAndName(@NonNull byte Mode, @NonNull String Name) {
        this.mode = Mode;
        this.name = Name;
        return this;
    }

}