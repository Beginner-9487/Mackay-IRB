package com.example.mackayirb.data.central;

public abstract class CentralLabelData<Manager extends CentralManagerData, Device extends CentralDeviceData> {

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