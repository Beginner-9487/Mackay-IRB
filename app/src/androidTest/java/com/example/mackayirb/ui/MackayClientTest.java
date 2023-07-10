package com.example.mackayirb.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.mackayirb.R;
import com.example.mackayirb.utils.BasicResourceManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MackayClientTest extends MackayTest {

    @Override
    public int getMode() {
        return BasicResourceManager.SharedPreferencesManager.MackayClientMode;
    }

    private int numberOfData = 5;
    @Test
    public void TestLiZe() {

        onView(withId(R.id.DataNameText))
                .check(matches(isDisplayed()))
                .perform(clearText())
                .perform(typeText("LiZe"))
                .perform(closeSoftKeyboard());

        onView(withId(R.id.ButtonStart))
                .check(matches(isDisplayed()))
                .perform(click());

        numberOfData = 1;
        for(int i=0; i<numberOfData; i++) {
            createNewData();
        }

        while (true) {}

    }


    @Test
    public void TestXieZhiLong() {

        onView(withId(R.id.DataNameText))
                .check(matches(isDisplayed()))
                .perform(clearText())
                .perform(typeText("XZL"))
                .perform(closeSoftKeyboard());

        onView(withId(R.id.ButtonStart))
                .check(matches(isDisplayed()))
                .perform(click());

        for(int i=0; i<numberOfData; i++) {
            createNewData();
        }

        while (true) {}

    }

}
