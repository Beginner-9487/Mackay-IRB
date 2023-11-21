package com.example.mackayirb.data.central;

import com.example.mackayirb.data.ble.BLEDataServer;

import java.util.ArrayList;

public abstract class CentralDeviceData<Manager extends CentralManagerData, Label extends CentralLabelData> {

    public BLEDataServer.BLEData bleData;
    public Manager myManager;

    public ArrayList<Label> labelData = new ArrayList<>();

    public boolean isLoaded = false;

    public CentralDeviceData(BLEDataServer.BLEData BleData, Manager manager) {
        bleData = BleData;
        myManager = manager;
    }

    public abstract Object getInitObjectPreparedForNextLabelData();

    public abstract boolean removeLabelByObject(byte type, Object object);

    public abstract boolean createDeviceDataFile();

    public abstract boolean editDeviceDataFile(Label centralLabelData);

    public void clearLabelData() {
        labelData.clear();
    }
}
