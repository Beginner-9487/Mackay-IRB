package com.example.mackayirb.ui.central;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.mackayirb.R;
import com.example.mackayirb.adapter.ChartDataMonitorAdapter;
import com.example.mackayirb.data.central.MackayDataManager;
import com.example.mackayirb.data.central.MackayLabelData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class CentralChartForRPNFragment extends CentralChartFragment implements CentralMvpView {

    LinearLayout Operation_LL;

    @Override
    public int getDataMonitorMode() {
        return ChartDataMonitorAdapter.RPNMode;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Operation_LL = view.findViewById(R.id.Operation_LL);
        Operation_LL.setVisibility(View.GONE);
        editHighlightSelector.setVisibility(View.GONE);
        setLayoutId(false);
        return view;
    }

    @Override
    public void updateChart() {
        setLineData();
        setHighlight(5.0f);
        mLineChart.refreshChart();
        showHighlightedData();
    }

    @Override
    public void setLineData() {
        try {
            iLineDataSets.clear();
            for (MackayLabelData labelData:((MackayDataManager) mCentralPresenter.getCentralDataManager()).getLatestLabelData().values()) {
                iLineDataSets.add(new LineDataSet(labelData.getSpecialEntries(), labelData.labelName));
            }
            mLineChart.setMyData(new LineData(iLineDataSets));
            showAllLineData();
        } catch (Exception e) {}
    }

    public void showAllLineData() {
        ArrayList<Boolean> ShownArray = new ArrayList<>();
        for(int i=0; i<iLineDataSets.size(); i++) {
            ShownArray.add(true);
        }
        mLineChart.setAllShowArray(ShownArray);
        mLineChart.refreshChart();
    }

}
