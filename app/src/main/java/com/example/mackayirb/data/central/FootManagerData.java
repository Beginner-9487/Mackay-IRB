package com.example.mackayirb.data.central;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.example.mackayirb.R;
import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.MyExcelFile;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.github.mikephil.charting.data.Entry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class FootManagerData extends CentralManagerData<FootManagerData, FootDeviceData, FootLabelData> {

    public int firstFootSequenceID = 0;
    public int finalFootSequenceID = 0;
    public int fetchSequenceID() {
        int i = finalFootSequenceID;
        finalFootSequenceID++;
        return i;
    }

    public static final int MAXIMUM_NUMBER_OF_DATA = 4000;

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
//                rawData.add(bleData.DataBuffer.getData());
                String nextLabelDataName = (String) findDeviceDataByBle(bleData).getInitObjectPreparedForNextLabelData();
                findLabelDataByBleAndObject(bleData, LabelName, nextLabelDataName).addNewData(bleData.DataBuffer.popData());
                dealWithLongTimeAgoData();
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
    public int getFeetLength() {
        int feetLength = 0;
        for (FootLabelData footLabelData:getAllLabelData()) {
            feetLength += footLabelData.getFeet().size();
        }
        return feetLength;
    }
    public int getFinalFootSequenceID() {
        int finalSequenceID = 0;
        for (FootLabelData footLabelData:getAllLabelData()) {
            int id = footLabelData.getFeet().get(footLabelData.getFeet().size()-1).sequenceID;
            if(id > finalSequenceID) {
                finalSequenceID = id;
            }
        }
        return finalSequenceID;
    }
    public void dealWithLongTimeAgoData() {
        int feetLength = getFeetLength();
        if(feetLength > MAXIMUM_NUMBER_OF_DATA) {
//            rawData.remove(0);
            for (FootLabelData footLabelData:getAllLabelData()) {
                if(footLabelData.getFeet().get(0).sequenceID == firstFootSequenceID) {
                    footLabelData.removeFootByIndex(0);
//                    Log.d("firstFootSequenceID: " + String.valueOf(firstFootSequenceID));
//                    Log.d("footLabelData.getFeet().size(): " + String.valueOf(footLabelData.getFeet().size()));
                    firstFootSequenceID++;
                    break;
                }
            }
        }
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
            FootLabelData data = new FootLabelData(targetDevice, name, this);
            data.addNewData(value);
            return data;
        }
        return null;
    }

    public void createFile() {
//        Handler mHandler = new Handler(Looper.getMainLooper()) {
//            @Override
//            public void handleMessage(Message msg) {
//                try {
//                    createManagerDataFile();
//                } catch (Exception e) {}
//            }
//        };
//        mHandler.sendEmptyMessage(0);


    }

    @Override
    public boolean createManagerDataFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH-mm-ss");
                    String currentTime = sdf.format(calendar.getTime());

                    // Log.i(labelName);
                    MyExcelFile file = new MyExcelFile();
                    String sdCardPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator;
                    file.createExcelWorkbook(sdCardPath + currentTime + ".xlsx");
                    file.create_new_sheet(BasicResourceManager.getResources().getStringArray(R.array.Foot)[0]);
                    file.create_new_sheet(BasicResourceManager.getResources().getStringArray(R.array.Foot)[1]);

