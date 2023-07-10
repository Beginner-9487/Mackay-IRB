package com.example.mackayirb.ui.base;

import androidx.fragment.app.Fragment;

import com.example.mackayirb.BLEApplication;
import com.example.mackayirb.injector.component.DaggerFragmentComponent;
import com.example.mackayirb.injector.component.FragmentComponent;
import com.example.mackayirb.injector.module.FragmentModule;

/**
 *
 */
public class BaseFragment extends Fragment {
    private FragmentComponent mFragmentComponent;

    public FragmentComponent getFragmentComponent() {
        if (mFragmentComponent == null) {
            mFragmentComponent = DaggerFragmentComponent.builder()
                    .applicationComponent(((BLEApplication)getActivity().getApplication()).getApplicationComponent())
                    .fragmentModule(new FragmentModule(this))
                    .build();
        }

        return mFragmentComponent;
    }
}
