package com.example.mackayirb.data.central;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.mackayirb.R;
import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.MyExcelFile;
import com.example.mackayirb.utils.MyNamingStrategy;
import com.github.mikephil.charting.data.Entry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.stream.Collectors;

public class FootDeviceData extends CentralDeviceData<FootDataManager, FootLabelData> {

    public FootDeviceData(BLEDataServer.BLEData BleData, FootDataManager manager) {
        super(BleData, manager);
        labelData = new ArrayList<FootLabelData>();
//        Log.d(String.valueOf(myManager.defaultMyNamingStrategyMode) + ": " + myManager.defaultMyNamingStrategyName);
        labelNamingStrategy = new MyNamingStrategy(myManager.defaultMyNamingStrategyMode, myManager.defaultMyNamingStrategyName);
    }

    public MyNamingStrategy labelNamingStrategy;

    public String getCreatedLabelName() {
//        return labelNamingStrategy.getCurrentName(bleData);
        return "abc";
    }

    public ArrayList<String> getLabelNameArray() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (FootLabelData l : labelData) {
            arrayList.add(l.labelName);
        }
        return arrayList;
    }

    public ArrayList<Boolean> getShowArray() {
        ArrayList<Boolean> arrayList = new ArrayList<>();
        for (FootLabelData l : labelData) {
            arrayList.add(l.show);
            // Log.e(String.valueOf(l.labelName) + ": " + String.valueOf(l.show));
        }
        return arrayList;
    }

    public void removeLabelDataByLabelName(String labelName) {
        for (FootLabelData l : labelData) {
            if (l.labelName.equals(labelName)) {
                labelData.remove(l);
            }
        }
    }

    @Override
    public FootLabelData findLabel(FootLabelData item) {
        int targetIndex = -1;
        for (int i = 0; i < labelData.size(); i++) {
            if (labelData.get(i).equals(item)) {
                targetIndex = i;
            }
        }
//        Log.d(String.valueOf(deviceData.size()));
        if (targetIndex == -1) {
            labelData.add((FootLabelData) new FootLabelData(this));
            targetIndex = labelData.size() - 1;
        }
        return labelData.get(targetIndex);
    }

    @Override
    public Object getLabelIndicator() {
//        return labelNamingStrategy.getCurrentName(bleData);
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


    String createdFileName = "";
    @Override
    public boolean createDeviceDataFile() {
        return false;
    }

    @Override
    public boolean editDeviceDataFile(FootLabelData centralLabelData) {
        return false;
    }

}