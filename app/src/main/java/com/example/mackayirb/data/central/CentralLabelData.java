package com.example.mackayirb.data.central;


import android.content.res.Resources;

public abstract class CentralLabelData<Device> {

    Device myDeviceData;

    public CentralLabelData(Device device) {
        myDeviceData = device;
    }

    public abstract byte addNewData(byte[] bytes);

    public abstract boolean saveNewFile();

    public Device getDevice() {
        return myDeviceData;
    }
}

