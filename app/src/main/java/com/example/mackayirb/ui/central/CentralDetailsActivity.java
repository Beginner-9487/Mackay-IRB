package com.example.mackayirb.ui.central;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.mackayirb.R;
import com.example.mackayirb.SampleGattAttributes;
import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.ui.base.BaseActivity;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

//import butterknife.BindView;
//import butterknife.ButterKnife;

/**
 * Created by jacobsu on 4/30/16.
 */
public class CentralDetailsActivity extends BaseActivity implements CentralMvpView {

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private String mDeviceName;
    private String mDeviceAddress;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;

    //    @BindView(R.id.device_address)
    TextView mDeviceAddressTv;

    //    @BindView(R.id.gatt_services_list)
    ExpandableListView mGattServicesList;

    //    @BindView(R.id.connection_state)
    TextView mConnectionState;

    //    @BindView(R.id.data_value)
    TextView mDataField;

    //    @BindView(R.id.signal_rssi)
    TextView mRssiField;

    //    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    CentralPresenter mCentralPresenter;

    @Inject
    BluetoothAdapter mBtAdapter;

    // If a given GATT characteristic is selected, check for supported features.
    // This sample demonstrates 'Read' and 'Notify' features.
    // See http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener mServicesListClickListener =
            new ExpandableListView.OnChildClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            Log.d("BluetoothGattCharacteristic has PROPERTY_READ, so send read request");

                        /*if (mNotifyCharacteristic != null) {
                            mBluetoothLeService.setCharacteristicNotification(
                                    mNotifyCharacteristic, false);
                            mNotifyCharacteristic = null;
                        }
                        mBluetoothLeService.readCharacteristic(characteristic);*/

                            // TODO Fragment 顯示資料
                            // mGattCharacteristics.get(groupPosition).get(childPosition).getValue();
                        }

                        if ((charaProp & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            Log.d("BluetoothGattCharacteristic has PROPERTY_NOTIFY, so send notify request");

                       /* mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(
                                characteristic, true);*/

                            // TODO Fragment 顯示資料
                        }

                        if (((charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) |
                                (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) > 0) {
                            Log.d("BluetoothGattCharacteristic has PROPERY_WRITE | PROPERTY_WRITE_NO_RESPONSE");

                            mWriteCharacteristic = characteristic;
                            // popup an dialog to write something.
                            /*showCharactWriteDialog();*/

                            // TODO Fragment 顯示資料
                        }

                        return true;
                    }
                    return false;
                }
            };

    private BluetoothDevice mRemoteDevice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
        setContentView(R.layout.fragment_gatt_services);

        mDeviceAddressTv = findViewById(R.id.device_address);
        mGattServicesList = findViewById(R.id.gatt_services_list);
        mConnectionState = findViewById(R.id.connection_state);
        mDataField = findViewById(R.id.data_value);
        mRssiField = findViewById(R.id.signal_rssi);
        mToolbar = findViewById(R.id.toolbar);

        if (mToolbar != null) {
            mToolbar.setTitle("Gatt Services");
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        mRemoteDevice = mBtAdapter.getRemoteDevice(mDeviceAddress);

        // Sets up UI references.
        if (mDeviceAddress != null) {
            mDeviceAddressTv.setText(mDeviceAddress);
        }

        mGattServicesList.setOnChildClickListener(mServicesListClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        mCentralPresenter.attachView(this);
        mCentralPresenter.initForBLEData();
        mCentralPresenter.startHandler(CentralPresenter.READ_RSSI_REPEAT);
    }

    @Override
    public void onStop() {
        super.onStop();

//        stopReadRssi();
        mCentralPresenter.detachView();
    }

    @Override
    public void showBLEDevice(BluetoothDevice bt) {
        // do nothing here

    }

    @Override
    public void showBLEData(final BLEDataServer.BLEData data) {
        if (Objects.equals(data.device.getAddress(), mDeviceAddress)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRssiField.setText(String.valueOf(data.rssi));
                    mConnectionState.setText((data.connectedState == BluetoothProfile.STATE_CONNECTED) ? getResources().getString(R.string.Connected) : getResources().getString(R.string.Disconnected));
                    mDataField.setText("");
                    displayGattServices(data.services);
                }
            });
        }
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        Log.d("displayGATTServices");

        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.UnknownService);
        String unknownCharaString = getResources().getString(R.string.UnknownCharacteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        BasicResourceManager.setCurrentActivity(this);
    }
}
