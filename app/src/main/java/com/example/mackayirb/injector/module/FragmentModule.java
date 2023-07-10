package com.example.mackayirb.injector.module;

import androidx.fragment.app.Fragment;

import dagger.Module;

/**
 *
 */
@Module
public class FragmentModule {

    private Fragment mFragment;

    public FragmentModule(Fragment fragment) {
        mFragment = fragment;
    }

}
