package com.example.mackayirb.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.mackayirb.R;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;

public class PermissionsFragment extends Fragment {

    public OnMessageSentListener messageSentListener;
    public interface OnMessageSentListener {
        void onMessageSent(String message);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        try {
//            messageSentListener = (OnMessageSentListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + " must implement OnMessageSentListener");
//        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        BasicResourceManager.setCurrentFragment(this);
        Log.d("onCreated");
        if (messageSentListener != null) {
            messageSentListener.onMessageSent("onCreated");
        }
        return inflater.inflate(R.layout.fragment_empty, container, false);
    }
}
