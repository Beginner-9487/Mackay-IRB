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

    public Label findLabel(Label item) {
        int targetIndex = -1;
        for (int i = 0; i < labelData.size(); i++) {
            if (labelData.get(i).equals(item)) {
                targetIndex = i;
            }
        }
//        Log.d(String.valueOf(deviceData.size()));
        if (targetIndex == -1) {
            labelData.add(createNewLabelData());
            targetIndex = labelData.size() - 1;
        }
        return labelData.get(targetIndex);
    }

    public abstract Label createNewLabelData();

    public abstract Object getInitObjectPreparedForNextLabelData();

    public abstract boolean removeLabelByObject(byte type, Object object);

    public abstract boolean createDeviceDataFile();

    public abstract boolean editDeviceDataFile(Label centralLabelData);
}
