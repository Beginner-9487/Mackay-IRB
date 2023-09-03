package com.example.mackayirb.injector.component;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;

import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.data.ble.BLEPeripheralServer;
import com.example.mackayirb.data.ble.DataManager;
import com.example.mackayirb.data.central.FootManagerData;
import com.example.mackayirb.data.central.MackayManagerData;
import com.example.mackayirb.injector.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 *
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

//    void inject(BluetoothLeService service);
    void inject(BLEPeripheralServer server);

    BluetoothManager bluetoothManager();
    BluetoothAdapter bluetoothAdapter();
    BluetoothLeScanner bluetoothLeScanner();

    DataManager dataManager();
    BLEDataServer bleDataServer();

//    CentralDataManager centralDataManager();
    MackayManagerData mackayDataManager();
    FootManagerData footDataManager();
}
