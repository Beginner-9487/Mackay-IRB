package com.example.mackayirb.ui;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.mackayirb.data.central.FakeBytesGiver;

import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MackayTest extends InitTest {

    @Override
    public int getNumberOfDataInLabel() {
        return FakeBytesGiver.getNumberOfDataInLabel();
    }

}
