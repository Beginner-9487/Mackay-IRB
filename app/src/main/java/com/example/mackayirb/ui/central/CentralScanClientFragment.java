package com.example.mackayirb.ui.central;

import com.example.mackayirb.R;
import com.example.mackayirb.data.central.MackayDataManager;
import com.example.mackayirb.ui.main.MainActivity;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.MyNamingStrategy;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class CentralScanClientFragment extends CentralScanDeveloperFragment implements CentralMvpView {

    private EditText editDataName;
    private Button buttonStart;
    private FloatingActionButton buttonAddNewDevice;

    public void Start() {
        ((MainActivity) getActivity()).changeToolbar(1);
        ((MainActivity) getActivity()).mViewPager.setCurrentItem(1);
        mCentralPresenter.Send_All_C(OtherUsefulFunction.hexStringToByteArray("60"));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mCentralPresenter.Send_All_C(OtherUsefulFunction.hexStringToByteArray("01"));
            }
        }, 500);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }
    @Override
    public int getRefreshId() {
        return R.id.device_scan_fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        editDataName = (EditText) view.findViewById(R.id.DataNameText);
        editDataName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                SetNamingStrategy();
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        buttonStart = (Button) view.findViewById(R.id.ButtonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!initPermission()) {return;}
                sendCommand();
                Start();
            }
        });
        buttonAddNewDevice = (FloatingActionButton) view.findViewById(R.id.AddNewDevice);
        buttonAddNewDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!initPermission()) {return;}
                createAddDeviceDialog().show();
            }
        });

        return view;
    }

    public void SetNamingStrategy() {
        ((MackayDataManager) (mCentralPresenter.getCentralDataManager())).SetAllNamingStrategy(MyNamingStrategy.MODE_XIE_ZHI_LONG, editDataName.getText().toString());
    }

    @Override
    public void showBLEDevice(BluetoothDevice bt) {
        // Log.d("showBLEDevice");
        // Log.d("showBLEDevice: ", bt.getAddress());
        // Log.d("getAllTargetDevices: ", ": " + getAllTargetDevices(mSharedPreference));
        boolean keepConnect = false;
        for (String address: BasicResourceManager.SharedPreferencesManager.getAllTargetDevices()) {
            if (bt != null && bt.getAddress().equals(address)) {
                keepConnect = true;
                break;
            }
        }
        if(keepConnect) {
             Log.d("Connect: " + bt.getAddress());
//            if(mLeDeviceAdapter.addDevice(bt)) {
//                mLeDeviceAdapter.notifyDataSetChanged();
//            }
            mLeDeviceAdapter.addDevice(bt);
            mLeDeviceAdapter.notifyDataSetChanged();
            mCentralPresenter.connectGatt(bt);
        } else if (!keepConnect) {
             Log.d("Disconnect: " + bt.getAddress());
//            if(mLeDeviceAdapter.removeDevice(bt)) {
//                mLeDeviceAdapter.notifyDataSetChanged();
//            }
            mLeDeviceAdapter.removeDevice(bt);
            mLeDeviceAdapter.notifyDataSetChanged();
            mCentralPresenter.disconnectGatt(bt);
        }
    }

    public void addDevice(String deviceAddress) {
        BasicResourceManager.SharedPreferencesManager.addDeviceToAllTargetDevices(deviceAddress);
        mCentralPresenter.getRemoteDevices();
    }
    public void editDevice(String oldAddress, String newAddress) {
        deleteDevice(oldAddress);
        addDevice(newAddress);
    }

    @Override
    public void deleteDevice(String deviceAddress) {
        BasicResourceManager.SharedPreferencesManager.deleteDeviceToAllTargetDevices(deviceAddress);
        mCentralPresenter.getRemoteDevices();
    }

    public Dialog createAddDeviceDialog() {
        return createAddOrEditDeviceDialog(true, getResources().getString(R.string.AddDevice), "", null);
    }
    public Dialog createEditDeviceDialog(String OldAddress) {
        return createAddOrEditDeviceDialog(false, getResources().getString(R.string.EditDevice), getResources().getString(R.string.CurrentDeviceIP) + getResources().getString(R.string.Is) + OldAddress, OldAddress);
    }
    public Dialog createAddOrEditDeviceDialog(final boolean AddOrEdit, String Title, String Message, String Address) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_old_and_new_device, (ViewGroup) getView(), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.NewDevice);
        builder
                .setView(viewInflated)
                .setTitle(Title)
                .setMessage(Message)
                .setPositiveButton(
                        getResources().getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (AddOrEdit) {
                                    addDevice(input.getText().toString());
                                } else {
                                    editDevice(Address, input.getText().toString());
                                }
                                dialogInterface.dismiss();
                            }
                        }
                )
                .setNegativeButton(
                        getResources().getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }
                );

        return builder.create();
    }

    private void sendCommand() {
        mCentralPresenter.Send_All_C(OtherUsefulFunction.hexStringToByteArray("60"));
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mCentralPresenter.Send_All_C(OtherUsefulFunction.hexStringToByteArray("01"));
            }
        }, 1000);
    }

    private AlertDialog getMyMultipleSelector() {
        // Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        Log.d(BasicResourceManager.SharedPreferencesManager.getAllTargetDevices().toString());
        String[] deviceList = BasicResourceManager.SharedPreferencesManager.getAllTargetDevices();
        boolean[] booleans = new boolean[deviceList.length];
        for (int i=0; i<deviceList.length; i++) {
            booleans[i] = false;
        }

        // Build
        final AlertDialog dialog = builder
                .setTitle(getResources().getString(R.string.DeleteSelectedDevices))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(android.R.string.ok), null)
                .setNegativeButton(getResources().getString(R.string.Fragment_SelectAll), null)
                .setMultiChoiceItems(deviceList, booleans, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                // =====================================================================================
                // MultiChoiceItems
                dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        booleans[i] = !booleans[i];
                        for (boolean b: booleans) {
                            if(b) {
                                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setText(getResources().getString(R.string.Fragment_ClearAll));
                                break;
                            } else {
                                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setText(getResources().getString(R.string.Fragment_SelectAll));
                            }
                        }
                    }
                });

                // =====================================================================================
                // Change All
                final Button buttonChangeAll = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                for (boolean b: booleans) {
                    if(b) {
                        buttonChangeAll.setText(getResources().getString(R.string.Fragment_ClearAll));
                        break;
                    }
                }
                buttonChangeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean b = buttonChangeAll.getText().equals(getResources().getString(R.string.Fragment_SelectAll));
                        for (int i=0; i<booleans.length; i++) {
                            booleans[i] = b;
                            dialog.getListView().setItemChecked(i, b);
                        }
                        buttonChangeAll.setText((b)?getResources().getString(R.string.Fragment_ClearAll):getResources().getString(R.string.Fragment_SelectAll));
                    }
                });

                // =====================================================================================
                // OK
                Button buttonOk = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<Boolean> bs = new ArrayList<>();
                        for (int i=0; i<deviceList.length; i++) {
                            if(booleans[i]) {
                                deleteDevice(deviceList[i]);
                            }
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        return dialog;

    }

    @Override
    public void startCentralDetailsActivity(BluetoothDevice device) {
    }

    @Override
    public void getCreatedEditDeviceDialog(String Address) {
        createEditDeviceDialog(Address).show();
    }

}