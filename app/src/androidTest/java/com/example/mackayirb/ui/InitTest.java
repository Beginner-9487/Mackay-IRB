package com.example.mackayirb.ui;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import androidx.test.espresso.intent.Intents;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.example.mackayirb.SampleGattAttributes;
import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.data.ble.DataManager;
import com.example.mackayirb.ui.central.CentralPresenter;
import com.example.mackayirb.ui.main.MainActivity;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.CircularBuffer;
import com.example.mackayirb.data.central.FakeBytesGiver;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.UUID;

@RunWith(AndroidJUnit4ClassRunner.class)
public abstract class InitTest {

    public InitTest() {
        super();
        BasicResourceManager.isTesting = true;
    }

    @Rule
    public ActivityTestRule<MainActivity> activityRule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void setUp(){
        Intents.init();
        BasicResourceManager.SharedPreferencesManager.setModeController(getMode());
        // activityRule.getActivity().restartApp();

        // Wait for the activity to start
        getActivityInstance(Stage.CREATED);
        activityRule.getActivity().getCentralPresenter().stopHandler(CentralPresenter.READ_RSSI_REPEAT);
        preparedFakeData = createPreparedFakeData();
    }

    public ActivityTestRule<MainActivity> getActivityRule() {
        return activityRule;
    }

    ArrayList<Activity> currentActivity = null;
    private void getActivityInstance(final Stage stage) {
        currentActivity = null;
        getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                currentActivity = new ArrayList<>(ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(stage));
            }
        });
    }

    public int getMode() {
        return 0;
    }

    private PreparedFakeData preparedFakeData;
    public PreparedFakeData getPreparedFakeData() {
        return preparedFakeData;
    }
    class PreparedFakeData {
        BluetoothGattService bluetoothGattService;
        BluetoothGattCharacteristic bluetoothGattCharacteristic;
        BLEDataServer.BLEData bleData;
        public PreparedFakeData() {}
    }
    public PreparedFakeData createPreparedFakeData() {

        DataManager dataManager = getActivityRule().getActivity().getCentralPresenter().getDataManager();
        dataManager.getBLEServer().getBLEData().clear();

        // Device1
        BLEDataServer.BLEData bleData = dataManager.getBLEServer().findBLEDataByDevice(null);
        BluetoothGattService bluetoothGattService = new BluetoothGattService(UUID.randomUUID(), 0);
        BluetoothGattCharacteristic bluetoothGattCharacteristic = new BluetoothGattCharacteristic(UUID.fromString(SampleGattAttributes.Subscribed_UUIDs().get(0)), 0, 0);

        PreparedFakeData prepareToReturn = new PreparedFakeData();
        prepareToReturn.bluetoothGattService = bluetoothGattService;
        prepareToReturn.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
        prepareToReturn.bleData = bleData;
        prepareToReturn.bleData.DataBuffer = new CircularBuffer(BLEDataServer.BLEDataBufferSize);

        return prepareToReturn;
    }

    public abstract int getNumberOfDataInLabel();

    public void createNewData() {
        FakeBytesGiver.Index = 0;
        for(int i = 0; i< getNumberOfDataInLabel(); i++) {
            getPreparedFakeData().bleData.DataBuffer.addData(
                    FakeBytesGiver.getBytes()
            );
        }
    }
}
