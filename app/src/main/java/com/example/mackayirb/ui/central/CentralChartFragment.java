package com.example.mackayirb.ui.central;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mackayirb.R;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.MyLineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public abstract class CentralChartFragment<LineChart extends MyLineChart> extends CentralFragment implements CentralMvpView {

    LineChart lineChart;
    public LineChart getLineChart() {
        return lineChart;
    }

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
        lineChart = view.findViewById(R.id.LineChart);
        lineChart.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // Check if the view's size has changed
                if (right - left != oldRight - oldLeft || bottom - top != oldBottom - oldTop) {
                    // View size has changed, do something
                    // For example, update the layout or perform some calculations
                    lineChart.refreshChart();
                    // Log.e(String.valueOf(left) + ", " + top + ", " + right + ", " + bottom + ", " + oldLeft + ", " + oldTop + ", " + oldRight + ", " + oldBottom);
                }
            }
        });
        return view;
    }

    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    ArrayList<Boolean> showArray = new ArrayList<>();
    public void updateChart() {
        if(!this.equals(BasicResourceManager.getCurrentFragment())) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        setLineData();
                        lineChart.refreshChart();
                    }
                });
            }
        }).start();
    }
    public void setLineData() {
        try {
            iLineDataSets = setLineDataSetsForChart();
            lineChart.setMyData(new LineData(iLineDataSets));
            showArray = setShowArrayForChart();
            lineChart.setAllShowArray(showArray);
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
        lineChart.highlightValue(null);
    }
    public void setHighlight(float x, int dataSetIndex) {
        lineChart.highlightValue(x, dataSetIndex);
    }
    public void setHighlight(float x) {
        lineChart.highlightValue(x);
    }
}

