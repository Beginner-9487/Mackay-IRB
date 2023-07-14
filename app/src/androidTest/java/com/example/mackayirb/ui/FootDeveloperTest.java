package com.example.mackayirb.ui;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.mackayirb.utils.BasicResourceManager;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class FootDeveloperTest extends FootTest {

    @Override
    public int getMode() {
        return BasicResourceManager.SharedPreferencesManager.FootDeveloperMode;
    }

    private int numberOfData = 5;
    @Test
    public void TestDeveloperFoot() {

        for(int i=0; i<numberOfData; i++) {
            createNewData();
        }

        while (true) {}

    }

}
