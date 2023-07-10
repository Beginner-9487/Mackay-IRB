package com.example.mackayirb.ui;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.mackayirb.R;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class FootTest extends InitTest {

    public int numberOfTypes() {
        return 2;
    }
    public int getDataMAX() {
        return 2;
    }
    @Override
    public void createNewData() {

        byte type = (byte) (Math.random() * numberOfTypes() + 0x0a);

        for(int i=0; i<getDataMAX(); i++) {
            getPreparedFakeData().bleData.lastReceivedData.get(
                    getPreparedFakeData().bluetoothGattService).get(
                    getPreparedFakeData().bluetoothGattCharacteristic).add(
                    new byte[]{
                            type,
                            (byte) i,
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                            getRandomByte(),
                    }
            );
        }
    }

}
