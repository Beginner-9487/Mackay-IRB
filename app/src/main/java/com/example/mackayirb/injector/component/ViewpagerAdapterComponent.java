package com.example.mackayirb.injector.component;

import com.example.mackayirb.injector.PerViewpagerAdapter;
import com.example.mackayirb.injector.module.ViewpagerAdapterModule;
import com.example.mackayirb.ui.main.MainViewpagerAdapter;

import dagger.Component;

/**
 *
 */
@PerViewpagerAdapter
@Component(dependencies = ApplicationComponent.class, modules = ViewpagerAdapterModule.class)
public interface ViewpagerAdapterComponent {
    void inject(MainViewpagerAdapter viewpagerAdapter);
}