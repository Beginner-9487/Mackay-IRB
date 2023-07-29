package com.example.mackayirb.ui.central;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mackayirb.R;
import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.ui.base.BaseFragment;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;

import javax.inject.Inject;

public class CentralFragment extends BaseFragment implements CentralMvpView {

    @Inject
    CentralPresenter mCentralPresenter;
    public CentralPresenter getCentralPresenter() {
        return mCentralPresenter;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("onAttach: " + getClass().getSimpleName());
//        BasicResourceManager.setCurrentFragment(this);
        if(BasicResourceManager.Permissions.checkAllPermissions()) {
            init();
        }
    }
    public void init() {
        getFragmentComponent().inject(this);
        mCentralPresenter.attachView(this);
        mCentralPresenter.initForBLEData();
        mCentralPresenter.startHandler(CentralPresenter.READ_RSSI_REPEAT);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("onResume: " + getClass().getSimpleName());
//        BasicResourceManager.setCurrentFragment(this);
        startHandler();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("onStop: " + getClass().getSimpleName());
        // mCentralPresenter.detachView();
        // stopHandler();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy: " + getClass().getSimpleName());
//        BasicResourceManager.removeCurrentActivity(this.getActivity());
//        mCentralPresenter.detachView();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("onDetach: " + getClass().getSimpleName());
//        BasicResourceManager.removeCurrentFragment(this);
        mCentralPresenter.detachView();
    }

    public long getDoSomethingFrequency() {
        return 100;
    }
    private Handler mHandler;
    private void setUpHandler() {
        if(Looper.myLooper() != null) {
            mHandler = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    try {
                        doSomethingFrequently();
                    } catch (Exception e) {}
                    sendMessageDelayed(
                            obtainMessage(0),
                            getDoSomethingFrequency()
                    );
                }
            };
        }
    }
    public void startHandler() {
        if (mHandler.hasMessages(0)) {
            return;
        }
        mHandler.sendEmptyMessage(0);
    }
    public void stopHandler() {
        mHandler.removeMessages(0);
    }
    public void doSomethingFrequently() {}

    public int getLayoutId() {
//        return 0;
        return R.layout.fragment_foot_chart;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("onCreateView: " + getClass().getSimpleName());
        setUpHandler();
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void showBLEDevice(BluetoothDevice bt) {}

    @Override
    public void showBLEData(BLEDataServer.BLEData data) {}
}
