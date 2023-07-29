package com.example.mackayirb.data.central;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

public class MackayLabelData extends CentralLabelData<MackayDeviceData> {

    private static int getDataTypeID() {
        return R.array.TypeLabels;
    }
    public static String[] getDataTypes() {
        return BasicResourceManager.getResources().getStringArray(getDataTypeID());
    }

    public static final byte XLabel = 0x00;
    public static final byte YLabel = 0x01;
    public static final byte SpecialLabel = 0x02;
    public static final byte TypeLabel = 0x03;
    public static final byte XUnit = 0x04;
    public static final byte YUnit = 0x05;

    public String labelName;
    public boolean show = true;
    public byte levelOfDownload = 0;
    public int type = -1;
    public int numberOfData = 0;
    public float xPrecision;
    public float yPrecision;

    public HashMap<Integer, Entry> data;

    public Entry CreateNewEntryByBytes(byte[] bytesVoltageInteger, byte[] bytesVoltageDecimal, byte[] bytesCurrentInteger, byte[] bytesCurrentDecimal) {
        return new Entry(
                OtherUsefulFunction.byteArrayToSignedInt(bytesVoltageInteger) + OtherUsefulFunction.byteArrayToSignedInt(bytesVoltageDecimal) / xPrecision,
                OtherUsefulFunction.byteArrayToSignedInt(bytesCurrentInteger) + OtherUsefulFunction.byteArrayToSignedInt(bytesCurrentDecimal) / yPrecision
        );
    }

    public MackayLabelData(MackayDeviceData mackayDeviceData, String LabelName, boolean Show, byte LevelOfDownload) {
        super(mackayDeviceData);
        labelName = LabelName;
        data = new HashMap<>();
        show = Show;
        levelOfDownload = LevelOfDownload;
    }
    public MackayLabelData(MackayDeviceData mackayDeviceData, String LabelName) {
        super(mackayDeviceData);
        labelName = LabelName;
        data = new HashMap<>();
    }
    public MackayLabelData(MackayDeviceData device) {
        super(device);
    }

    @Override
    public byte addNewData(byte[] bytes) {
//        Log.e(OtherUsefulFunction.byteArrayToHexString(bytes, ", "));
        if(bytes == null) {
            // Log.e("Data is null.");
            return 0x00;
        }
        if (type == -1) {
            type = OtherUsefulFunction.byteArrayToSignedInt(new byte[]{bytes[0]});
            xPrecision = 1000.0f;
            yPrecision = 1000000.0f;
        } else if (type != OtherUsefulFunction.byteArrayToSignedInt(new byte[]{bytes[0]})) {
            Log.e("It is a different Type of Data!");
            return 0x00;
        }

        if (numberOfData == 0) {
            numberOfData = OtherUsefulFunction.byteArrayToSignedInt(new byte[]{bytes[1], bytes[2]});
        } else if (numberOfData != OtherUsefulFunction.byteArrayToSignedInt(new byte[]{bytes[1], bytes[2]})) {
            Log.e("It is a different Number of Data!");
            return 0x00;
        }
//             Log.e("Chart: Size: " + labelName + ": " + String.valueOf(data.values().size()) + ": " + OtherUsefulFunction.byteArrayToHexString(bytes, ", "));
        data.put(
                OtherUsefulFunction.byteArrayToSignedInt(new byte[]{bytes[3], bytes[4]}),
                CreateNewEntryByBytes(
                        new byte[]{bytes[5], bytes[6]},
                        new byte[]{bytes[7], bytes[8]},
                        new byte[]{bytes[9], bytes[10]},
                        new byte[]{bytes[11], bytes[12], bytes[13], bytes[14]}
                )
        );

        // Final data.
        if (levelOfDownload == 0x00 && numberOfData == OtherUsefulFunction.byteArrayToSignedInt(new byte[]{bytes[3], bytes[4]})) {
            levelOfDownload = 0x01;
            saveNewFile();
            getDevice().editDeviceDataFile(this);
            return 0x02;
        }

        return 0x01;
    }

    public String getRelatedUnit(byte target) {
        int idTargetArray;
        int idDefault;
        switch (target) {
            case XLabel:
                idTargetArray = R.array.XLabels;
                idDefault = R.string.DefaultXLabel;
                break;
            case YLabel:
                idTargetArray = R.array.YLabels;
                idDefault = R.string.DefaultYLabel;
                break;
            case SpecialLabel:
                idTargetArray = R.array.SpecialLabels;
                idDefault = R.string.DefaultYLabel;
                break;
            case TypeLabel:
                idTargetArray = R.array.TypeLabels;
                idDefault = R.string.DefaultTypeLabel;
                break;
            case XUnit:
                idTargetArray = R.array.XUnits;
                idDefault = R.string.DefaultXUnit;
                break;
            case YUnit:
                idTargetArray = R.array.YUnits;
                idDefault = R.string.DefaultYUnit;
                break;
            default:
                return "";
        }
        String s = BasicResourceManager.getResources().getStringArray(idTargetArray)[this.type];
        return (s.equals("")) ? BasicResourceManager.getResources().getString(idDefault) : s;
    }

    public void addNewDataList(ArrayList<byte[]> bs) {
        for (byte[] b:bs) {
            addNewData(b);
        }
    }

    // =====================================================================================
    // =====================================================================================

