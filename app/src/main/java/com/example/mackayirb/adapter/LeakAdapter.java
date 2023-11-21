package com.example.mackayirb.adapter;

import androidx.annotation.NonNull;

public class LeakAdapter extends NormalAdapter {
    public void add(String string, int color) {
        super.add(string, null, color);
    }
}
