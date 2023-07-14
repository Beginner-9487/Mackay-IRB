package com.example.mackayirb.data.ble;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.mackayirb.R;
import com.example.mackayirb.SampleGattAttributes;
import com.example.mackayirb.injector.ApplicationContext;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.OtherUsefulFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 *
 */
public class BLEDataServer extends Service {

    private Context mContext;
    private BluetoothLeScanner mLeScanner;
    private BLEPeripheralServer mPeripheralServer;

    private BluetoothAdapter mBluetoothAdapter;

    private ObservableEmitter<BLEData> mLEScanEmitter;

    // BlueGatt -> BLEData
    // BlueGatt -> ObservableEmitter
    private List<BLEData> mBLEData = new ArrayList<>();
    public List<BLEData> getBLEData() {
        return mBLEData;
    }
    private Map<ObservableEmitter<BLEData>, BluetoothGatt> mGattMap = new HashMap<>();

    // ================================================================================
    // TODO Foreground

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "BluetoothServiceChannel";

    public BLEDataServer() {
        // Default constructor with no arguments
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand");
        startForeground(NOTIFICATION_ID, createNotification());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    private Notification createNotification() {
        createNotificationChannel();

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setContentTitle("Bluetooth Service")
                .setContentText("Scanning for Bluetooth devices")
                .setSmallIcon(R.drawable.ic_launcher_foreground);

        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Bluetooth Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // ================================================================================
    // TODO Scanning

    public Observable<BLEData> scanBLEPeripheral(boolean enabled) {
        return Observable.create(new ObservableOnSubscribe<BLEData>() {
            @Override
            public void subscribe(ObservableEmitter<BLEDataServer.BLEData> e) throws Exception {
                if (enabled) {
                    mLEScanEmitter = e;
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//                        return;
                    }
                    mLeScanner.startScan(mScanCallback);
                } else {
                    e.onComplete();
                    mLeScanner.stopScan(mScanCallback);
                }

            }
        });
    }

    // ================================================================================
    // TODO mScanCallback

    private boolean IgnoreDeviceWithoutName(ScanResult result) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            return false;
        }
        // Ignore all devices without name.
        if (result.getDevice().getName() == null) {
            return false;
        }
        return true;
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if (!IgnoreDeviceWithoutName(result)) {
                return;
            }
            BLEData d = findBLEDataByDevice(result.getDevice());
            d.rssi = result.getRssi();
            if (mLEScanEmitter != null) {
                mLEScanEmitter.onNext(d);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            if (mLeScanner == null) {
                return;
            }
            for (ScanResult r : results) {

                if (!IgnoreDeviceWithoutName(r)) {
                    return;
                }

                BLEData d = findBLEDataByDevice(r.getDevice());
                d.rssi = r.getRssi();
                mLEScanEmitter.onNext(d);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            // mLEScanEmitter.onError(new Throwable("scan failed with errorCode: " + errorCode));
            mLEScanEmitter.onComplete();
        }
    };

