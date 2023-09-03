package com.example.mackayirb.ui.central;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mackayirb.R;
import com.example.mackayirb.adapter.ChartDataMonitorAdapter;
import com.example.mackayirb.adapter.LineChartDataMonitorAdapter;
import com.example.mackayirb.utils.Log;

public abstract class CentralChartWithMonitorFragment<Monitor extends LineChartDataMonitorAdapter> extends CentralChartFragment implements CentralMvpView {

    EditText editHighlightSelector;
    RecyclerView dataMonitor;
    public Monitor dataMonitorAdapter;
    public abstract void setDataMonitorAdapter();
    public Monitor getDataMonitorAdapter() {
        return dataMonitorAdapter;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view = initDataMonitor(view);
        return view;
    }
    @Override
    public View initView(View view) {
        super.initView(view);

        editHighlightSelector = view.findViewById(R.id.HighlightSelector);
        editHighlightSelector.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    // Log.e(charSequence.toString());
                    setHighlight(Float.valueOf(charSequence.toString()));
                } catch (Exception e) {
                    Log.e(e.getMessage());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        return view;
    }
    public View initDataMonitor(View view) {
        setDataMonitorAdapter();
        dataMonitor = view.findViewById(R.id.DataMonitor);
        dataMonitor.setLayoutManager(new LinearLayoutManager(getContext()));
        dataMonitor.setAdapter(getDataMonitorAdapter());
        dataMonitor.setHasFixedSize(true);
        dataMonitor.setItemAnimator(new DefaultItemAnimator());
        dataMonitor.setLayoutManager(getLinearLayoutManager());
        getDataMonitorAdapter().setListener(new ChartDataMonitorAdapter.DataItemClickListener() {});
        return view;
    }
    public LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    public void updateChart() {
        super.updateChart();
        showHighlightedData();
    }

    public void showHighlightedData() {
        if(mLineChart.getHighlighted() == null) {
            if(getDataMonitorAdapter() != null) {
                // mLineChart.zoomOut();    // auto zoom out
            }
        } else {
            if(getDataMonitorAdapter() != null) {
                getDataMonitorAdapter().setX(mLineChart.getHighlighted()[0].getX());
                getDataMonitorAdapter().clearData();
                addDataIntoMonitor();
                getDataMonitorAdapter().notifyDataSetChanged();
            }
        }
    }

    public abstract void addDataIntoMonitor();

    @Override
    public void removeHighlight() {
        super.removeHighlight();
        showHighlightedData();
    }
    @Override
    public void setHighlight(float x, int dataSetIndex) {
        super.setHighlight(x, dataSetIndex);
        showHighlightedData();
    }
    @Override
    public void setHighlight(float x) {
        super.setHighlight(x);
        showHighlightedData();
    }
}
