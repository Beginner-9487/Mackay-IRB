package com.example.mackayirb.data.central;

import com.example.mackayirb.SampleGattAttributes;
import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.MyNamingStrategy;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FootDataManager extends CentralDataManager<FootDataManager, FootDeviceData, FootLabelData> {

    @Inject
    public FootDataManager() {
        super();
        deviceData = new ArrayList<FootDeviceData>();
    }

    public static final byte LabelName = 0x00;
    @Override
    public void updateLabelData(BLEDataServer.BLEData bleData) {
        try {
            if(bleData.DataBuffer.getData() != null) {
//                Log.d("updateLabelData");
                findLabelDataByBleAndObject(bleData, LabelName, findDeviceDataByBle(bleData).getLabelIndicator()).addNewData(bleData.DataBuffer.popData());
            }
        } catch (Exception e) {}
    }
    @Override
    public FootLabelData findLabelDataByBleAndObject(BLEDataServer.BLEData bleData, byte type, Object object) {
//        Log.d("findLabelDataByBleAndObject");
        switch (type) {
            case LabelName:
//                Log.d(String.valueOf(LabelName));
                String labelName = (String) object;
//                Log.d("labelName: " + labelName + ", size: " + String.valueOf(deviceData.size()));
                FootDeviceData targetDevice = findDeviceDataByBle(bleData);
                int targetIndex = -1;
                for (int i = 0; i<targetDevice.labelData.size(); i++) {
//                    Log.d("targetDevice.labelData.get(i).labelName: " + String.valueOf(i) + ": " + targetDevice.labelData.get(i).labelName);
                    if(targetDevice.labelData.get(i).labelName.equals(labelName)) {
                        targetIndex = i;
                    }
                }
//                Log.d("targetIndex: " + String.valueOf(targetIndex));
                if(targetIndex == -1) {
                    FootLabelData data = createLabelData(bleData, targetDevice);
//                    Log.d("data != null: " + String.valueOf(data != null));
                    if (data != null) {
                        targetDevice.labelData.add(data);
                        targetIndex = deviceData.size() - 1;
                    } else {
                        return null;
                    }
                }
                return targetDevice.labelData.get(targetIndex);
        }
        return null;
    }

    @Override
    public FootDeviceData createDeviceData(BLEDataServer.BLEData bleData, FootDataManager manager) {
        if (
            BasicResourceManager.isTesting == true ||
            bleData.DataBuffer.getData() != null
        ) {
            return new FootDeviceData(bleData, manager);
        } else {
            return null;
        }
    }

    @Override
    public FootLabelData createLabelData(BLEDataServer.BLEData bleData, FootDeviceData targetDevice) {
        byte[] value = bleData.DataBuffer.popData();
//        Log.d(String.valueOf(value.length));
        String name = targetDevice.getCreatedLabelName();
//        Log.d(name);
        if(value != null && name != null) {
            FootLabelData data = new FootLabelData(targetDevice, name);
            data.addNewData(value);
            return data;
        }
        return null;
    }

    // =====================================================================================
    // =====================================================================================

    public byte defaultMyNamingStrategyMode = MyNamingStrategy.MODE_NULL;
    public String defaultMyNamingStrategyName = "";

    public void removeLabelDataByBLE(BLEDataServer.BLEData bleData, String labelName) {
        findDeviceDataByBle(bleData).removeLabelByObject(FootDeviceData.LABEL_NAME, labelName);
    }

    public void SetAllNamingStrategy(byte Mode, String Name) {
        defaultMyNamingStrategyMode = Mode;
        defaultMyNamingStrategyName = Name;
//        Log.d("Size: " + String.valueOf(deviceData.size()) + "; Mode: " + String.valueOf(Mode) + "; Name: " + Name);
        for(FootDeviceData d: deviceData) {
            d.labelNamingStrategy.setModeAndName(Mode, Name);
        }
    }

    public String[] getDataTypes() {
        return FootLabelData.getDataTypes();
    }

    // =====================================================================================
    // =====================================================================================

    public ArrayList<String> getAllLabelNameArray() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (FootDeviceData d:deviceData) {
            for (String s:d.getLabelNameArray()) {
                arrayList.add(s);
            }
        }
        return arrayList;
    }
    public ArrayList<Boolean> getAllShowArray() {
        ArrayList<Boolean> arrayList = new ArrayList<>();
        for (CentralDeviceData d:deviceData) {
            for (boolean b:((FootDeviceData) d).getShowArray()) {
                arrayList.add(b);
            }
        }
        return arrayList;
    }
    public void setAllShowArray(ArrayList<Boolean> booleans) {
        int index = 0;
        for (FootDeviceData d:deviceData) {
            for(FootLabelData l: d.labelData) {
                try {
                    l.show = booleans.get(index);
                } catch (Exception e) {}
                index++;
            }
        }
    }
    public void setTypeShowArray(ArrayList<Boolean> booleans) {
        for (FootDeviceData d:deviceData) {
            for(FootLabelData l: d.labelData) {
                if(booleans.get(l.type)) {
                    l.show = true;
                }
                else {
                    l.show = false;
                }
            }
        }
    }
    public void deleteSelectedData(ArrayList<Boolean> booleans) {
        int index = 0;
        try {
            for (FootDeviceData d:deviceData) {
//                Log.d(String.valueOf(d.labelData.size()));
                for(FootLabelData l: d.labelData) {
                    if(booleans.get(index)) {
                        d.labelData.remove(l);
                    }
                    index++;
                }
            }
        } catch (Exception e) {}
    }
    public void deleteSelectedType(ArrayList<Boolean> booleans) {
        try {
            for (FootDeviceData d:deviceData) {
//                Log.d(String.valueOf(d.labelData.size()));
                for(FootLabelData l: d.labelData) {
                    if(booleans.get(l.type)) {
                        d.labelData.remove(l);
                    }
                }
            }
        } catch (Exception e) {}
    }
    public ArrayList<FootLabelData> getAllLabelData() {
        ArrayList<FootLabelData> FootLabelDataArrayList = new ArrayList<>();
        for (FootDeviceData d:deviceData) {
            for(FootLabelData l: d.labelData) {
                FootLabelDataArrayList.add(l);
            }
        }
        return FootLabelDataArrayList;
    }
    public HashMap<Integer, FootLabelData> getLatestLabelData() {
        HashMap<Integer, FootLabelData> labelData = new HashMap<>();
        for (FootDeviceData d:deviceData) {
            for(FootLabelData l: d.labelData) {
                labelData.put(l.type, l);
            }
        }
        return labelData;
    }

}