    // ================================================================================
    // TODO mGattCallback
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                return;
            }

            Log.e(gatt.getDevice().getName() + ": newState: " + newState);

            super.onConnectionStateChange(gatt, status, newState);
            BLEData d = findBLEData(gatt);
            List<ObservableEmitter<BLEData>> emitters = findObservableEmitter(gatt);

            d.connectedState = newState;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
            }

            for (ObservableEmitter<BLEData> s : emitters) {
                s.onNext(d);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            BLEData d = findBLEData(gatt);
            List<ObservableEmitter<BLEData>> subscribers = findObservableEmitter(gatt);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                d.services = gatt.getServices();

                // Add CCCD to all Characteristics
                for (BluetoothGattService service : gatt.getServices()) {
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        if (SampleGattAttributes.checkSubscribed(String.valueOf(characteristic.getUuid()))) {
                            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                                return;
                            }
                            boolean success = gatt.setCharacteristicNotification(characteristic, true);
                            if (success) {
                                // 来源：http://stackoverflow.com/questions/38045294/oncharacteristicchanged-not-called-with-ble
                                for (BluetoothGattDescriptor dp : characteristic.getDescriptors()) {
                                    if (dp != null) {
                                        if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                                            dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                        } else if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                                            dp.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                                        }
                                        gatt.writeDescriptor(dp);
                                    }
                                }
                            }
                        }

                    }
                }

                for (ObservableEmitter<BLEData> s : subscribers) {
                    s.onNext(d);
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
//                Toast.makeText(mContext, "onCharacteristicRead: " + characteristic.getUuid().toString(), Toast.LENGTH_SHORT).show();
                whenFetchingData(gatt, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
//            Toast.makeText(mContext, "onCharacteristicChanged: " + characteristic.getUuid().toString(), Toast.LENGTH_SHORT).show();
            whenFetchingData(gatt, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            BLEData d = findBLEData(gatt);
            List<ObservableEmitter<BLEData>> emitters = findObservableEmitter(gatt);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                d.rssi = rssi;

                for (ObservableEmitter<BLEData> s : emitters) {
                    s.onNext(d);
                }
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }

    };

    @Inject
    public BLEDataServer(@ApplicationContext Context context, BluetoothLeScanner leScanner, BLEPeripheralServer peripheralServer, BluetoothAdapter bluetoothAdapter) {
        mContext = context;
        mLeScanner = leScanner;
        mPeripheralServer = peripheralServer;
        mBluetoothAdapter = bluetoothAdapter;
    }

    // ================================================================================
    // TODO ConnectGatt, DisconnectGatt

    final boolean AutoConnect = true;

    public Observable<BLEData> connectGatt(final BluetoothDevice device) {
        Log.d(device.getAddress());
        // if device is already connected,
        return Observable.create(new ObservableOnSubscribe<BLEData>() {
            @Override
            public void subscribe(ObservableEmitter<BLEData> e) throws Exception {
                BluetoothGatt gatt = findBluetoothGatt(device);

                if (gatt == null) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                        return;
                    }
                    gatt = device.connectGatt(mContext, AutoConnect, mGattCallback);
                } else {
                    gatt.connect();
                    e.onNext(findBLEData(gatt));
                }

                for (Map.Entry<ObservableEmitter<BLEData>, BluetoothGatt> entry : mGattMap.entrySet()) {
                    if (entry.getValue().equals(gatt)) {
                        mGattMap.remove(entry.getKey());
                    }
                }
                mGattMap.put(e, gatt);
            }
        });
    }

    public void disconnectGatt(final BluetoothDevice device) {
        Log.d(device.getAddress());
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            return;
        }
        BluetoothGatt gatt = findBluetoothGatt(device);
        if (gatt != null) {
            BLEData bleData = findBLEData(gatt);
            if (bleData.connectedState != BluetoothProfile.STATE_DISCONNECTED && bleData.connectedState != BluetoothProfile.STATE_DISCONNECTING) {
                gatt.disconnect();
            }
        }
    }

    // ================================================================================
    // TODO Other Function

    public List<BLEData> getRemoteBLEData() {
        return mBLEData;
    }

    public boolean readRemoteRssi(BluetoothDevice device) {
        BluetoothGatt gatt = findBluetoothGatt(device);

        if (gatt != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                return false;
            }
            return gatt.readRemoteRssi();
        }

        return false;
    }

    public void startCentralMode() {
        // stop Peripheral Mode first
        stopPeripheralMode();
    }

    public void stopCentralMode() {
        // stop scan
        // disconnect from all gatt
    }

    public void whenFetchingData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//        Toast.makeText(mContext, "whenFetchingData: \n" + characteristic.getUuid().toString(), Toast.LENGTH_SHORT).show();
