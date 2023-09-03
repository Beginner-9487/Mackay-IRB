package com.example.mackayirb.data.central;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.mackayirb.R;
import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.fragment.PermissionAgreeFragment;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.MyExcelFile;
import com.example.mackayirb.utils.MyNamingStrategy;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.github.mikephil.charting.data.Entry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FootManagerData extends CentralManagerData<FootManagerData, FootDeviceData, FootLabelData> {

    public ArrayList<byte[]> rawData = new ArrayList<>();

    @Inject
    public FootManagerData() {
        super();
    }

    public static final byte LabelName = 0x00;
    @Override
    public void updateLabelData(BLEDataServer.BLEData bleData) {
        try {
            while(bleData.DataBuffer.getData() != null) {
//                Log.d("updateLabelData");
                rawData.add(bleData.DataBuffer.getData());
                String nextLabelDataName = (String) findDeviceDataByBle(bleData).getInitObjectPreparedForNextLabelData();
                findLabelDataByBleAndObject(bleData, LabelName, nextLabelDataName).addNewData(bleData.DataBuffer.popData());
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
    public FootDeviceData createDeviceData(BLEDataServer.BLEData bleData, FootManagerData manager) {
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

    @Override
    public boolean createManagerDataFile() {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH-mm-ss");
            String currentTime = sdf.format(calendar.getTime());

            // Log.i(labelName);
            MyExcelFile file = new MyExcelFile();
            String sdCardPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator;
            file.createExcelWorkbook(sdCardPath + currentTime + ".xls");
            file.create_new_sheet(BasicResourceManager.getResources().getStringArray(R.array.Foot)[0]);
            file.create_new_sheet(BasicResourceManager.getResources().getStringArray(R.array.Foot)[1]);

            for (int i=0; i<rawData.size(); i++) {
                int sheetIndex = 0;
                switch (rawData.get(i)[0]) {
                    case FootLabelData.Position.LEFT_FOOT:
                        sheetIndex = 0;
                        break;
                    case FootLabelData.Position.RIGHT_FOOT:
                        sheetIndex = 1;
                        break;
                }
                int columnIndex = 0;
                for (byte b:rawData.get(i)) {
                    file.write_file(sheetIndex, i, columnIndex, OtherUsefulFunction.byteArrayToHexString(new byte[]{b}, ""));
                    columnIndex++;
                }
            }

            // Save as Excel XLSX file
            if (file.exportDataIntoWorkbook()) {
                Log.i(BasicResourceManager.getResources().getString(R.string.Temp_UI_save_toast));
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(BasicResourceManager.getCurrentActivity(), currentTime + ": " + BasicResourceManager.getResources().getString(R.string.Temp_UI_save_toast), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {}
                    }
                });
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

    public void removeLabelDataByBLE(BLEDataServer.BLEData bleData, String labelName) {
        findDeviceDataByBle(bleData).removeLabelByObject(FootDeviceData.LABEL_NAME, labelName);
    }
    public ArrayList<String> getAllLabelNameArray() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (FootDeviceData d:deviceData) {
            for (String s:d.getLabelNameArray()) {
                arrayList.add(s);
            }
        }
        return arrayList;
    }
    public void deleteSelectedData(ArrayList<Boolean> booleans) {
        int index = 0;
        try {
            for (FootDeviceData d:deviceData) {
                for(FootLabelData l: d.labelData) {
                    if(booleans.get(index)) {
                        d.labelData.remove(l);
                    }
                    index++;
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

    /**
     * EachPosition -> EachVice -> DataList
     */
    public ArrayList<ArrayList<ArrayList<Entry>>> getEntryListSequencedByPosition() {
        ArrayList<ArrayList<ArrayList<Entry>>> footLabelDataArrayList = new ArrayList<>();
        ArrayList<FootLabelData> allLabel = getAllLabelData();
//        Log.d("allLabel.size(): " + String.valueOf(allLabel.size()));
        for (byte position:FootLabelData.Position.ALL_POSITION) {
            footLabelDataArrayList.add(new ArrayList<>());
        }
        for (int i=0; i<allLabel.size(); i++) {
//            Log.d("i: " + String.valueOf(i));
            for (int j=0; j<FootLabelData.Position.ALL_POSITION.length; j++) {
//                Log.d("j: " + String.valueOf(j));
                if (allLabel.get(i).getFeet().size() > 0 && FootLabelData.Position.ALL_POSITION[j] == allLabel.get(i).getFeet().get(0).position) {
                    footLabelDataArrayList.set(j, allLabel.get(i).getViceEntryList());
//                    Log.d(String.valueOf(i) +", " + String.valueOf(j) + ", " + "oo");
                    break;
                }
            }
        }
//        Log.d(String.valueOf(footLabelDataArrayList.size()));
        return footLabelDataArrayList;
    }

}