    public ArrayList<Float> getYByX(float x) {
        ArrayList<Float> yList = new ArrayList<>();
        Entry lowerX = null;
        Entry greaterX = null;
        for (Entry xy : data.values()) {
            byte GES = 0x00;
            if (xy.getX() <= x) {
                lowerX = new Entry(xy.getX(), xy.getY());
                GES += 1;
            }
            if (xy.getX() >= x) {
                greaterX = new Entry(xy.getX(), xy.getY());
                GES += 2;
            }
            if (lowerX != null && greaterX != null) {
                yList.add((greaterX.getX() == lowerX.getX()) ? lowerX.getY() : (lowerX.getY() + (x - lowerX.getX()) * (greaterX.getY() - lowerX.getY()) / (greaterX.getX() - lowerX.getX())));
                if ((GES & 0x1) > 0) {
                    greaterX = null;
                }
                if ((GES & 0x2) > 0) {
                    lowerX = null;
                }
            }
        }
        return yList;
    }

    public float YtoSpecial(float y) {
        switch (type) {
            case 4:
                return y + 0;
            case 5:
                return y + 1 - 1;
            default:
                return y;
        }
    }

    public ArrayList<Float> getSpecialByX(float x) {
        ArrayList<Float> arrayList = new ArrayList<>();
        for (float y : getYByX(x)) {
            arrayList.add(YtoSpecial(y));
        }
        return arrayList;
    }

    public ArrayList<Entry> getSpecialEntries() {
        ArrayList<Entry> arrayList = new ArrayList<>();
        for (Entry xy : data.values()) {
            // Log.d("X: " + String.valueOf(xy.getX()));
            // Log.d("Y: " + String.valueOf(xy.getY()));
            // Log.d("T: " + String.valueOf(type));
            // Log.d("S: " + String.valueOf(String.valueOf(YtoSpecial(xy.getY()))));
            arrayList.add(new Entry(xy.getX(), YtoSpecial(xy.getY())));
        }
        return arrayList;
    }

    public String getAllEntryString() {
        String string = "";
        for (Map.Entry<Integer, Entry> v : data.entrySet()) {
            string += v.getKey() + "; " + v.getValue().toString() + " Special: " + String.valueOf(YtoSpecial(v.getValue().getY())) + "\n";
        }
        return string;
    }

    public HashMap<Integer, ArrayList<Float>> getAllXYSpecial() {
        HashMap<Integer, ArrayList<Float>> hashMap = new HashMap<>();
        for (Map.Entry<Integer, Entry> data : data.entrySet()) {
            ArrayList<Float> d = new ArrayList<Float>();
            d.add(data.getValue().getX());
            d.add(data.getValue().getY());
            d.add(YtoSpecial(data.getValue().getY()));
            hashMap.put(data.getKey(), d);
        }
        return hashMap;
    }

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
        // Log.i("saveMyFile: " + labelName);
        try {
            markAsDownloaded();

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH-mm-ss");
            String currentTime = sdf.format(calendar.getTime());

            // Log.i(labelName);
            MyExcelFile file = new MyExcelFile();
            String sdCardPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator;
            file.createExcelWorkbook(sdCardPath + labelName + ".xls");
            file.create_new_sheet(labelName);

            // Add value in the cell
            // Log.i(getRelatedUnit(SpecialLabel));
            int rowIndex = 0;
            file.write_file(0, rowIndex, 0, BasicResourceManager.getResources().getString(R.string.LabelName) + ": " + labelName);
            rowIndex++;
            file.write_file(0, rowIndex, 0, BasicResourceManager.getResources().getString(R.string.SaveFileTime) + ": " + currentTime);
            rowIndex++;
            file.write_file(0, rowIndex, 0, BasicResourceManager.getResources().getString(R.string.Type) + ": " + getRelatedUnit(TypeLabel) + " (" + String.valueOf(type) + ")");
            rowIndex++;
            file.write_file(0, rowIndex, 0, BasicResourceManager.getResources().getString(R.string.Number));
            file.write_file(0, rowIndex, 1, getRelatedUnit(XLabel));
            file.write_file(0, rowIndex, 2, getRelatedUnit(YLabel));
            file.write_file(0, rowIndex, 3, getRelatedUnit(SpecialLabel));

            // Log.i(String.valueOf(getAllXYSpecial().entrySet().size()));
            for (Map.Entry<Integer, ArrayList<Float>> data:getAllXYSpecial().entrySet()) {
                file.write_file(0, (int) (rowIndex+1+data.getKey()), 0, String.valueOf(data.getKey()));
                file.write_file(0, rowIndex+1+data.getKey(), 1, String.valueOf(data.getValue().get(0)));
                file.write_file(0, rowIndex+1+data.getKey(), 2, String.valueOf(data.getValue().get(1)));
                file.write_file(0, rowIndex+1+data.getKey(), 3, String.valueOf(data.getValue().get(2)));
            }

            // Log.d(file.toString());

            // Save as Excel XLSX file
            if (file.exportDataIntoWorkbook()) {
                // Log.i(BasicResourceManager.getResources().getString(R.string.Temp_UI_save_toast));
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Toast.makeText(BasicResourceManager.getCurrentActivity(), labelName + ": " + BasicResourceManager.getResources().getString(R.string.Temp_UI_save_toast), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {}
                    }
                });
                return true;
            }
        } catch (Exception e) {}
        return false;
    }

}