//                    ArrayList<byte[]> savedData = (ArrayList<byte[]>) rawData.clone();
//                    // save rawData
//                    for (int i=0; i<savedData.size(); i++) {
//                        int sheetIndex = 0;
//                        switch (savedData.get(i)[0]) {
//                            case FootLabelData.Position.LEFT_FOOT:
//                                sheetIndex = 0;
//                                break;
//                            case FootLabelData.Position.RIGHT_FOOT:
//                                sheetIndex = 1;
//                                break;
//                        }
//                        int columnIndex = 0;
//                        for (byte b:savedData.get(i)) {
//                            file.write_file(sheetIndex, i, columnIndex, OtherUsefulFunction.byteArrayToHexString(new byte[]{b}, ""));
//                            columnIndex++;
//                        }
//                        Log.d(String.valueOf(i) + ", " + String.valueOf(savedData.size()));
//                    }
//                    Log.d("rawData");

                    // save Map information
                    int finalSequenceID = getFinalFootSequenceID();
                    int sheetIndex = 0;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Toast.makeText(BasicResourceManager.getCurrentActivity(), "Downloading...", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {}
                        }
                    });
                    ArrayList<FootLabelData> allData = getAllLabelData();
                    for(int j=0; j<allData.size(); j++) {
                        int rowIndex = 0;
                        ArrayList<FootLabelData.Foot> feet = allData.get(j).getFeet();
                        for(int k=0; k<feet.size(); k++) {
                            FootLabelData.Foot foot = feet.get(k);
                            switch (foot.position) {
                                case FootLabelData.Position.LEFT_FOOT:
                                    sheetIndex = 0;
                                    break;
                                case FootLabelData.Position.RIGHT_FOOT:
                                    sheetIndex = 1;
                                    break;
                            }
//                            Log.d("foot.position: " + String.valueOf(foot.position));

//                            int columnIndex = savedData.get(0).length + 1;
                            int columnIndex = 0;
//                            Log.d("rowIndex: " + String.valueOf(rowIndex) + ", " + String.valueOf(foot.sequenceID) + ", " + String.valueOf(rawData.size()));
//                            Log.d("rowIndex: " + String.valueOf(rowIndex) + ", " + String.valueOf(foot.sequenceID) + ", " + String.valueOf(finalSequenceID));
//                            Log.d(String.valueOf(rowIndex) + ", " + String.valueOf(foot.sequenceID) + ", " + String.valueOf(savedData.size() + ", " + String.valueOf(rawData.size())));
                            if(foot.sequenceID >= finalSequenceID) {
                                break;
                            }
                            file.write_file(sheetIndex, rowIndex, columnIndex, String.valueOf(foot.sequenceID));
                            columnIndex++;

                            // map
                            for (int i=0; i<foot.getNumberOfSensor(); i++) {
                                file.write_file(sheetIndex, rowIndex, columnIndex, String.valueOf(foot.mapFloatList.get(FootLabelData.MapFloatList.ShearForceX)[i]));
                                columnIndex++;
                                file.write_file(sheetIndex, rowIndex, columnIndex, String.valueOf(foot.mapFloatList.get(FootLabelData.MapFloatList.ShearForceY)[i]));
                                columnIndex++;
                                file.write_file(sheetIndex, rowIndex, columnIndex, String.valueOf(foot.mapFloatList.get(FootLabelData.MapFloatList.Pressures)[i]));
                                columnIndex++;
                                file.write_file(sheetIndex, rowIndex, columnIndex, String.valueOf(foot.mapFloatList.get(FootLabelData.MapFloatList.Temperatures)[i]));
                                columnIndex++;
//                                Log.d("map: " + String.valueOf(i));
                            }

                            // imu
                            file.write_file(sheetIndex, rowIndex, columnIndex, String.valueOf(foot.imuFloat[FootLabelData.IMUFloat.AccX]));
                            columnIndex++;
                            file.write_file(sheetIndex, rowIndex, columnIndex, String.valueOf(foot.imuFloat[FootLabelData.IMUFloat.AccY]));
                            columnIndex++;
                            file.write_file(sheetIndex, rowIndex, columnIndex, String.valueOf(foot.imuFloat[FootLabelData.IMUFloat.AccZ]));
                            columnIndex++;
                            file.write_file(sheetIndex, rowIndex, columnIndex, String.valueOf(foot.imuFloat[FootLabelData.IMUFloat.Pitch]));
                            columnIndex++;
                            file.write_file(sheetIndex, rowIndex, columnIndex, String.valueOf(foot.imuFloat[FootLabelData.IMUFloat.Roll]));
                            columnIndex++;
                            file.write_file(sheetIndex, rowIndex, columnIndex, String.valueOf(foot.imuFloat[FootLabelData.IMUFloat.Yaw]));
                            columnIndex++;
//
//                            Log.d("imu: " + String.valueOf(rowIndex) + ", " + String.valueOf(finalSequenceID));
                            rowIndex++;
                        }
                    }
