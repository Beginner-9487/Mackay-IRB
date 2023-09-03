package com.example.mackayirb.ui.central;

import com.example.mackayirb.R;
import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.OtherUsefulFunction;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import java.util.Calendar;
import java.util.HashMap;

public class CentralTempUIFragment2 extends CentralFragment implements CentralMvpView {

    EditText edit_command;
    Button btn_c3;
    Button btn_clearRawData;
    Button btn_saveFile;
    TextView text_time;

    long startingClock;
    boolean isStartingClockInit = false;

    @Override
    public void doSomethingFrequently() {
        text_time.setText(String.valueOf((Calendar.getInstance().getTimeInMillis() - startingClock) / 1000.0f));
    }

    HashMap<BluetoothDevice, BLEDataServer.BLEData> mData = new HashMap<>();

    @Override
    public int getLayoutId() {
        return R.layout.fragment_temp_ui_2;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        edit_command = view.findViewById(R.id.Command_Edit);
        btn_c3 = view.findViewById(R.id.C3_Send_Button);
        btn_clearRawData = view.findViewById(R.id.Clear_Raw_Data_Button);
        btn_saveFile = view.findViewById(R.id.Save_File_Button);
        text_time = view.findViewById(R.id.TimeText);

        startingClock = (isStartingClockInit) ? startingClock : Calendar.getInstance().getTimeInMillis();
        isStartingClockInit = true;

        btn_c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BasicResourceManager.Permissions.checkGroupPermissions(BasicResourceManager.Permissions.REQUEST_EXTERNAL_STORAGE_CODE)) {
                    mCentralPresenter.SendToAllCharacteristic(OtherUsefulFunction.hexStringToByteArray(edit_command.getText().toString()));
                    edit_command.setText("");
                    startingClock = Calendar.getInstance().getTimeInMillis();
                }
            }
        });

        btn_clearRawData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BasicResourceManager.Permissions.checkGroupPermissions(BasicResourceManager.Permissions.REQUEST_EXTERNAL_STORAGE_CODE)) {
                    mCentralPresenter.clearRawData();
                }
            }
        });

        btn_saveFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BasicResourceManager.Permissions.checkGroupPermissions(BasicResourceManager.Permissions.REQUEST_EXTERNAL_STORAGE_CODE)) {
                    mCentralPresenter.createManagerDataFile();
                }
            }
        });

        startHandler();

        return view;
    }

    @Override
    public void showBLEDevice(BluetoothDevice bt) {

    }

    @Override
    public void showBLEData(BLEDataServer.BLEData data) {
        mData.put(data.device, data);
    }

}

