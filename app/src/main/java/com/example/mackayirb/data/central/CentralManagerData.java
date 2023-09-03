package com.example.mackayirb.data.central;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.data.ble.DataManager;
import com.example.mackayirb.utils.Log;

import java.util.ArrayList;

import javax.inject.Singleton;

@Singleton
public abstract class CentralManagerData<Manager extends CentralManagerData, Device extends CentralDeviceData, Label extends CentralLabelData> {

    public ArrayList<Device> deviceData = new ArrayList<>();

    public CentralManagerData() {}

    public void setup(DataManager dataManager) {
        if(this.dataManager == null) {
            this.dataManager = dataManager;
            setUpHandler();
            startReadValues();
        }
        Log.d("setup");
    }

    private DataManager dataManager;

    private static final byte READ_VALUES_REPEAT = 0x00;
    private final long READING_VALUES_TASK_FREQUENCY = 50;

    private Handler mHandler;
    private void setUpHandler() {
        if(Looper.myLooper() != null) {
            mHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case READ_VALUES_REPEAT:
                            for(BLEDataServer.BLEData bleData: dataManager.getRemoteBLEData()) {
                                updateLabelData(bleData);
                            }

                            sendMessageDelayed(
                                    obtainMessage(READ_VALUES_REPEAT),
                                    READING_VALUES_TASK_FREQUENCY
                            );
                            break;
                    }
                }
            };
        }
    }

    public void startReadValues() {
        if (mHandler.hasMessages(READ_VALUES_REPEAT)) {
            return;
        }

        mHandler.sendEmptyMessage(READ_VALUES_REPEAT);
    }
    public void stopReadValues() {
        mHandler.removeMessages(READ_VALUES_REPEAT);
    }

    public abstract void updateLabelData(BLEDataServer.BLEData bleData);

    synchronized public Device findDeviceDataByBle(BLEDataServer.BLEData bleData) {
        int targetIndex = -1;
        for (int i = 0; i< deviceData.size(); i++) {
            if(((CentralDeviceData) (deviceData.get(i))).bleData.equals(bleData)) {
                targetIndex = i;
            }
        }
//        Log.d(String.valueOf(targetIndex) + ": " + String.valueOf(deviceData.size()));
        Device device;
        if(targetIndex == -1) {
            device = createDeviceData(bleData, (Manager) this);
            if(device != null) {
                deviceData.add(device);
                targetIndex = deviceData.size() - 1;
                device = deviceData.get(targetIndex);
            }
        } else {
            device = deviceData.get(targetIndex);
        }
        return device;
    }

    public abstract Label findLabelDataByBleAndObject(BLEDataServer.BLEData bleData, byte type, Object object);

    public Device findDeviceDataByLabelData(Label centralLabelData) {
        for (Device d:deviceData) {
            for (Label data:((CentralDeviceData<Manager, Label>) d).labelData) {
                if(data.equals(centralLabelData)) {
                    return d;
                }
            }
        }
        return null;
    }

    public abstract Device createDeviceData(BLEDataServer.BLEData bleData, Manager manager);
    public abstract Label createLabelData(BLEDataServer.BLEData bleData, Device targetDevice);

    public ArrayList<Device> getDeviceData() {
        return deviceData;
    }

    public abstract boolean createManagerDataFile();

}
