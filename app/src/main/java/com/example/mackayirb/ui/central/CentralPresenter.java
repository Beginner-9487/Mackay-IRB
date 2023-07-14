package com.example.mackayirb.ui.central;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.data.central.CentralDataManager;
import com.example.mackayirb.data.ble.DataManager;
import com.example.mackayirb.data.central.FootDataManager;
import com.example.mackayirb.data.central.MackayDataManager;
import com.example.mackayirb.ui.base.BasePresenter;
import com.example.mackayirb.utils.Log;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 *
 */
public class CentralPresenter extends BasePresenter<CentralMvpView> {

    // =====================================================================================
    private DataManager myDataManager;
    public DataManager getDataManager() {
        return myDataManager;
    }

    // =====================================================================================
    private byte dataManagerType;
    private MackayDataManager myMackayDataManager;
    private FootDataManager myFootDataManager;
    public static final byte MackayDataManager = 0x00;
    public static final byte FootDataManager = 0x01;
    public CentralDataManager getCentralDataManager() {
        switch (dataManagerType) {
            case MackayDataManager:
                return myMackayDataManager;
            case FootDataManager:
                return myFootDataManager;
        }
        return null;
    }

    // =====================================================================================
    private Disposable mScanDisposable;
    private final HashMap<BluetoothDevice, Disposable> myConnectedDisposable;

    @Inject
    public CentralPresenter(DataManager dataManager, MackayDataManager mackayDataManager, FootDataManager footDataManager) {
        myDataManager = dataManager;
        myMackayDataManager = mackayDataManager;
        myFootDataManager = footDataManager;
        myConnectedDisposable = new HashMap<>();
    }

    @Override
    public void attachView(CentralMvpView centralView) {
        super.attachView(centralView);

    }

    public void initForBLEData() {
        List<BLEDataServer.BLEData> data = myDataManager.getRemoteBLEData();

        for(BLEDataServer.BLEData d: data) {
            getMvpView().showBLEData(d);
        }
    }

    public void initForCentralDataManager(byte type) {
        dataManagerType = type;
        getCentralDataManager().setup(myDataManager);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void getRemoteDevices() {
        try {
            checkViewAttached();

            List<BLEDataServer.BLEData> data = myDataManager.getRemoteBLEData();

            for(BLEDataServer.BLEData d: data) {
                getMvpView().showBLEDevice(d.device);
                getMvpView().showBLEData(d);
            }
        } catch (Exception e) {
            // Log.e(e.getMessage());
        }
    }

    public void scanBLEPeripheral(boolean enabled) {
        try {
            checkViewAttached();

            if (mScanDisposable != null && !mScanDisposable.isDisposed()) {
                mScanDisposable.dispose();
            }

            mScanDisposable = myDataManager.scanBLEPeripheral(enabled)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Consumer<BLEDataServer.BLEData>() {
                        @Override
                        public void accept(BLEDataServer.BLEData bleData) throws Exception {
                            if (isViewAttached()) {
                                getMvpView().showBLEDevice(bleData.device);
                                if (!myConnectedDisposable.containsKey(bleData.device)) { getMvpView().showBLEData(bleData); }
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
    }

    public void connectGatt(BluetoothDevice device) {
        try {
            checkViewAttached();

            // debugs here! if connect same bluetoothDevice multi times
            for (BluetoothDevice d : myConnectedDisposable.keySet()) {
                if (d == device) {
                    myConnectedDisposable.get(d).dispose();
                    myConnectedDisposable.remove(d);
                    break;
                }
            }

            Disposable s = myDataManager.connectGatt(device)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Consumer<BLEDataServer.BLEData>() {
                        @Override
                        public void accept(BLEDataServer.BLEData bleData) throws Exception {
                            if (isViewAttached()) {
                                getMvpView().showBLEData(bleData);
                            }
                        }
                    });

            myConnectedDisposable.put(device, s);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
    }

    public void disconnectGatt(BluetoothDevice device) {
        try {
            myDataManager.disconnectGatt(device);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
    }

    public void createBond(BLEDataServer.BLEData data) {
        try {
            checkViewAttached();
            myDataManager.createBond(data.device);
        } catch (Exception e) {
            Log.e(e.getMessage());
        }
    }

    public static final int READ_RSSI_REPEAT = 1;
    public final long READING_RSSI_TASK_FREQUENCY = 2000;
    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case READ_RSSI_REPEAT:
                    synchronized (CentralPresenter.this) {
                        for (BLEDataServer.BLEData d : myDataManager.getRemoteBLEData()) {
//                            readRemoteRssi(d.device);
                        }
                    }

                    sendMessageDelayed(
                            obtainMessage(READ_RSSI_REPEAT),
                            READING_RSSI_TASK_FREQUENCY
                    );
                    break;
            }
        }
    };

    public void startHandler(int task) {
        if (mHandler.hasMessages(task)) {
            return;
        }
        mHandler.sendEmptyMessage(task);
    }
    public void stopHandler(int task) {
        mHandler.removeMessages(task);
    }

    public boolean readRemoteRssi(BluetoothDevice device) {
        return myDataManager.readRemoteRssi(device);
    }

    public List<BLEDataServer.BLEData> getRemoteBLEData() {
        return myDataManager.getRemoteBLEData();
    }

    // =====================================================================================
    public void getAllBondedDevices() {
        try {
            checkViewAttached();

            // send all devices to view here;
            List<BLEDataServer.BLEData> data = myDataManager.getAllBondedDevices();

            for(BLEDataServer.BLEData d: data) {
                getMvpView().showBLEDevice(d.device);
                getMvpView().showBLEData(d);
            }

        } catch (Exception e) {
            Log.e(e.getMessage());
        }

    }

    // =====================================================================================
    public void SendToAllCharacteristic(byte[] command) {
        myDataManager.SendToAllCharacteristic(command);
    }

    public BLEDataServer getBLEDataServer() {
        return myDataManager.getBLEServer();
    }

}