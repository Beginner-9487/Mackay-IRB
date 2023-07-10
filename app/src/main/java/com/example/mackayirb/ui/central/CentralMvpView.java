package com.example.mackayirb.ui.central;

import android.bluetooth.BluetoothDevice;

import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.ui.base.MvpView;

/**
 *
 */
public interface CentralMvpView extends MvpView {
    void showBLEDevice(BluetoothDevice bt);
    void showBLEData(BLEDataServer.BLEData data);
}
