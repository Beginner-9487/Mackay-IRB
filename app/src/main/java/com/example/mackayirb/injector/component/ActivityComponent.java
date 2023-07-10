package com.example.mackayirb.injector.component;

import com.example.mackayirb.injector.PerActivity;
import com.example.mackayirb.injector.module.ActivityModule;
import com.example.mackayirb.ui.central.CentralDetailsActivity;
import com.example.mackayirb.ui.main.MainActivity;

import dagger.Component;

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(CentralDetailsActivity activity);
}
