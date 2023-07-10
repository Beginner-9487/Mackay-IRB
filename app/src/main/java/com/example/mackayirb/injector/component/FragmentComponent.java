package com.example.mackayirb.injector.component;

import com.example.mackayirb.injector.PerFragment;
import com.example.mackayirb.injector.module.FragmentModule;
import com.example.mackayirb.ui.central.CentralFragment;

import dagger.Component;

/**
 *
 */
@PerFragment
@Component(dependencies = ApplicationComponent.class, modules = FragmentModule.class)
public interface FragmentComponent {
    void inject(CentralFragment fragment);
}
