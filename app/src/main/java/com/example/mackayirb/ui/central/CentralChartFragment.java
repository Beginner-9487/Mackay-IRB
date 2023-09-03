package com.example.mackayirb.ui.central;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mackayirb.R;
import com.example.mackayirb.data.central.MackayManagerData;
import com.example.mackayirb.utils.MyLineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public abstract class CentralChartFragment extends CentralFragment implements CentralMvpView {

    MyLineChart mLineChart;

    @Override
    public void doSomethingFrequently() {
        updateChart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view = initView(view);
        return view;
    }
    public View initView(View view) {
        mLineChart = view.findViewById(R.id.LineChart);
        mLineChart.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // Check if the view's size has changed
                if (right - left != oldRight - oldLeft || bottom - top != oldBottom - oldTop) {
                    // View size has changed, do something
                    // For example, update the layout or perform some calculations
                    mLineChart.refreshChart();
                    // Log.e(String.valueOf(left) + ", " + top + ", " + right + ", " + bottom + ", " + oldLeft + ", " + oldTop + ", " + oldRight + ", " + oldBottom);
                }
            }
        });
        return view;
    }

    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    ArrayList<Boolean> showArray = new ArrayList<>();
    public void updateChart() {
        setLineData();
        mLineChart.refreshChart();
    }
    public void setLineData() {
        try {
            iLineDataSets = setLineDataSetsForChart();
            mLineChart.setMyData(new LineData(iLineDataSets));
            showArray = setShowArrayForChart();
            mLineChart.setAllShowArray(showArray);
        } catch (Exception e) {}
    }

    public abstract ArrayList<ILineDataSet> setLineDataSetsForChart();
    public ArrayList<Boolean> setShowArrayForChart() {
        ArrayList<Boolean> showArray = new ArrayList<>();
        for (ILineDataSet l:iLineDataSets) {
            showArray.add(true);
        }
        return showArray;
    }

    public void removeHighlight() {
        mLineChart.highlightValue(null);
    }
    public void setHighlight(float x, int dataSetIndex) {
        mLineChart.highlightValue(x, dataSetIndex);
    }
    public void setHighlight(float x) {
        mLineChart.highlightValue(x);
    }
}