//        Toast.makeText(BasicResourceManager.getCurrentFragment().getContext(), "whenFetchingData: " +
//                OtherUsefulFunction.byteArrayToHexString(characteristic.getValue(), ", ")
//                , Toast.LENGTH_SHORT).show();
        updateLastReceivedData(gatt, characteristic);
    }

    public void updateLastReceivedData(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        BLEData d = findBLEData(gatt);
        List<ObservableEmitter<BLEData>> subscribers = findObservableEmitter(gatt);

        if (!d.lastReceivedData.containsKey(characteristic.getService())) {
            d.lastReceivedData.put(characteristic.getService(), new HashMap());
        }
        if (!d.lastReceivedData.get(characteristic.getService()).containsKey(characteristic)) {
            d.lastReceivedData.get(characteristic.getService()).put(characteristic, new ArrayList<byte[]>());
        }
        d.lastReceivedData.get(characteristic.getService()).get(characteristic).add(characteristic.getValue());
        // Log.d(String.valueOf(d.lastReceivedData.get(characteristic.getService()).get(characteristic).size()));

        for (ObservableEmitter<BLEData> s : subscribers) {
            s.onNext(d);
        }
    }

    public boolean supportLEAdvertiser() {
        return mPeripheralServer.supportPeripheralMode();
    }

    public void startPeripheralMode(String name) {
        // stop Central mode first
        stopCentralMode();

        mPeripheralServer.startPeripheralMode(name);
    }

    public void stopPeripheralMode() {
        mPeripheralServer.stopPeripheralMode();
    }

    private BluetoothGatt findBluetoothGatt(BluetoothDevice device) {
        for (BluetoothGatt d : mGattMap.values()) {
            if (d.getDevice().equals(device)) {
                return d;
            }
        }

        return null;
    }

    private List<ObservableEmitter<BLEData>> findObservableEmitter(BluetoothGatt gatt) {
        List<ObservableEmitter<BLEData>> emitters = new ArrayList<>();

        for (ObservableEmitter<BLEData> s : mGattMap.keySet()) {
            if (mGattMap.get(s).equals(gatt)) {
                emitters.add(s);
            }
        }

        return emitters;
    }

    private BLEData findBLEData(BluetoothGatt gatt) {
        for (BLEData d : mBLEData) {
            if (gatt != null && gatt.getDevice().equals(d.device)) {
                return d;
            }
        }

        BLEData d = new BLEData(gatt.getDevice());
        mBLEData.add(d);

        return d;
    }

    public BLEData findBLEDataByDevice(BluetoothDevice device) {
        for (BLEData d : mBLEData) {
            if (d != null && d.device != null && d.device.equals(device)) {
                return d;
            }
        }

        BLEData d = new BLEData(device);
        mBLEData.add(d);

        return d;
    }

    public void createBond(BluetoothDevice device) {
        findBLEDataByDevice(device);    // 沒有就加入
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            return;
        }
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            device.createBond();
        }
    }

    public List<BLEData> getAllBondedDevices() {
        List<BLEData> bl = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            return null;
        }
        for (BluetoothDevice b : mBluetoothAdapter.getBondedDevices()) {
            bl.add(findBLEDataByDevice(b));
        }
        return bl;
    }

    // =================================================================================================
    // TODO Temp UI
    public void SendToAllCharacteristic(byte[] command) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            return;
        }
        try {
            for (BluetoothGatt gatt : mGattMap.values()) {
                for (BluetoothGattService service : gatt.getServices()) {
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        if (((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) |
                                (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) > 0) {

                            if (SampleGattAttributes.checkInput(String.valueOf(characteristic.getUuid()))) {
                                characteristic.setValue(command);
                                gatt.writeCharacteristic(characteristic);
                            }

                        }
                    }
                }

                gatt.executeReliableWrite();

            }
        } catch (Exception e) {}
    }

    public byte[] getDeviceData(BluetoothDevice bluetoothDevice, String UUID) {
        return findBLEDataByDevice(bluetoothDevice).getLastReceivedData(UUID);
    }

    // ================================================================================
    // TODO BLEData

    public class BLEData {
        public BluetoothDevice device;
        public int rssi;
        public int connectedState = BluetoothProfile.STATE_DISCONNECTED;
        public List<BluetoothGattService> services; // 從這裡讀取 UUID, Properties, Value, Descriptor
        public HashMap<BluetoothGattService, HashMap<BluetoothGattCharacteristic, ArrayList<byte[]>>> lastReceivedData = new HashMap<>();
        public BLEData(BluetoothDevice device) {
            this.device = device;
        }
        public byte[] getLastReceivedData(String UUID) {
            return processLastReceivedData(UUID, true);

        }
        public byte[] readLastReceivedData(String UUID) {
            return processLastReceivedData(UUID, false);
        }
        synchronized private byte[] processLastReceivedData(String UUID, boolean GetOrRead) {
            for (HashMap<BluetoothGattCharacteristic, ArrayList<byte[]>> s:lastReceivedData.values()) {
                for (Map.Entry<BluetoothGattCharacteristic, ArrayList<byte[]>> c:s.entrySet()) {
                    // Log.d(String.valueOf(c.getKey().getUuid()) + ":" + UUID);
                    if(String.valueOf(c.getKey().getUuid()).equals(UUID)) {
                        // Log.d(String.valueOf(c.getValue().size()));
                        if(c.getValue().size() > 0) {
                            if(GetOrRead) {
                                // Log.d(OtherUsefulFunction.byteArrayToHexString(c.getValue().get(0), ","));
                                return c.getValue().remove(0);
                            } else {
                                return c.getValue().get(0);
                            }
                        }
                    }
                }
            }
            return null;
        }
    }

}