//                    Log.d("Map");

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
                    }
                } catch (Exception e) {
                    Log.e(e.getMessage());
                    Log.e(e.toString());
                }
            }
        }).start();
        return true;
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
                    footLabelDataArrayList.set(j, allLabel.get(i).getImuEntryList());
//                    Log.d(String.valueOf(i) +", " + String.valueOf(j) + ", " + "oo");
                    break;
                }
            }
        }
//        Log.d(String.valueOf(footLabelDataArrayList.size()));
        return footLabelDataArrayList;
    }


    public ArrayList<Entry> trapz(ArrayList<Entry> entries, float samplingInterval) {
        ArrayList<Entry> trapzList = new ArrayList<>();
        trapzList.add(new Entry(0,0));
        for(int i=1; i<entries.size(); i++) {
            trapzList.add(new Entry(
                    ((entries.get(i-1).getX() + entries.get(i).getX()) * samplingInterval / 2f) + trapzList.get(i-1).getX(),
                    ((entries.get(i-1).getY() + entries.get(i).getY()) * samplingInterval / 2f) + trapzList.get(i-1).getY()
            ));
        }
        return trapzList;
    }
    public ArrayList<Entry> getCorrection(ArrayList<Entry> entries) {

        if(entries.size() == 0) {
            return entries;
        }

        entries.set(0, new Entry(0, 0));
        for(int i=0; i<entries.size()-1; i++) {
            entries.set(i, new Entry(
                entries.get(i).getX() + ((entries.get(0).getX() - entries.get(entries.size()-1).getX()) * i / (entries.size() - 1)),
                entries.get(i).getY() + ((entries.get(0).getY() - entries.get(entries.size()-1).getY()) * i / (entries.size() - 1))
            ));
        }
        entries.set(entries.size()-1, new Entry(0, 0));

        return entries;
    }
    public Entry getAxAy(FootLabelData.Foot foot) {
        float accX = foot.imuFloat[FootLabelData.IMUFloat.AccX] / 16384f;
        float accY = foot.imuFloat[FootLabelData.IMUFloat.AccY] / 16384f;
        float accZ = foot.imuFloat[FootLabelData.IMUFloat.AccZ] / 16384f;
        float thetaPitch = (float) Math.toRadians(foot.imuFloat[FootLabelData.IMUFloat.Pitch]);
        float thetaRoll = (float) Math.toRadians(foot.imuFloat[FootLabelData.IMUFloat.Roll]);
        return new Entry(
                (float) (Math.cos(thetaPitch) * accZ - Math.sin(thetaPitch) * accY) * 9.8f,
                (float) (Math.cos(thetaRoll) * accX - Math.sin(thetaRoll) * accY) * 9.8f
        );
    }
    public ArrayList<ArrayList<Entry>> calc_AxAy(int startID, int endID, boolean isCorrected) {
        ArrayList<ArrayList<Entry>> entriesList = new ArrayList<>();
        for (int i=0; i<getAllLabelData().size(); i++) {
            ArrayList<FootLabelData.Foot> feet = getAllLabelData().get(i).getFeet();
            for(int j=0; j<feet.size(); j++) {
                FootLabelData.Foot foot = feet.get(j);
                if(foot.sequenceID < startID) {
                    continue;
                }
                if(foot.sequenceID > endID) {
                    continue;
                }
                int k = 0;
                switch (foot.position) {
                    case FootLabelData.Position.LEFT_FOOT:
                        k = 0;
                        break;
                    case FootLabelData.Position.RIGHT_FOOT:
                        k = 1;
                        break;
                }
                while (entriesList.size() <= k) {
                    entriesList.add(new ArrayList<>());
                }
                entriesList.get(k).add(getAxAy(foot));
            }
        }
        if(isCorrected) {
            for (int i=0; i<entriesList.size(); i++) {
                entriesList.set(i, getCorrection(entriesList.get(i)));
            }
        }
        return entriesList;
    }
    public ArrayList<Entry> getVxVy(int startID, ArrayList<Entry> entriesAxAy, float samplingInterval, boolean isCorrected) {

        entriesAxAy = trapz(entriesAxAy, samplingInterval);

        if(isCorrected) {
            Entry temp = new Entry(
                    entriesAxAy.get(entriesAxAy.size()-1).getX(),
                    entriesAxAy.get(entriesAxAy.size()-1).getY()
            );
            entriesAxAy = getCorrection(entriesAxAy);
            entriesAxAy.set(entriesAxAy.size()-1, temp);

            Entry x = new Entry(0,0);
            for(int i=0; i<entriesAxAy.size()-1; i++) {
                if (entriesAxAy.get(i).getX() < 0) {
                    x.setX(x.getX() - entriesAxAy.get(i).getX());
                    entriesAxAy.get(i).setX(0);
                }
                if (entriesAxAy.get(i).getY() < 0) {
                    x.setY(x.getY() - entriesAxAy.get(i).getY());
                    entriesAxAy.get(i).setY(0);
                }
            }

            float y = entriesAxAy.size() - 1;

            float z = startID + y / 2f;
            if ((z * 10 % 10) == 5) {
                z += 0.5;
            }
            z = (float) Math.floor(z);

            float a = 0;
            float b = 0;
            for(int i=startID; i<(z-1); i++) {
                a += (i + 1) * (i + 1);
            }
            for(int i = (int) (z - startID); i<(entriesAxAy.size()-2); i++) {
                b += (i + 1) * (i + 1);
            }
            for(int i = 0; i<entriesAxAy.size()-1; i++) {
                if (a != 0 && (i+1) < z) {
                    entriesAxAy.get(i+1).setX(entriesAxAy.get(i+1).getX() + ((x.getX() / 2) * (i+1-0) * (i+1-0) / a));
                    entriesAxAy.get(i+1).setY(entriesAxAy.get(i+1).getY() + ((x.getY() / 2) * (i+1-0) * (i+1-0) / a));
                }
                else if (a != 0 && (i+1) > z) {
                    entriesAxAy.get(i+1).setX(entriesAxAy.get(i+1).getX() + ((x.getX() / 2) * (i+1-(entriesAxAy.size()-1)) * (i+1-(entriesAxAy.size()-1)) / a));
                    entriesAxAy.get(i+1).setY(entriesAxAy.get(i+1).getY() + ((x.getY() / 2) * (i+1-(entriesAxAy.size()-1)) * (i+1-(entriesAxAy.size()-1)) / a));
                }
            }

            entriesAxAy.set(entriesAxAy.size()-1, new Entry(0f,0f));

        }
        return entriesAxAy;
    }
    public ArrayList<ArrayList<Entry>> calc_VxVy(int startID, int endID, int samplingRate, boolean isCorrected) {
        float samplingInterval = 1f / samplingRate;
        ArrayList<ArrayList<Entry>> entriesList = calc_AxAy(startID, endID, true);
        for (int i=0; i<entriesList.size(); i++) {
            entriesList.set(i, getVxVy(startID, entriesList.get(i), samplingInterval, isCorrected));
        }
        return entriesList;
    }
    public ArrayList<Entry> getSxSy(ArrayList<Entry> entries, float samplingInterval) {
        return trapz(entries, samplingInterval);
    }
    public ArrayList<ArrayList<Entry>> calc_SxSy(int startID, int endID, int samplingRate) {
        float samplingInterval = 1f / samplingRate;
        ArrayList<ArrayList<Entry>> entriesList = calc_VxVy(startID, endID, samplingRate, true);
        for (int i=0; i<entriesList.size(); i++) {
            entriesList.set(i, getSxSy(entriesList.get(i), samplingInterval));
        }
        return entriesList;
    }

}
