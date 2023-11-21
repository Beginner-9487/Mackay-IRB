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

public class MackayDeviceData extends CentralDeviceData<MackayManagerData, MackayLabelData> {

    public MackayDeviceData(BLEDataServer.BLEData BleData, MackayManagerData manager) {
        super(BleData, manager);
        labelNamingStrategy = new MyNamingStrategy(myManager.defaultMyNamingStrategyMode, myManager.defaultMyNamingStrategyName);
    }

    public MyNamingStrategy labelNamingStrategy;

    public String getCreatedLabelName() {
        return labelNamingStrategy.getCurrentName(bleData);
    }

    // get dataset Entry for Chart
    public ArrayList<ArrayList<Entry>> getEntryList() {
        ArrayList<ArrayList<Entry>> entryList = new ArrayList<>();
        int index = 0;
        for (MackayLabelData centralLabelData : labelData) {
            entryList.add(new ArrayList<>(centralLabelData.getSpecialEntries()));
            index++;
        }
        return entryList;
    }

    public ArrayList<String> getLabelNameArray() {
        ArrayList<String> arrayList = new ArrayList<>();
        for (MackayLabelData l : labelData) {
            arrayList.add(l.labelName);
        }
        return arrayList;
    }

    @Override
    public Object getInitObjectPreparedForNextLabelData() {
//        Log.d(String.valueOf(labelNamingStrategy.getMode()) + ": " + labelNamingStrategy.getName() + ": " + labelNamingStrategy.getCurrentName(bleData));
        return labelNamingStrategy.getCurrentName(bleData);
    }

    public static final byte LABEL_NAME = 0x00;
    @Override
    public boolean removeLabelByObject(byte type, Object object) {
        switch (type) {
            case LABEL_NAME:
                String labelName = (String) object;
                for (MackayLabelData l : labelData) {
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
        new Thread(new Runnable() {
            @Override
            public void run() {
        //        Log.d("createDeviceDataFile");
                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH-mm-ss");
                    String currentTime = sdf.format(calendar.getTime());

                    String dataName = labelNamingStrategy.getName();

                    if (createdFileName.equals("")) {
                        createdFileName = new MyNamingStrategy(MyNamingStrategy.All5s, dataName).getCurrentName(bleData);
                    }

                    MyExcelFile file = new MyExcelFile();
                    String sdCardPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator;
                    file.createExcelWorkbook(sdCardPath + createdFileName + ".xls");
                    file.create_new_sheet(dataName);

                    // Add value in the cell
                    int rowIndex = 0;
                    file.write_file(0, rowIndex, 0, BasicResourceManager.getResources().getString(R.string.LabelName) + ": " + dataName);
                    rowIndex++;
                    file.write_file(0, rowIndex, 0, BasicResourceManager.getResources().getString(R.string.SaveFileTime) + ": " + currentTime);
                    rowIndex++;
                    rowIndex++;
                    for (int i = 0; i < BasicResourceManager.getResources().getStringArray(R.array.TypeLabels).length; i++) {
                        file.write_file(0, rowIndex, i, BasicResourceManager.getResources().getStringArray(R.array.TypeLabels)[i]);
                    }

        //                    Log.d(file.toString());

                    // Save as Excel XLSX file
                    if (file.exportDataIntoWorkbook()) {
                        Log.i(BasicResourceManager.getResources().getString(R.string.Temp_UI_save_toast));
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(BasicResourceManager.getCurrentActivity(), dataName + ": " + BasicResourceManager.getResources().getString(R.string.Temp_UI_save_toast), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {}
                            }
                        });
                    }
                } catch (Exception e) {}
            }
        }).start();
        return true;
    }

    @Override
    public boolean editDeviceDataFile(MackayLabelData centralLabelData) {

        if(isLoaded == false) {
            createDeviceDataFile();
            isLoaded = true;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
        //        Log.i(centralLabelData.labelName);
                try {
                    MyExcelFile file = new MyExcelFile();
                    String sdCardPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator;

                    String filePath = sdCardPath + createdFileName + ".xls";
                    file.readExcelFromStorage(BasicResourceManager.getCurrentFragment().getActivity(), filePath);
                    file.setFilePath(filePath);

                    // Add value in the cell
                    int rowIndex = 3;
                    for (Map.Entry<Integer, ArrayList<Float>> data: centralLabelData.getAllXYSpecial().entrySet()) {
                        file.write_file(
                                0,
//                                rowIndex + this.labelData.size(),
                                rowIndex + labelData.stream().filter(d -> d.type == centralLabelData.type).collect(Collectors.toList()).size(),
                                centralLabelData.type,
                                String.valueOf(centralLabelData.getSpecialByX(5.0f).get(0))
                        );
                    }

                    // Save as Excel XLSX file
                    if (file.exportDataIntoWorkbook()) {
                        Log.i(BasicResourceManager.getResources().getString(R.string.Temp_UI_save_toast));
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Toast.makeText(BasicResourceManager.getCurrentActivity(), centralLabelData.labelName + ": " + BasicResourceManager.getResources().getString(R.string.Temp_UI_save_toast), Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {}
                            }
                        });
                    }
                } catch (Exception e) {}
            }
        }).start();
        return true;
    }
}
