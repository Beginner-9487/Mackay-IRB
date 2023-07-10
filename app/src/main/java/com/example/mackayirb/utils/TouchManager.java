package com.example.mackayirb.utils;

import android.view.MotionEvent;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

public class TouchManager {
    private ArrayList<Entry> touchList = new ArrayList<>();
    public void createTouch(int index) {
        while (touchList.size() <= index) {
            touchList.add(new Entry(0,0));
        }
    }
    public void setTouch(int index, float x, float y) {
        createTouch(index);
        Entry xy = touchList.get(index);
        xy.setX(x);
        xy.setY(y);
    }
    public Entry getTouch(int index) {
        createTouch(index);
        return touchList.get(index);
    }
    public Entry getCenter(int[] indexList) {
        Entry center = new Entry(0f, 0f);
        for (int index:indexList) {
            center.setX((getTouch(index).getX()));
            center.setY((getTouch(index).getY()));
        }
        center.setX(center.getX() / indexList.length);
        center.setY(center.getY() / indexList.length);
        return center;
    }
    public void setTouchByEvent(MotionEvent event) {
        for(int i=0; i<event.getPointerCount(); i++) {
            setTouch(i, event.getX(i), event.getY(i));
        }
    }
}
