package com.example.mackayirb.ui.central;

import com.example.mackayirb.R;
import com.example.mackayirb.utils.BasicResourceManager;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;

public class CentralScanDeveloperFragment extends CentralScanFragment implements CentralMvpView {

    @Override
    public int getLayoutId() {
        return R.layout.fragment_device_scan;
    }
    @Override
    public int getRefreshId() {
        return R.id.swipe_refresh_layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        startScanner();
    }

    @Override
    public void onResume() {
        super.onResume();
        mCentralPresenter.getRemoteDevices();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScanner();
    }

    @Override
    public void onRefreshSwipeLayout() {
        startScanner();
    }

    @Override
    public void startCentralDetailsActivity(BluetoothDevice device) {
        if (BasicResourceManager.Permissions.checkGroupPermissions(BasicResourceManager.Permissions.REQUEST_BLUETOOTH_CODE)) {
            Intent intent = new Intent(getActivity(), CentralDetailsActivity.class);
            intent.putExtra(CentralDetailsActivity.EXTRAS_DEVICE_NAME, device.getName());
            intent.putExtra(CentralDetailsActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
            startActivity(intent);
        }
    }

    @Override
    public void getCreatedEditDeviceDialog(String Address) {

    }

    @Override
    public void deleteDevice(String Address) {

    }

}