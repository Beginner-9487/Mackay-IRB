package com.example.mackayirb;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.core.content.ContextCompat;

import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.injector.component.ApplicationComponent;
import com.example.mackayirb.injector.component.DaggerApplicationComponent;
import com.example.mackayirb.injector.module.ApplicationModule;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;

/**
 * Created by jacobsu on 4/21/16.
 */
public class BLEApplication extends Application {

    private ApplicationComponent mApplicationComponent;

    SharedPreferences mSharedPreference;

    private void startBluetoothService() {
        if(BasicResourceManager.isTesting) {return;}

        Intent serviceIntent = new Intent(this, BLEDataServer.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        Log.d("startBluetoothService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        BasicResourceManager.setBasic(getResources(), mSharedPreference);
        Log.d("onCreate");
        startBluetoothService();
    }

    public ApplicationComponent getApplicationComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }

        return mApplicationComponent;
    }
}
