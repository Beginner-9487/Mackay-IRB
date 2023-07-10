package com.example.mackayirb.data.ble;

import android.bluetooth.BluetoothDevice;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

/**
 *
 */
@Singleton
public class DataManager {

    private BLEDataServer mBLEServer;
    public BLEDataServer getBLEServer() {
        return mBLEServer;
    }

    @Inject
    public DataManager(BLEDataServer bleServer) { mBLEServer = bleServer; }

    public void startCentralMode() {
        mBLEServer.startCentralMode();
    }

    public void stopCentralMode() {
        mBLEServer.stopCentralMode();
    }

    public List<BLEDataServer.BLEData> getRemoteBLEData() {
        return mBLEServer.getRemoteBLEData();
    }

    public Observable<BLEDataServer.BLEData> scanBLEPeripheral(boolean enabled) {
        return mBLEServer.scanBLEPeripheral(enabled);
    }

    public Observable<BLEDataServer.BLEData> connectGatt(BluetoothDevice device) {
        return mBLEServer.connectGatt(device);
    }

    public void disconnectGatt(BluetoothDevice device) {
        mBLEServer.disconnectGatt(device);
    }

    public void createBond(BluetoothDevice device) {
        mBLEServer.createBond(device);
    }

    public boolean readRemoteRssi(BluetoothDevice device) {
        return mBLEServer.readRemoteRssi(device);
    }

    public boolean supportAdvertiser() {
        return mBLEServer.supportLEAdvertiser();
    }

    public void startPeripheralMode(String name) {
        mBLEServer.startPeripheralMode(name);
    }

    public void stopPeripheralMode() {
        mBLEServer.stopPeripheralMode();
    }

    public List<BLEDataServer.BLEData> getAllBondedDevices() {
        return mBLEServer.getAllBondedDevices();
    }

    public byte[] getLastReceivedData(BluetoothDevice bluetoothDevice, String UUID) {
        return mBLEServer.getDeviceData(bluetoothDevice, UUID);
    }

    // =================================================================================================
    // Temp UI
    public void Send_All_C(byte[] command) {
        mBLEServer.Send_All_C(command);
    }

}