package com.example.mackayirb.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.mackayirb.R;
import com.example.mackayirb.data.central.FakeBytesGiver;
import com.example.mackayirb.utils.BasicResourceManager;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MackayDeveloperTest extends MackayTest {

    @Override
    public int getMode() {
        return BasicResourceManager.SharedPreferencesManager.MackayDeveloperMode;
    }

    private int numberOfData = 5;
    @Test
    public void TestDeveloperLiZe() {

        onView(withId(R.id.ViewPager))
                .check(matches(isDisplayed()))
                .perform(swipeLeft());

        for(int i=0; i<numberOfData; i++) {
            onView(withId(R.id.DataName_Text))
                    .check(matches(isDisplayed()))
                    .perform(clearText())
                    .perform(typeText("LiZe" + String.valueOf(i)));
            onView(withId(R.id.ViewPager))
                    .check(matches(isDisplayed()))
                    .perform(swipeLeft());
            createNewData();
            onView(withId(R.id.ViewPager))
                    .check(matches(isDisplayed()))
                    .perform(swipeRight());
        }

        onView(withId(R.id.ViewPager))
                .check(matches(isDisplayed()))
                .perform(swipeLeft());

        while (true) {}

    }


    @Test
    public void TestDeveloperXieZhiLong() {

        onView(withId(R.id.ViewPager))
                .check(matches(isDisplayed()))
                .perform(swipeLeft());

        onView(withId(R.id.RadioXieZhiLong))
                .perform(click());

        onView(withId(R.id.DataName_Text))
                .check(matches(isDisplayed()))
                .perform(clearText())
                .perform(typeText("XZL"));

        onView(withId(R.id.ViewPager))
                .check(matches(isDisplayed()))
                .perform(swipeLeft());

        for(int i=0; i<numberOfData; i++) {
            FakeBytesGiver.currentType = FakeBytesGiver.getNewType();
            createNewData();
        }

        while (true) {}

    }

}
