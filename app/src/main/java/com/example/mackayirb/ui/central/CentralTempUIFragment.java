package com.example.mackayirb.ui.central;

import com.example.mackayirb.R;
import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.data.central.MackayDataManager;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.MyNamingStrategy;
import com.example.mackayirb.utils.OtherUsefulFunction;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;


import java.util.Calendar;
import java.util.HashMap;

public class CentralTempUIFragment extends CentralFragment implements CentralMvpView {

    RadioGroup radioGroup_senior;
    EditText edit_command;
    Button btn_c3;
    EditText edit_dataName;
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
        return R.layout.central_temp_ui;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        radioGroup_senior = view.findViewById(R.id.SeniorRadioGroup);
        edit_command = view.findViewById(R.id.Command_Edit);
        btn_c3 = view.findViewById(R.id.C3_Send_Button);
        edit_dataName = view.findViewById(R.id.DataName_Text);
        text_time = view.findViewById(R.id.TimeText);

        startingClock = (isStartingClockInit) ? startingClock : Calendar.getInstance().getTimeInMillis();
        isStartingClockInit = true;

        radioGroup_senior.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                SetNamingStrategy();
            }
        });
        btn_c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BasicResourceManager.Permissions.checkGroupPermissions(BasicResourceManager.Permissions.REQUEST_EXTERNAL_STORAGE_CODE)) {
                    SetNamingStrategy();
                    mCentralPresenter.SendToAllCharacteristic(OtherUsefulFunction.hexStringToByteArray(edit_command.getText().toString()));
                    edit_command.setText("");
                    startingClock = Calendar.getInstance().getTimeInMillis();
                }
            }
        });

        edit_dataName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SetNamingStrategy();
            }
            @Override
            public void afterTextChanged(Editable editable) { }
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

    public void SetNamingStrategy() {
        switch(radioGroup_senior.getCheckedRadioButtonId()) {
            case R.id.RadioLiZe:
                ((MackayDataManager) (mCentralPresenter.getCentralDataManager())).SetAllNamingStrategy(MyNamingStrategy.MODE_NORMAL, edit_dataName.getText().toString());
                break;
            case R.id.RadioXieZhiLong:
                ((MackayDataManager) (mCentralPresenter.getCentralDataManager())).SetAllNamingStrategy(MyNamingStrategy.MODE_XIE_ZHI_LONG, edit_dataName.getText().toString());
                break;
        }
    }

}

