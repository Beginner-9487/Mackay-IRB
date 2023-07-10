package com.example.mackayirb.data.central;

import com.example.mackayirb.data.ble.BLEDataServer;

import java.util.ArrayList;

public abstract class CentralDeviceData<Manager, Label> {

    public BLEDataServer.BLEData bleData;
    public Manager myManager;

    public ArrayList<Label> labelData;

    public boolean isLoaded = false;

    public CentralDeviceData(BLEDataServer.BLEData BleData, Manager manager) {
        bleData = BleData;
        myManager = manager;
    }

    public abstract Label findLabel(Label item);

    public abstract Object getLabelIndicator();

    public abstract boolean removeLabelByObject(byte type, Object object);

    public abstract boolean createDeviceDataFile();

    public abstract boolean editDeviceDataFile(Label centralLabelData);

}
