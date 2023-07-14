package com.example.mackayirb.ui;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.mackayirb.R;
import com.example.mackayirb.utils.OtherUsefulFunction;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MackayTest extends InitTest {

    public int getDataMAX() {
        return 70;
    }
    @Override
    public void createNewData() {

        byte type = (byte) (Math.random() * getActivityRule().getActivity().getResources().getStringArray(R.array.TypeLabels).length);

        for(int i=0; i<getDataMAX(); i++) {
            getPreparedFakeData().bleData.lastReceivedData.get(
                    getPreparedFakeData().bluetoothGattService).get(
                    getPreparedFakeData().bluetoothGattCharacteristic).add(
                    OtherUsefulFunction.concatWithArrayCopy(
                            new byte[]{
                                    type,
                                    0x00, (byte) (getDataMAX()), 0x00, (byte) (i+1), 0x00, 0x00, (byte) (i*100/256), (byte) (i*100%256), 0x00, 0x00,
                            },
                            getRandomByteArray(4)
                    )
            );
        }
    }

}
