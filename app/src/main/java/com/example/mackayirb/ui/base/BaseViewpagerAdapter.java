package com.example.mackayirb.ui.base;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.mackayirb.utils.BasicResourceManager;

import java.util.ArrayList;
import java.util.Arrays;

// Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
public class BaseViewpagerAdapter extends FragmentStatePagerAdapter {

    // http://uirate.net/?p=10958

    //2.宣告變數為mFragments
    private ArrayList<String> mTitles;
    private ArrayList<Fragment> mFragments;
    FragmentManager fragmentManager;

    //3.初始化
    public BaseViewpagerAdapter(FragmentManager fm, String[] Titles, Fragment[] Fragments) {
        super(fm);
        fragmentManager = fm;
        mTitles = new ArrayList<>(Arrays.asList(Titles));
        mFragments = new ArrayList<>(Arrays.asList(Fragments));
    }

    //4.分頁內容
    @Override
    public Fragment getItem(int position) { return mFragments.get(position); }

    //5.分頁數量
    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    public void clear() {
        // Clear all fragments from the FragmentManager
        for (Fragment fragment : fragmentManager.getFragments()) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        // Notify the adapter of the dataset change
        mFragments.clear();
        BasicResourceManager.removeCurrentFragment();
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Call super method to detach the fragment
        super.destroyItem(container, position, object);

        // Remove the reference to the old fragment
//        mFragments.remove(position);
    }

}