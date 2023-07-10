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
public class MackayDataManager extends CentralDataManager<MackayDataManager, MackayDeviceData, MackayLabelData> {

    @Inject
    public MackayDataManager() {
        super();
        deviceData = new ArrayList<MackayDeviceData>();
//        Log.d("Create");
    }

    public static final byte LabelName = 0x00;
    @Override
    public void updateLabelData(BLEDataServer.BLEData bleData) {
        try {
            if(bleData.readLastReceivedData(SampleGattAttributes.subscribed_UUIDs.get(0)) != null) {
//                Log.d("updateLabelData");
                findLabelDataByBleAndObject(bleData, LabelName, findDeviceDataByBle(bleData).getLabelIndicator()).addNewData(bleData.getLastReceivedData(SampleGattAttributes.subscribed_UUIDs.get(0)));
            }
        } catch (Exception e) {}
    }
    @Override
    public MackayLabelData findLabelDataByBleAndObject(BLEDataServer.BLEData bleData, byte type, Object object) {
//        Log.d("findLabelDataByBleAndObject");
        switch (type) {
            case LabelName:
//                Log.d(String.valueOf(LabelName));
                String labelName = (String) object;
//                Log.d("labelName: " + labelName + ", size: " + String.valueOf(deviceData.size()));
                MackayDeviceData targetDevice = findDeviceDataByBle(bleData);
                int targetIndex = -1;
                for (int i = 0; i<targetDevice.labelData.size(); i++) {
//                Log.d("targetDevice.labelData.get(i).labelName: " + String.valueOf(i) + ": " + targetDevice.labelData.get(i).labelName);
                    if(targetDevice.labelData.get(i).labelName.equals(labelName)) {
                        targetIndex = i;
                    }
                }
//                Log.d("targetIndex: " + String.valueOf(targetIndex));
                if(targetIndex == -1) {
                    MackayLabelData data = createLabelData(bleData, targetDevice);
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
    public MackayDeviceData createDeviceData(BLEDataServer.BLEData bleData, MackayDataManager manager) {
        if (
                BasicResourceManager.isTesting == true ||
                bleData.readLastReceivedData(SampleGattAttributes.subscribed_UUIDs.get(0)) != null
        ) {
            return new MackayDeviceData(bleData, manager);
        } else {
            return null;
        }
    }

    @Override
    public MackayLabelData createLabelData(BLEDataServer.BLEData bleData, MackayDeviceData targetDevice) {
        byte[] value = bleData.getLastReceivedData(SampleGattAttributes.subscribed_UUIDs.get(0));
        String name = targetDevice.getCreatedLabelName();
        if(value != null && name != null) {
            MackayLabelData data = new MackayLabelData(targetDevice, name);
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
        findDeviceDataByBle(bleData).removeLabelByObject(MackayDeviceData.LABEL_NAME, labelName);
    }

    public void SetAllNamingStrategy(byte Mode, String Name) {
        defaultMyNamingStrategyMode = Mode;
        defaultMyNamingStrategyName = Name;
//        Log.d("Size: " + String.valueOf(deviceData.size()) + "; Mode: " + String.valueOf(Mode) + "; Name: " + Name);
        for(MackayDeviceData d: deviceData) {
            d.labelNamingStrategy.setModeAndName(Mode, Name);
        }
    }

    public String[] getDataTypes() {
        return MackayLabelData.getDataTypes();
    }

    // =====================================================================================
    // =====================================================================================

    public ArrayList<String> getAllLabelNameArray() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (MackayDeviceData d:deviceData) {
            for (String s:d.getLabelNameArray()) {
                arrayList.add(s);
            }
        }
        return arrayList;
    }
    public ArrayList<Boolean> getAllShowArray() {
        ArrayList<Boolean> arrayList = new ArrayList<>();
        for (CentralDeviceData d:deviceData) {
            for (boolean b:((MackayDeviceData) d).getShowArray()) {
                arrayList.add(b);
            }
        }
        return arrayList;
    }
    public void setAllShowArray(ArrayList<Boolean> booleans) {
        int index = 0;
        for (MackayDeviceData d:deviceData) {
            for(MackayLabelData l: d.labelData) {
                try {
                    l.show = booleans.get(index);
                } catch (Exception e) {}
                index++;
            }
        }
    }
    public void setTypeShowArray(ArrayList<Boolean> booleans) {
        for (MackayDeviceData d:deviceData) {
            for(MackayLabelData l: d.labelData) {
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
            for (MackayDeviceData d:deviceData) {
//                Log.d(String.valueOf(d.labelData.size()));
                for(MackayLabelData l: d.labelData) {
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
            for (MackayDeviceData d:deviceData) {
//                Log.d(String.valueOf(d.labelData.size()));
                for(MackayLabelData l: d.labelData) {
                    if(booleans.get(l.type)) {
                        d.labelData.remove(l);
                    }
                }
            }
        } catch (Exception e) {}
    }
    public ArrayList<MackayLabelData> getAllLabelData() {
        ArrayList<MackayLabelData> mackayLabelDataArrayList = new ArrayList<>();
        for (MackayDeviceData d:deviceData) {
            for(MackayLabelData l: d.labelData) {
                mackayLabelDataArrayList.add(l);
            }
        }
        return mackayLabelDataArrayList;
    }
    public HashMap<Integer, MackayLabelData> getLatestLabelData() {
        HashMap<Integer, MackayLabelData> labelData = new HashMap<>();
        for (MackayDeviceData d:deviceData) {
            for(MackayLabelData l: d.labelData) {
                labelData.put(l.type, l);
            }
        }
        return labelData;
    }

}
