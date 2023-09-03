package com.example.mackayirb.ui.central;

import com.example.mackayirb.R;
import com.example.mackayirb.adapter.LeDeviceAdapter;
import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.ui.base.BasePresenter;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public abstract class CentralScanFragment extends CentralFragment implements CentralMvpView {

    RecyclerView mRecyclerView;

    SwipeRefreshLayout mRefreshLayout;

    public LeDeviceAdapter mLeDeviceAdapter;

    public LeDeviceAdapter getLeDeviceAdapter() {
        return mLeDeviceAdapter;
    }

    public abstract int getRefreshId();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setupScanner();

        mRecyclerView = view.findViewById(R.id.recycler);

        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(getRefreshId());

        mLeDeviceAdapter = new LeDeviceAdapter();
        mRecyclerView.setAdapter(mLeDeviceAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mLeDeviceAdapter.setListener(new LeDeviceAdapter.DeviceItemClickListener() {

            @Override
            public void onItemClicked(BluetoothDevice device, int position) {
                startCentralDetailsActivity(device);
            }

            @Override
            public void onItemEditDeviceButtonClicked(BluetoothDevice device, int position, boolean connection_state_setting) {
                if(!initPermission()) {return;}
                getCreatedEditDeviceDialog(device.getAddress());
            }

            @Override
            public void onItemDeleteDeviceButtonClicked(BLEDataServer.BLEData data, int position, boolean bonding_state_setting) {
                if(!initPermission()) {return;}
                deleteDevice(data.device.getAddress());
            }

            @Override
            public void onItemConnectionButtonClicked(BluetoothDevice device, int position, boolean connection_state_setting) {
                connectGatt(device, connection_state_setting);
            }

            @Override
            public void onItemPairButtonClicked(BLEDataServer.BLEData data, int position, boolean bonding_state_setting) {
                createBond(data);
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onRefreshSwipeLayout();
            }
        });

        // TODO setColorSchemeResources
        mRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    public boolean initPermission() {
        try {
            return
                    // OtherUsefulFunction.checkBluetoothPermission(getActivity()) &&
                    BasicResourceManager.Permissions.checkGroupPermissions(BasicResourceManager.Permissions.REQUEST_LOCATION_CODE) &&
                            BasicResourceManager.Permissions.checkGroupPermissions(BasicResourceManager.Permissions.REQUEST_EXTERNAL_STORAGE_CODE);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void showBLEDevice(BluetoothDevice bt) {
        // maybe running in UI thread
        mLeDeviceAdapter.addDevice(bt);
        mLeDeviceAdapter.notifyDataSetChanged();
//        Log.d(bt.getAddress());
    }

    @Override
    public void showBLEData(BLEDataServer.BLEData data) {
        try {
            // Log.e(data.device.getName());
            mLeDeviceAdapter.showBLEData(data);
            mLeDeviceAdapter.notifyDataSetChanged();
//            Log.d(data.device.getAddress());
        } catch (Exception e) {}
    }

    public abstract void onRefreshSwipeLayout();

    public abstract void startCentralDetailsActivity(BluetoothDevice device);

    public abstract void getCreatedEditDeviceDialog(String Address);

    public abstract void deleteDevice(String Address);

    public void connectGatt(BluetoothDevice device, boolean connection_state_setting) {
        if(connection_state_setting) {
            mCentralPresenter.connectGatt(device);
        } else {
            mCentralPresenter.disconnectGatt(device);
        }
    }
    public void createBond(BLEDataServer.BLEData data) {
        mCentralPresenter.createBond(data);
    }

    private final int BLE_SCAN_PERIOD = 10000;
    private Handler mScanner;
    private void setupScanner() {
        if(Looper.myLooper() != null) {
            mScanner = new Handler(Looper.myLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 0:
                            try {
                                scanLeDevice(true);
                            } catch (Exception e) {}
                            sendMessageDelayed(
                                    obtainMessage(1),
                                    BLE_SCAN_PERIOD
                            );
                            break;
                        case 1:
                            scanLeDevice(false);
                    }

                }
            };
        }
    }
    public void startScanner() {
        if (mScanner.hasMessages(1)) {
            return;
        }
        mScanner.sendEmptyMessage(0);
    }
    public void stopScanner() {
        if (mScanner.hasMessages(1)) {
            mScanner.removeMessages(1);
            return;
        }
        mScanner.sendEmptyMessage(1);
    }
    public void scanLeDevice(final boolean enable) {
        if(!initPermission()) {return;}
        try {
            mCentralPresenter.scanBLEPeripheral(enable);
            mRefreshLayout.setRefreshing(enable);
        } catch (BasePresenter.MvpViewNotAttachedException e) {
            Log.e(e.toString());
            return;
        }
    }

}
