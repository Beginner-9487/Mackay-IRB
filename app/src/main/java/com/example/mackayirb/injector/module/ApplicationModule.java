package com.example.mackayirb.injector.module;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;

import androidx.annotation.NonNull;

import com.example.mackayirb.injector.ApplicationContext;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module
public class ApplicationModule {
    private Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    public Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    public Context provideContext() {
        return mApplication;
    }

    @Provides
    public BluetoothManager provideBluetoothManager() {
        return (BluetoothManager) mApplication.getSystemService(Context.BLUETOOTH_SERVICE);
    }

    @Provides
    public BluetoothAdapter provideBluetoothAdapter(@NonNull BluetoothManager btmanager) {
        return btmanager.getAdapter();
    }

    @Provides
    public BluetoothLeScanner provideBluetoothLeScanner(@NonNull BluetoothAdapter btAdapter) {
        return btAdapter.getBluetoothLeScanner();
    }

    @Provides
    @Singleton
    public AdvertiseSettings.Builder provideAdvertiseSettingsBuilder() {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        builder.setConnectable(true);
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);

        return builder;
    }

    @Provides
    @Singleton
    public AdvertiseData.Builder provideAdvertiseDataBuilder() {
        AdvertiseData.Builder builder = new AdvertiseData.Builder();
        builder.setIncludeDeviceName(true);
        builder.setIncludeTxPowerLevel(true);

        return builder;
    }
}
