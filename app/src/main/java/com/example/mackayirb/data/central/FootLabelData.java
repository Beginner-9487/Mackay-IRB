package com.example.mackayirb.data.central;

import android.widget.Toast;

import com.example.mackayirb.R;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Arrays;

public class FootLabelData extends CentralLabelData<FootDeviceData> {

    private static int getDataTypeID() {
        return R.array.TypeLabels;
    }
    public static String[] getDataTypes() {
        return BasicResourceManager.getResources().getStringArray(getDataTypeID());
    }

    public String labelName;
    public boolean show = true;
    public byte levelOfDownload = 0;
    public int type = -1;

    private static final byte numberOfSensor = 17;
//    private static final byte numberOfSensor = 12;

    private ArrayList<Foot> feet = new ArrayList<>();
    public class Foot {
        public class Position {
//            public static final byte LEFT_FOOT = 0x04;
//            public static final byte RIGHT_FOOT = 0x05;
            public static final byte LEFT_FOOT = 0x0a;
            public static final byte RIGHT_FOOT = 0x0b;
        }
        public class OtherInformation {
            public static final byte AccX = 0x00;
            public static final byte AccY = 0x01;
            public static final byte AccZ = 0x02;
            public static final byte ClockTime = 0x03;
            public static final byte COP_X = 0x04;
            public static final byte COP_Y = 0x05;
            public static final byte Roll_H = 0x06;
            public static final byte Roll_L = 0x07;
            public static final byte Pitch_H = 0x08;
            public static final byte Pitch_L = 0x09;
            public static final byte Yaw_H = 0x0a;
            public static final byte Yaw_L = 0x0b;
            public static final byte GyroX_H = 0x0c;
            public static final byte GyroX_L = 0x0d;
            public static final byte GyroY_H = 0x0e;
            public static final byte GyroY_L = 0x0f;
            public static final byte GyroZ_H = 0x10;
            public static final byte GyroZ_L = 0x11;
        }
        public byte position;
        public ArrayList<Float> pressures = new ArrayList<>();
        public float pressureAverage = 0.0f;
        public Entry pressureCenter = new Entry(0.0f, 0.0f);
        public ArrayList<Float> otherInformation = new ArrayList<>();
        public Foot(byte[] bytes) {
//            Toast.makeText(BasicResourceManager.getCurrentFragment().getContext(),
//                    String.valueOf(bytes.length) + "," +
//                            String.valueOf(bytes[0])
//                    , Toast.LENGTH_SHORT).show();

            int index = 0;

//            Log.d("position");
            position = bytes[index];
            index++;

//            Log.d("pressures");
            for (int i=0; i<numberOfSensor; i++) {
                pressures.add(
                        (float) OtherUsefulFunction.byteArrayToSignedInt(
                                new byte[] {
                                        bytes[index],
                                        bytes[index+1]
                                }
                        )
                );
                pressureAverage += pressures.get(pressures.size()-1);
                Entry xy = SensorPositions.getPositions(position).get(i);
                pressureCenter.setX(pressureCenter.getX() + xy.getX() * pressures.get(pressures.size()-1));
                pressureCenter.setY(pressureCenter.getY() + xy.getY() * pressures.get(pressures.size()-1));
                index += 2;
            }
            pressureAverage /= pressures.size();
            pressureCenter.setX(pressureCenter.getX() / pressureAverage);
            pressureCenter.setY(pressureCenter.getY() / pressureAverage);
//            pressureCenter.setX((float) (Math.random() * 600f));
//            pressureCenter.setY((float) (Math.random() * 1500f));

//            Log.d("otherInformation");
            for (int i=0; i<3; i++) {
                otherInformation.add((float) OtherUsefulFunction.byteArrayToSignedInt(new byte[]{bytes[index], bytes[index+1]}));
                index += 2;
            }
            otherInformation.add(OtherUsefulFunction.byteArrayToSignedInt(new byte[]{bytes[index], bytes[index+1], bytes[index+2]}) / 1000.f);
            index += 3;
            for (int i=0; i<14; i++) {
                otherInformation.add((float) bytes[index]);
                index++;
            }

//            Log.d("finish");
        }
    }

