package com.example.mackayirb.data.central;

import com.example.mackayirb.R;
import com.example.mackayirb.utils.BasicResourceManager;
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

    public static class Position {
        public static final byte LEFT_FOOT = 0x0a;
        public static final byte RIGHT_FOOT = 0x0b;
    }
    public static class MainFloatList {
        public static final byte ShearForce = 0;
        public static final byte Pressures = 1;
        public static final byte Temperatures = 2;
        public static final byte Average = 3;
    }
    public static class ViceFloat {
        public static final byte AccX = 0;
        public static final byte AccY = 1;
        public static final byte AccZ = 2;
        public static final byte ClockTime = 3;
        public static final byte COP_X = 4;
        public static final byte COP_Y = 5;
        public static final byte Roll_H = 6;
        public static final byte Roll_L = 7;
        public static final byte Pitch_H = 8;
        public static final byte Pitch_L = 9;
        public static final byte Yaw_H = 10;
        public static final byte Yaw_L = 11;
        public static final byte GyroX_H = 12;
        public static final byte GyroX_L = 13;
        public static final byte GyroY_H = 14;
        public static final byte GyroY_L = 15;
        public static final byte GyroZ_H = 16;
        public static final byte GyroZ_L = 17;

        public static final byte[] Triple = new byte[]{ClockTime};
    }
    private ArrayList<Foot> feet = new ArrayList<>();
    public class Foot {
        public byte position;
        public ArrayList<float[]> mainFloatList = new ArrayList<>();
        public Entry[] mainCenterEntry = new Entry[MainFloatList.Average];
        public float[] viceFloat = new float[ViceFloat.GyroZ_L];
        public Foot(byte[] bytes) {

            OtherUsefulFunction.ByteIterator data = new OtherUsefulFunction.ByteIterator(bytes);

            position = data.next();

            for(int m = 0; m< MainFloatList.Average; m++) {
                mainFloatList.add(new float[getNumberOfSensor()]);
            }
            mainFloatList.add(new float[MainFloatList.Average]);

            // mainFloatList, mainEntry
            for(int m = 0; m< MainFloatList.Average; m++) {
                mainCenterEntry[m] = new Entry(0f, 0f);
                for (int i=0; i<getNumberOfSensor(); i++) {
                    mainFloatList.get(m)[i] = (float) OtherUsefulFunction.byteArrayToSignedInt(
                            data.array(2)
                    );
                    mainFloatList.get(MainFloatList.Average)[m] += mainFloatList.get(m)[i];
                    Entry xy = getSensorList().get(i);
                    mainCenterEntry[m].setX(mainCenterEntry[m].getX() + xy.getX() * mainFloatList.get(m)[i]);
                    mainCenterEntry[m].setY(mainCenterEntry[m].getY() + xy.getY() * mainFloatList.get(m)[i]);
                }
                mainCenterEntry[m].setX(mainCenterEntry[m].getX() / mainFloatList.get(MainFloatList.Average)[m]);
                mainCenterEntry[m].setY(mainCenterEntry[m].getY() / mainFloatList.get(MainFloatList.Average)[m]);
                mainFloatList.get(MainFloatList.Average)[m] /= getNumberOfSensor();
            }

            // viceFloat
            viceFloat = new float[ViceFloat.GyroZ_L];
            for (int i=0; i<ViceFloat.GyroZ_L; i++) {
                byte lock = 2;
                if(Arrays.asList(ViceFloat.Triple).contains(i)) { lock = (byte) 3; }
                viceFloat[i] = (float) OtherUsefulFunction.byteArrayToSignedInt(
                        data.array(lock)
                );
            }
        }
        public ArrayList<Entry> getSensorList() {
            return SensorPositions.getPositions(this.position);
        }
        public int getNumberOfSensor() {
            return 31;
        }
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
                case Position.LEFT_FOOT:
                    return Foot_Left;
                case Position.RIGHT_FOOT:
                    return Foot_Right;
            }
            return Foot_Left;
        }
        public static ArrayList<Entry> Foot_Left = new ArrayList<>(Arrays.asList(
                new Entry(165.0f, 1716.0f),
                new Entry(294.25f, 1710.5f),
                new Entry(167.75f, 1564.75f),
                new Entry(297.0f, 1559.25f),
                new Entry(173.25f, 1413.5f),
                new Entry(299.75f, 1408.0f),
                new Entry(176.0f, 1278.75f),
                new Entry(302.5f, 1276.0f),
                new Entry(178.75f, 1078.0f),
                new Entry(305.25f, 1075.25f),
                new Entry(385.0f, 965.25f),
                new Entry(132.0f, 965.25f),
                new Entry(434.5f, 825.0f),
                new Entry(280.5f, 822.25f),
                new Entry(129.25f, 816.75f),
                new Entry(486.75f, 695.75f),
                new Entry(365.75f, 690.25f),
                new Entry(220.0f, 684.75f),
                new Entry(88.0f, 679.25f),
                new Entry(506.0f, 561.0f),
                new Entry(382.25f, 552.75f),
                new Entry(247.5f, 547.25f),
                new Entry(123.75f, 539.0f),
                new Entry(506.0f, 434.5f),
                new Entry(343.75f, 420.75f),
                new Entry(178.75f, 412.5f),
                new Entry(508.75f, 310.75f),
                new Entry(360.25f, 297.0f),
                new Entry(211.75f, 286.0f),
                new Entry(445.5f, 198.0f),
                new Entry(313.5f, 192.5f)
        ));
        public static ArrayList<Entry> Foot_Right = new ArrayList<>(Arrays.asList(
                new Entry(1080.75f, 1716.0f),
                new Entry(951.5f, 1710.5f),
                new Entry(1078.0f, 1564.75f),
                new Entry(948.75f, 1559.25f),
                new Entry(1072.5f, 1413.5f),
                new Entry(946.0f, 1408.0f),
                new Entry(1069.75f, 1278.75f),
                new Entry(943.25f, 1276.0f),
                new Entry(1067.0f, 1078.0f),
                new Entry(940.5f, 1075.25f),
                new Entry(1113.75f, 965.25f),
                new Entry(860.75f, 965.25f),
                new Entry(811.25f, 825.0f),
                new Entry(965.25f, 822.25f),
                new Entry(1116.5f, 816.75f),
                new Entry(759.0f, 695.75f),
                new Entry(880.0f, 690.25f),
                new Entry(1025.75f, 684.75f),
                new Entry(1157.75f, 679.25f),
                new Entry(739.75f, 561.0f),
                new Entry(863.5f, 552.75f),
                new Entry(998.25f, 547.25f),
                new Entry(1122.0f, 539.0f),
                new Entry(739.75f, 434.5f),
                new Entry(902.0f, 420.75f),
                new Entry(1067.0f, 412.5f),
                new Entry(737.0f, 310.75f),
                new Entry(885.5f, 297.0f),
                new Entry(1034.0f, 286.0f),
                new Entry(800.25f, 198.0f),
                new Entry(932.25f, 192.5f)
        ));
    }

}
