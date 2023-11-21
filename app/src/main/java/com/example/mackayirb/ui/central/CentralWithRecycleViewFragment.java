package com.example.mackayirb.ui.central;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mackayirb.R;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public abstract class CentralWithRecycleViewFragment<RecyclerViewAdapter extends RecyclerView.Adapter> extends CentralFragment implements CentralMvpView {
    RecyclerView recyclerView;
    public RecyclerViewAdapter recyclerViewAdapter;
    public abstract void setRecyclerViewAdapter();
    public RecyclerViewAdapter getRecyclerViewAdapter() {
        return recyclerViewAdapter;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view = initView(view);
        return view;
    }
    public View initView(View view) {
        setRecyclerViewAdapter();
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(getRecyclerViewAdapter());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(getLinearLayoutManager());
        return view;
    }
    public LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }
    public abstract void updateData();
    @Override
    public void doSomethingFrequently() {
        updateData();
    }
}
