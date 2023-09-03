package com.example.mackayirb.data.central;

import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.MyNamingStrategy;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MackayManagerData extends CentralManagerData<MackayManagerData, MackayDeviceData, MackayLabelData> {

    @Inject
    public MackayManagerData() {
        super();
    }

    public static final byte LabelName = 0x00;
    @Override
    public void updateLabelData(BLEDataServer.BLEData bleData) {
        try {
            while(bleData.DataBuffer.getData() != null) {
//                Log.d("updateLabelData");
                String nextLabelDataName = (String) findDeviceDataByBle(bleData).getInitObjectPreparedForNextLabelData();
                findLabelDataByBleAndObject(bleData, LabelName, nextLabelDataName).addNewData(bleData.DataBuffer.popData());
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
    public MackayDeviceData createDeviceData(BLEDataServer.BLEData bleData, MackayManagerData manager) {
        if (
            BasicResourceManager.isTesting == true ||
            bleData.DataBuffer.getData() != null
        ) {
            return new MackayDeviceData(bleData, manager);
        } else {
            return null;
        }
    }

    @Override
    public MackayLabelData createLabelData(BLEDataServer.BLEData bleData, MackayDeviceData targetDevice) {
        byte[] value = bleData.DataBuffer.popData();
        String name = targetDevice.getCreatedLabelName();
        if(value != null && name != null) {
            MackayLabelData data = new MackayLabelData(targetDevice, name);
            data.addNewData(value);
            return data;
        }
        return null;
    }

    @Override
    public boolean createManagerDataFile() {
        Log.d("false");
        return false;
    }

    // =====================================================================================
    // =====================================================================================

    public byte defaultMyNamingStrategyMode = MyNamingStrategy.MODE_NULL;
    public String defaultMyNamingStrategyName = "";

    public void SetAllNamingStrategy(byte Mode, String Name) {
        defaultMyNamingStrategyMode = Mode;
        defaultMyNamingStrategyName = Name;
        for(MackayDeviceData d: deviceData) {
            d.labelNamingStrategy.setModeAndName(Mode, Name);
        }
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

    public ArrayList<MackayLabelData> getAllLabelData() {
        ArrayList<MackayLabelData> mackayLabelDataArrayList = new ArrayList<>();
        for (MackayDeviceData d:deviceData) {
            for(MackayLabelData l: d.labelData) {
                mackayLabelDataArrayList.add(l);
            }
        }
        return mackayLabelDataArrayList;
    }
    public void deleteSelectedData(ArrayList<Boolean> booleans) {
        int index = 0;
        try {
            for (MackayDeviceData d:deviceData) {
                for(int i=0; i<d.labelData.size(); i++) {
                    if(booleans.get(index)) {
                        d.labelData.remove(i);
                        i--;
//                        Log.i(String.valueOf(d.labelData.size()));
                    }
//                    Log.i(String.valueOf(index) + ", " + String.valueOf(booleans.get(index)));
                    index++;
                }
            }
        } catch (Exception e) {}
    }
    public void deleteSelectedType(ArrayList<Boolean> booleans) {
        try {
            for (MackayDeviceData d:deviceData) {
//                Log.d(String.valueOf(d.labelData.size()));
                for(int i=0; i<d.labelData.size(); i++) {
                    if(booleans.get(d.labelData.get(i).type)) {
                        d.labelData.remove(d.labelData.get(i));
                        i--;
//                        Log.i(String.valueOf(d.labelData.size()));
                    }
//                    Log.i(String.valueOf(d.labelData.get(i).type) + ", " + String.valueOf(booleans.get(d.labelData.get(i).type)));
                }
            }
        } catch (Exception e) {}
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
