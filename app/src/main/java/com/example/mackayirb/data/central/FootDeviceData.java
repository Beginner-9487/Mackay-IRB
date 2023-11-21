package com.example.mackayirb.data.central;

import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.utils.MyNamingStrategy;

import java.util.ArrayList;

public class FootDeviceData extends CentralDeviceData<FootManagerData, FootLabelData> {

    public FootDeviceData(BLEDataServer.BLEData BleData, FootManagerData manager) {
        super(BleData, manager);
    }

    public MyNamingStrategy labelNamingStrategy;

    public String getCreatedLabelName() {
        return "abc";
    }

    public ArrayList<String> getLabelNameArray() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (FootLabelData l : labelData) {
            arrayList.add(l.labelName);
        }
        return arrayList;
    }

    @Override
    public Object getInitObjectPreparedForNextLabelData() {
        return "abc";
    }

    public static final byte LABEL_NAME = 0x00;
    @Override
    public boolean removeLabelByObject(byte type, Object object) {
        switch (type) {
            case LABEL_NAME:
                String labelName = (String) object;
                for (FootLabelData l : labelData) {
                    if(l.labelName.equals((String) labelName)) {
                        labelData.remove(l);
                    }
                }
                return true;
        }
        return false;
    }

    @Override
    public boolean createDeviceDataFile() {
        return false;
    }

    @Override
    public boolean editDeviceDataFile(FootLabelData centralLabelData) {
        return false;
    }

}