    public ArrayList<Foot> getFeet() {
        return feet;
    }
    public ArrayList<ArrayList<Foot>> getFeetSortedByPosition() {
        ArrayList<ArrayList<Foot>> sortedFeet = new ArrayList<>();
        for (Foot foot:feet) {
            if(sortedFeet.size() == 0) {
                sortedFeet.add(new ArrayList<>());
                sortedFeet.get(sortedFeet.size()-1).add(foot);
            } else {
                boolean lock = true;
                for (ArrayList<Foot> f:sortedFeet) {
                    if(foot.position == f.get(0).position) {
                        f.add(foot);
                        lock = false;
                        break;
                    }
                }
                if (lock) {
                    sortedFeet.add(new ArrayList<>());
                    sortedFeet.get(sortedFeet.size()-1).add(foot);
                }
            }
        }
        return sortedFeet;
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
        show = Show;
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
        return false;
    }

    public static class SensorPositions {
        public static ArrayList<Entry> getPositions(byte position) {
            switch (position) {
                case Foot.Position.LEFT_FOOT:
                    return Foot_Left;
                case Foot.Position.RIGHT_FOOT:
                    return Foot_Right;
            }
            return Foot_Left;
        }
        public static ArrayList<Entry> Foot_Left = new ArrayList<>(Arrays.asList(
                new Entry(336.42857142857144f, 134.64285714285714f),
                new Entry(225.35714285714286f, 134.64285714285714f),
                new Entry(133.57142857142858f, 210.35714285714286f),
                new Entry(317.5f, 310.0f),
                new Entry(212.85714285714286f, 310.0f),
                new Entry(113.57142857142858f, 342.8571428571429f),
                new Entry(303.5714285714286f, 501.42857142857144f),
                new Entry(210.35714285714286f, 501.42857142857144f),
                new Entry(109.64285714285715f, 501.42857142857144f),
                new Entry(233.21428571428572f, 689.2857142857143f),
                new Entry(103.21428571428572f, 699.2857142857142f),
                new Entry(226.7857142857143f, 900.7142857142858f),
                new Entry(114.64285714285715f, 856.7857142857143f),
                new Entry(226.7857142857143f, 1017.8571428571429f),
                new Entry(114.64285714285715f, 1017.8571428571429f),
                new Entry(226.7857142857143f, 1172.8571428571431f),
                new Entry(114.64285714285715f, 1172.8571428571431f)
        ));
        public static ArrayList<Entry> Foot_Right = new ArrayList<>(Arrays.asList(
                new Entry(535.7142857142858f, 134.64285714285714f),
                new Entry(646.7857142857144f, 134.64285714285714f),
                new Entry(738.5714285714287f, 210.35714285714286f),
                new Entry(554.6428571428572f, 310.0f),
                new Entry(659.2857142857143f, 310.0f),
                new Entry(758.5714285714287f, 342.8571428571429f),
                new Entry(568.5714285714287f, 501.42857142857144f),
                new Entry(661.7857142857143f, 501.42857142857144f),
                new Entry(762.5f, 501.42857142857144f),
                new Entry(638.9285714285714f, 689.2857142857143f),
                new Entry(768.9285714285714f, 699.2857142857142f),
                new Entry(645.3571428571429f, 900.7142857142858f),
                new Entry(757.5f, 856.7857142857143f),
                new Entry(645.3571428571429f, 1017.8571428571429f),
                new Entry(757.5f, 1017.8571428571429f),
                new Entry(645.3571428571429f, 1172.8571428571431f),
                new Entry(757.5f, 1172.8571428571431f)
        ));
    }

}
