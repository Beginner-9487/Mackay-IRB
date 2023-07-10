package com.example.mackayirb.adapter;

import com.example.mackayirb.R;
import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.utils.BasicResourceManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class LeDeviceAdapter extends RecyclerView.Adapter<LeDeviceAdapter.DeviceViewHolder> {

    SharedPreferences mSharedPreference;

    private final List<BluetoothDevice> mLeDevices = new ArrayList<>();
    private final Map<BluetoothDevice, BLEDataServer.BLEData> mBLEDataMap = new HashMap<>();

    private DeviceItemClickListener mListener;

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_device, parent, false);

        DeviceViewHolder viewHolder = new DeviceViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final DeviceViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        BluetoothDevice bt = mLeDevices.get(position);
        BLEDataServer.BLEData data = mBLEDataMap.get(bt);
        // Log.d("LeDeviceAdapter", "position(" + position + "), " + bt.toString());

//        if (ActivityCompat.checkSelfPermission(BasicResourceManager.getCurrentFragment().getActivity(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
////            return;
//        }
        holder.deviceName.setText(holder.resources.getString(R.string.DeviceName) + ": " + bt.getName());
        holder.deviceAddress.setText(holder.resources.getString(R.string.DeviceAddress) + ": " + bt.getAddress());

        // Log.e("LeDeviceAdapter", bt.getName() + ": connectedState: " + String.valueOf(data.connectedState));

        switch (BasicResourceManager.SharedPreferencesManager.getModeController()) {
            case BasicResourceManager.SharedPreferencesManager.MackayClientMode:
                holder.barEditDeleteDevice.setVisibility(View.VISIBLE);
                holder.barConnection.setVisibility(View.GONE);
                break;
            case BasicResourceManager.SharedPreferencesManager.MackayDeveloperMode:
            case BasicResourceManager.SharedPreferencesManager.FootDeveloperMode:
                holder.barEditDeleteDevice.setVisibility(View.GONE);
                holder.barConnection.setVisibility(View.VISIBLE);
                break;
        }

        if (data != null) {

            if (data.connectedState == BluetoothProfile.STATE_CONNECTED) {
                holder.deviceCard.setBackgroundResource(R.color.Connected_Card);
                holder.deviceState.setText(holder.resources.getString(R.string.State) + ": " + holder.resources.getString(R.string.Connected));
            } else {
                holder.deviceCard.setBackgroundResource(R.color.Disconnected_Card);
                holder.deviceState.setText(holder.resources.getString(R.string.State) + ": " + holder.resources.getString(R.string.Disconnected));
            }

            holder.deviceRSSI.setText(holder.resources.getString(R.string.State) + ": " + data.rssi);
        }

        if (data == null || data.connectedState == BluetoothProfile.STATE_DISCONNECTED) {
            holder.buttonConnect.setText(R.string.listItem_device_Btn_Connect);
            holder.connect_button_state = true;
        } else {
            holder.buttonConnect.setText(R.string.listItem_device_Btn_Disconnect);
            holder.connect_button_state = false;
        }

        if (data == null || data.device.getBondState() == BluetoothDevice.BOND_NONE) {
            holder.iconDeleteDevice.setVisibility(View.VISIBLE);
            holder.pair_button_state = true;
        } else {
            holder.iconDeleteDevice.setVisibility(View.GONE);
            holder.pair_button_state = false;
        }

        holder.deviceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null && mLeDevices.size() > position) {
                    mListener.onItemClicked(mLeDevices.get(position), position);
                }
            }
        });

        holder.iconEditDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null && mLeDevices.size() > position) {
                    mListener.onItemEditDeviceButtonClicked(mLeDevices.get(position), position, holder.connect_button_state);
                }
            }
        });

        holder.iconDeleteDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null && mLeDevices.size() > position) {
                    mListener.onItemDeleteDeviceButtonClicked(mBLEDataMap.get(mLeDevices.get(position)), position, holder.pair_button_state);
                }
            }
        });

        holder.buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null && mLeDevices.size() > position) {
                    mListener.onItemConnectionButtonClicked(mLeDevices.get(position), position, holder.connect_button_state);
                }
            }
        });

        holder.buttonPair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null && mLeDevices.size() > position) {
                    mListener.onItemPairButtonClicked(mBLEDataMap.get(mLeDevices.get(position)), position, holder.pair_button_state);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLeDevices.size();
    }

    public BluetoothDevice getBtDeviceAtIndex(int index) {
        if (index < mLeDevices.size()) {
            return mLeDevices.get(index);
        }

        return null;
    }

    public boolean addDevice(BluetoothDevice device) {
        if (device != null && !mLeDevices.contains(device)) {
            mLeDevices.add(device);
            return true;
        }
        return false;
    }

    public boolean removeDevice(BluetoothDevice device) {
        if (device != null && mLeDevices.contains(device)) {
            mLeDevices.remove(device);
            return true;
        }
        return false;
    }
    public boolean removeDeviceByAddress(String Address) {
        for (BluetoothDevice device:mLeDevices) {
            if (device.getAddress().equals(Address)) {
                mLeDevices.remove(device);
                return true;
            }
        }
        return false;
    }

    public void clearDevices() {
        mLeDevices.clear();
    }

    public void showBLEData(BLEDataServer.BLEData data) {
        mBLEDataMap.put(data.device, data);
    }

    public void setListener(DeviceItemClickListener listener) {
        mListener = listener;
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {

        Resources resources;

        TextView deviceName;
        TextView deviceAddress;
        TextView deviceState;
        TextView deviceRSSI;
        CardView deviceCard;
        Button iconEditDevice;
        Button iconDeleteDevice;
        Button buttonConnect;
        Button buttonPair;
        boolean connect_button_state;
        boolean pair_button_state;
        LinearLayout barEditDeleteDevice;
        LinearLayout barConnection;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            resources = itemView.getResources();

            deviceCard = (CardView) itemView.findViewById(R.id.device_card);
            deviceName = (TextView) itemView.findViewById(R.id.device_name);
            deviceAddress = (TextView) itemView.findViewById(R.id.device_address);
            deviceState = (TextView) itemView.findViewById(R.id.connection_state);
            deviceRSSI = (TextView) itemView.findViewById(R.id.rssi);
            iconEditDevice = (Button) itemView.findViewById(R.id.IconEditDevice);
            iconDeleteDevice = (Button) itemView.findViewById(R.id.IconDeleteDevice);
            buttonConnect = (Button) itemView.findViewById(R.id.ButtonConnect);
            buttonPair = (Button) itemView.findViewById(R.id.ButtonPair);
            barEditDeleteDevice = (LinearLayout) itemView.findViewById(R.id.BarEditDeleteDevice);
            barConnection = (LinearLayout) itemView.findViewById(R.id.BarConnection);
        }

        public String getDeviceName() {
            return deviceName.getText().toString();
        }
    }

    public interface DeviceItemClickListener {
        void onItemClicked(BluetoothDevice device, int position);
        void onItemEditDeviceButtonClicked(BluetoothDevice device, int position, boolean connection_state_setting);
        void onItemDeleteDeviceButtonClicked(BLEDataServer.BLEData data, int position, boolean bonding_state_setting);
        void onItemConnectionButtonClicked(BluetoothDevice device, int position, boolean connection_state_setting);
        void onItemPairButtonClicked(BLEDataServer.BLEData data, int position, boolean bonding_state_setting);
    }
}