package com.example.mackayirb.injector.module;

import android.app.Activity;

import com.example.mackayirb.data.ble.DataManager;
import com.example.mackayirb.data.central.FootDataManager;
import com.example.mackayirb.data.central.MackayDataManager;
import com.example.mackayirb.ui.central.CentralPresenter;
import com.example.mackayirb.ui.main.MainPresenter;

import dagger.Module;
import dagger.Provides;

/**
 *
 */
@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    public MainPresenter provideMainPresenter() {
        return new MainPresenter();
    }

    @Provides
    public CentralPresenter provideCentralPresenter(DataManager dataManager, MackayDataManager mackayDataManager, FootDataManager footDataManager) {
        return new CentralPresenter(dataManager, mackayDataManager, footDataManager);
    }
}
