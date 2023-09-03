package com.example.mackayirb.data.central;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mackayirb.R;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.MyExcelFile;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.github.mikephil.charting.data.Entry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FootLabelData extends CentralLabelData<FootManagerData, FootDeviceData> {

    public String labelName;
    public byte levelOfDownload = 0;

    public static class Position {
        public static final byte LEFT_FOOT = 0x0a;
        public static final byte RIGHT_FOOT = 0x0b;
        public static final byte[] ALL_POSITION = new byte[]{LEFT_FOOT, RIGHT_FOOT};
    }
    public static class MainFloatList {
        public static final byte ShearForceX = 0;
        public static final byte ShearForceY = 1;
        public static final byte Pressures = 2;
        public static final byte Temperatures = 3;

        public static final byte MAIN_LENGTH = Temperatures + 1;
        public static final byte defaultNumberOfByte = 2;
        public static final byte[] Once = new byte[]{Temperatures};
    }
    public static class ViceFloat {
        public static final byte AccX = 0;
        public static final byte AccY = 1;
        public static final byte AccZ = 2;
        public static final byte GyroX = 3;
        public static final byte GyroY = 4;
        public static final byte GyroZ = 5;
        public static final byte MagX = 6;
        public static final byte MagY = 7;
        public static final byte MagZ = 8;
        public static final byte Pitch = 9;
        public static final byte Roll = 10;
        public static final byte Yaw = 11;

        public static final byte VICE_LENGTH = Yaw + 1;
        public static final byte defaultNumberOfByte = 2;
        public static final byte[] Triple = new byte[]{};
    }
    private ArrayList<Foot> feet = new ArrayList<>();
    public class Foot {
        public byte position;

        public ArrayList<float[]> mainFloatList = new ArrayList<>();
        public Entry[] mainCenter = new Entry[MainFloatList.MAIN_LENGTH];
        public float[] mainAverage = new float[MainFloatList.MAIN_LENGTH];
        public ArrayList<float[]> mainDirection = new ArrayList<>();

        public float[] viceFloat = new float[ViceFloat.VICE_LENGTH];

        public Foot(byte[] bytes) {

            OtherUsefulFunction.ByteIterator data = new OtherUsefulFunction.ByteIterator(bytes);

            position = data.next();

            // viceFloat
            for (byte i=0; i<ViceFloat.VICE_LENGTH; i++) {
                byte numberOfByte = ViceFloat.defaultNumberOfByte;
                if(OtherUsefulFunction.contains(ViceFloat.Triple, i)) { numberOfByte = (byte) 3; }
                viceFloat[i] = (float) OtherUsefulFunction.byteArrayToSignedInt(
                        data.array(true, numberOfByte)
                );
                // Log.d("Vice:" + String.valueOf(i) + ":" + String.valueOf(data.index()));
            }

            // mainFloatList, mainEntry
            for(byte m = 0; m < MainFloatList.MAIN_LENGTH; m++) {
                mainFloatList.add(new float[getNumberOfSensor()]);
                mainCenter[m] = new Entry(0f, 0f);
                mainAverage[m] = 0;
                mainDirection.add(new float[getNumberOfSensor()]);

                for (byte i=0; i<getNumberOfSensor(); i++) {
                    // Magnitude
                    byte numberOfByte = MainFloatList.defaultNumberOfByte;
                    if(OtherUsefulFunction.contains(MainFloatList.Once, m)) { numberOfByte = (byte) 1; }
                    mainFloatList.get(m)[i] = (float) OtherUsefulFunction.byteArrayToUnsignedInt(
                            data.array(true, numberOfByte)
                    );
                    // Log.d("Main:" + String.valueOf(m) + ":" + String.valueOf(i) + ":" + String.valueOf(data.index()) + ":" + String.valueOf(mainFloatList.get(m)[i]));
                    // Average, Center
                    mainAverage[m] += mainFloatList.get(m)[i];
                    Entry currentPosition = getSensorList().get(i);
                    mainCenter[m].setX(mainCenter[m].getX() + currentPosition.getX() * mainFloatList.get(m)[i]);
                    mainCenter[m].setY(mainCenter[m].getY() + currentPosition.getY() * mainFloatList.get(m)[i]);
                }
                // Average, Center
                mainCenter[m].setX(mainCenter[m].getX() / mainAverage[m]);
                mainCenter[m].setY(mainCenter[m].getY() / mainAverage[m]);
                mainAverage[m] /= getNumberOfSensor();
            }
        }
        public ArrayList<Entry> getSensorList() {
            return FootSensorPositions.getPositions(this.position);
        }
        public int getNumberOfSensor() {
            return FootSensorPositions.getPositions(this.position).size();
        }
        public Entry getVectorMagnitudeDirection(int index, byte X, byte Y, @Nullable Entry center) {
            if(center == null) {
                center = new Entry(0f,0f);
            }
            float x = mainFloatList.get(X)[index] - center.getX();
            float y = mainFloatList.get(Y)[index] - center.getY();
            return new Entry(
                    (float) Math.sqrt(
                            (Math.pow(x, 2) + Math.pow(y, 2))
                    ),
                    (float) Math.atan2(x, y)
            );
        }
        public ArrayList<String> getViceName() {
            return getViceNameByPosition(this.position);
        }
    }

    public ArrayList<ArrayList<Entry>> getViceEntryList() {
        ArrayList<ArrayList<Entry>> entryList = new ArrayList<>();
        for(int i=0; i < ViceFloat.VICE_LENGTH; i++) {
            entryList.add(new ArrayList<>());
        }
        if(feet.size() == 0) { return entryList; }
        for(int i=0; i < ViceFloat.VICE_LENGTH; i++) {
            for (Foot foot:feet) {
                entryList.get(i).add(new Entry(entryList.get(i).size()-1, foot.viceFloat[i]));
//                Log.d(String.valueOf(entryList.size()-1) + ": " + String.valueOf(entryList.get(entryList.size()-1).size()));
            }
        }
//        Log.d("ALL: " + String.valueOf(entryList.size()));
        return entryList;
    }
    public static ArrayList<String> getAllViceName() {
        ArrayList<String> strings = new ArrayList<>();
        for (byte p:Position.ALL_POSITION) {
            strings.addAll(getViceNameByPosition(p));
        }
        return strings;
    }
    public static ArrayList<ArrayList<String>> getAllViceNameList() {
        ArrayList<ArrayList<String>> strings = new ArrayList<>();
        for (byte p:Position.ALL_POSITION) {
            strings.add(getViceNameByPosition(p));
        }
        return strings;
    }
    public static ArrayList<String> getViceNameByPosition(byte position) {
        ArrayList<String> strings = new ArrayList<>();
        int index = 0;
        switch (position) {
            case Position.LEFT_FOOT:
                index = 0;
                break;
            case Position.RIGHT_FOOT:
                index = 1;
                break;
        }
        for (String vice:BasicResourceManager.getResources().getStringArray(R.array.FootViceDataLabels)) {
            strings.add(BasicResourceManager.getResources().getStringArray(R.array.Foot)[index] + " - " + vice);
        }
        return strings;
    }

    public ArrayList<Foot> getFeet() {
        return feet;
    }
    public ArrayList<Foot> getNewestFeet() {
        ArrayList<Foot> newestFeet = new ArrayList<>();
        for (int i=feet.size()-1; i>=0; i--) {
            boolean lock = true;
            for (Foot foot:newestFeet) {
                if(foot.position == feet.get(i).position) {
                    lock = false;
                    break;
                }
            }
            if(lock) {
                newestFeet.add(feet.get(i));
            }
        }
        return newestFeet;
    }

    public Foot CreateNewFootByBytes(byte[] bytes) {
//        Toast.makeText(BasicResourceManager.getCurrentFragment().getContext(), "CreateNewFootByBytes: " + String.valueOf(bytes.length), Toast.LENGTH_SHORT).show();
//        Toast.makeText(BasicResourceManager.getCurrentFragment().getContext(),
//                "CreateNewFootByBytes: " + OtherUsefulFunction.byteArrayToHexString(bytes, ", ")
//                , Toast.LENGTH_SHORT).show();
        return new Foot(bytes);
    }

    public FootLabelData(FootDeviceData FootDeviceData, String LabelName, boolean Show, byte LevelOfDownload) {
        super(FootDeviceData);
//        labelName = LabelName;
        labelName = "abc";
        levelOfDownload = LevelOfDownload;
    }
    public FootLabelData(FootDeviceData FootDeviceData, String LabelName) {
        super(FootDeviceData);
//        labelName = LabelName;
        labelName = "abc";
    }
    public FootLabelData(FootDeviceData device) {
        super(device);
    }

    @Override
    public byte addNewData(byte[] bytes) {
//        Log.e(OtherUsefulFunction.byteArrayToHexString(bytes, ", "));
        if(bytes == null) {
            // Log.e("Data is null.");
            return 0x00;
        }
//        Toast.makeText(BasicResourceManager.getCurrentFragment().getContext(), "addNewData: start", Toast.LENGTH_SHORT).show();
        feet.add(CreateNewFootByBytes(bytes));
//        Toast.makeText(BasicResourceManager.getCurrentFragment().getContext(), "addNewData: end", Toast.LENGTH_SHORT).show();
        return 0x01;
    }

    // =====================================================================================
    // =====================================================================================

    public void markAsDownloaded() {
        if(levelOfDownload != 0x02) {
            levelOfDownload = 0x02;
            myDeviceData.labelNamingStrategy.next();
        }
    }

    // =====================================================================================
    // =====================================================================================

    @Override
    public boolean saveNewFile() {
//        Log.i("saveMyFile: " + labelName);
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH-mm-ss");
            String currentTime = sdf.format(calendar.getTime());

            Log.i(labelName);
            MyExcelFile file = new MyExcelFile();
            String sdCardPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator;
            file.createExcelWorkbook(sdCardPath + currentTime + ".xls");
            file.create_new_sheet(currentTime);

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

}
