package com.example.mackayirb.injector.module;

import androidx.fragment.app.FragmentStatePagerAdapter;

import dagger.Module;

/**
 *
 */
@Module
public class ViewpagerAdapterModule {

    private FragmentStatePagerAdapter mFragmentStatePagerAdapter;

    public ViewpagerAdapterModule(FragmentStatePagerAdapter fragmentStatePagerAdapter) {
        mFragmentStatePagerAdapter = fragmentStatePagerAdapter;
    }

}
