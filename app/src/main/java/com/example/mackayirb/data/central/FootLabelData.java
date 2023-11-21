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

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class FootLabelData extends CentralLabelData<FootManagerData, FootDeviceData> {

    public String labelName;
    public byte levelOfDownload = 0;
    FootManagerData manager;

    public static Module foot_pressure_module = null;

    public static class Position {
        public static final byte LEFT_FOOT = 0x0a;
        public static final byte RIGHT_FOOT = 0x0b;
        public static final byte[] ALL_POSITION = new byte[]{LEFT_FOOT, RIGHT_FOOT};
    }
    public static class MapFloatList {
        // Origin
        public static final byte MagX = 0;
        public static final byte MagY = 1;
        public static final byte MagZ = 2;
        public static final byte Temperatures = 3;

        public static final byte ORIGIN_MAP_LENGTH = Temperatures + 1;
        public static final byte defaultNumberOfByte = 2;
        public static final byte[] Once = new byte[]{Temperatures};

        // Calculated
        public static final byte ShearForceX = 4;
        public static final byte ShearForceY = 5;
        public static final byte Pressures = 6;

        public static final byte CALCULATED_MAP_LENGTH = Pressures + 1 - ORIGIN_MAP_LENGTH;

        public static final byte ALL_MAP_LENGTH = ORIGIN_MAP_LENGTH + CALCULATED_MAP_LENGTH + 1;
    }
    public static class IMUFloat {
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

        public static final byte IMU_LENGTH = Yaw + 1;
        public static final byte defaultNumberOfByte = 2;
        public static final byte[] Triple = new byte[]{};
    }
    private ArrayList<Foot> feet = new ArrayList<>();
    public class Foot {
        public byte position;

        public ArrayList<float[]> mapFloatList = new ArrayList<>();
        public Entry[] mapCenter = new Entry[MapFloatList.ALL_MAP_LENGTH];
        public float[] mapAverage = new float[MapFloatList.ALL_MAP_LENGTH];
        public ArrayList<float[]> mapDirection = new ArrayList<>();

        public float[] imuFloat = new float[IMUFloat.IMU_LENGTH];

        public int sequenceID;

        public Foot(byte[] bytes) {

            // load module
            synchronized (Foot.class) {
                if(foot_pressure_module == null) {
                    try {
                        Log.d("loading path: " + OtherUsefulFunction.assetFilePath(BasicResourceManager.getCurrentActivity().getBaseContext(), "foot_magnets_convert_into_pressure.pt"));
                        foot_pressure_module = LiteModuleLoader.load(OtherUsefulFunction.assetFilePath(BasicResourceManager.getCurrentActivity().getBaseContext(), "foot_magnets_convert_into_pressure.pt"));
//                        foot_pressure_module = org.pytorch.PyTorchAndroid.loadModuleFromAsset(BasicResourceManager.getCurrentActivity().getBaseContext().getAssets(), "foot_pressure_model.ptl");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            sequenceID = manager.fetchSequenceID();
//             Log.d("sequenceID: " + String.valueOf(sequenceID));

            OtherUsefulFunction.ByteIterator data = new OtherUsefulFunction.ByteIterator(bytes);

            position = data.next();

            // imuFloat
            for (byte i = 0; i< IMUFloat.IMU_LENGTH; i++) {
                byte numberOfByte = IMUFloat.defaultNumberOfByte;
                if(OtherUsefulFunction.contains(IMUFloat.Triple, i)) { numberOfByte = (byte) 3; }
                imuFloat[i] = (float) OtherUsefulFunction.byteArrayToSignedInt(
                        data.array(true, numberOfByte)
                );
                // Log.d("Vice:" + String.valueOf(i) + ":" + String.valueOf(data.index()));
            }

            // mapFloatList, mapEntry
            for(byte m = 0; m < MapFloatList.ORIGIN_MAP_LENGTH; m++) {
                mapFloatList.add(new float[getNumberOfSensor()]);
                mapCenter[m] = new Entry(0f, 0f);
                mapAverage[m] = 0;
                mapDirection.add(new float[getNumberOfSensor()]);

                for (byte i=0; i<getNumberOfSensor(); i++) {
                    // Magnitude
                    byte numberOfByte = MapFloatList.defaultNumberOfByte;
                    if(OtherUsefulFunction.contains(MapFloatList.Once, m)) { numberOfByte = (byte) 1; }
                    mapFloatList.get(m)[i] = (float) OtherUsefulFunction.byteArrayToUnsignedInt(
                            data.array(true, numberOfByte)
                    );
                    // Log.d("Main:" + String.valueOf(m) + ":" + String.valueOf(i) + ":" + String.valueOf(data.index()) + ":" + String.valueOf(mainFloatList.get(m)[i]));
                    // Average, Center
                    mapAverage[m] += mapFloatList.get(m)[i];
                    Entry currentPosition = getSensorList().get(i);
                    mapCenter[m].setX(mapCenter[m].getX() + currentPosition.getX() * mapFloatList.get(m)[i]);
                    mapCenter[m].setY(mapCenter[m].getY() + currentPosition.getY() * mapFloatList.get(m)[i]);
                }
                // Average, Center
                mapCenter[m].setX(mapCenter[m].getX() / mapAverage[m]);
                mapCenter[m].setY(mapCenter[m].getY() / mapAverage[m]);
                mapAverage[m] /= getNumberOfSensor();
            }

            // calculated map
            mapFloatList.add(new float[getNumberOfSensor()]);
            mapFloatList.add(new float[getNumberOfSensor()]);
            mapFloatList.add(new float[getNumberOfSensor()]);
            for (byte i=0; i<getNumberOfSensor(); i++) {
                final Tensor inputTensor = Tensor.fromBlob(
                        new float[] {
                                mapFloatList.get(MapFloatList.MagX)[i],
                                mapFloatList.get(MapFloatList.MagY)[i],
                                mapFloatList.get(MapFloatList.MagZ)[i]
                        },
                        new long[] {1,3}
                );
                final Tensor outputTensor = foot_pressure_module.forward(IValue.from(inputTensor)).toTensor();
                final float[] scores = outputTensor.getDataAsFloatArray();
                mapFloatList.get(MapFloatList.ShearForceX)[i] = scores[0];
                mapFloatList.get(MapFloatList.ShearForceY)[i] = scores[1];
                mapFloatList.get(MapFloatList.Pressures)[i] = scores[2];
//                if(i == 20) {
//                    Log.d("MagX: " + String.valueOf(mapFloatList.get(MapFloatList.MagX)[i]));
//                    Log.d("MagY: " + String.valueOf(mapFloatList.get(MapFloatList.MagY)[i]));
//                    Log.d("MagZ: " + String.valueOf(mapFloatList.get(MapFloatList.MagZ)[i]));
//                    Log.d("ShearForceX: " + String.valueOf(mapFloatList.get(MapFloatList.ShearForceX)[i]));
//                    Log.d("ShearForceY: " + String.valueOf(mapFloatList.get(MapFloatList.ShearForceY)[i]));
//                    Log.d("Pressures: " + String.valueOf(mapFloatList.get(MapFloatList.Pressures)[i]));
//                }
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
            float x = mapFloatList.get(X)[index] - center.getX();
            float y = mapFloatList.get(Y)[index] - center.getY();
            return new Entry(
                    (float) Math.sqrt(
                            (Math.pow(x, 2) + Math.pow(y, 2))
                    ),
                    (float) Math.atan2(x, y)
            );
        }
        public ArrayList<String> getImuName() {
            return getImuNameByPosition(this.position);
        }
    }

    public ArrayList<ArrayList<Entry>> getImuEntryList() {
        ArrayList<ArrayList<Entry>> entryList = new ArrayList<>();
        for(int i = 0; i < IMUFloat.IMU_LENGTH; i++) {
            entryList.add(new ArrayList<>());
        }
        if(feet.size() == 0) { return entryList; }
        for(int i = 0; i < IMUFloat.IMU_LENGTH; i++) {
            for (Foot foot:feet) {
                entryList.get(i).add(new Entry(entryList.get(i).size()-1, foot.imuFloat[i]));
//                Log.d(String.valueOf(entryList.size()-1) + ": " + String.valueOf(entryList.get(entryList.size()-1).size()));
            }
        }
//        Log.d("ALL: " + String.valueOf(entryList.size()));
        return entryList;
    }
    public static ArrayList<String> getAllImuName() {
        ArrayList<String> strings = new ArrayList<>();
        for (byte p:Position.ALL_POSITION) {
            strings.addAll(getImuNameByPosition(p));
        }
        return strings;
    }
    public static ArrayList<ArrayList<String>> getAllImuNameList() {
        ArrayList<ArrayList<String>> strings = new ArrayList<>();
        for (byte p:Position.ALL_POSITION) {
            strings.add(getImuNameByPosition(p));
        }
        return strings;
    }
    public static ArrayList<String> getImuNameByPosition(byte position) {
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
        for (String vice:BasicResourceManager.getResources().getStringArray(R.array.FootIMUDataLabels)) {
            strings.add(BasicResourceManager.getResources().getStringArray(R.array.Foot)[index] + " - " + vice);
        }
        return strings;
    }

    public void removeFootByIndex(int i) {
        feet.remove(i);
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

    public FootLabelData(FootDeviceData FootDeviceData, String LabelName, FootManagerData manager) {
        super(FootDeviceData);
//        labelName = LabelName;
        labelName = "abc";
        this.manager = manager;
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
        feet.add(CreateNewFootByBytes(bytes));
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
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                    }
                } catch (Exception e) {}
            }
        }).start();
        return true;
    }
}
