package com.example.mackayirb.ui.main;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.mackayirb.ui.base.BaseViewpagerAdapter;

public class MainViewpagerAdapter extends BaseViewpagerAdapter {

    public MainViewpagerAdapter(FragmentManager fm, String[] Titles, Fragment[] Fragments) {
        super(fm, Titles, Fragments);
        // getViewpagerAdapterComponent().inject(this);
    }

}