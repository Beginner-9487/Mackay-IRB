package com.example.mackayirb.ui.central;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.mackayirb.R;
import com.example.mackayirb.adapter.MackayDataMonitorAdapter;
import com.example.mackayirb.adapter.MackayForRPNDataMonitorAdapter;
import com.example.mackayirb.data.central.MackayManagerData;
import com.example.mackayirb.data.central.MackayLabelData;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Map;

public class CentralMackayForRPNFragment extends CentralMackayFragment implements CentralMvpView {

    LinearLayout Operation_LL;

    MackayForRPNDataMonitorAdapter mackayForRPNDataMonitorAdapter;
    public void setDataMonitorAdapter() {
        mackayForRPNDataMonitorAdapter = new MackayForRPNDataMonitorAdapter();
    }
    public MackayDataMonitorAdapter getDataMonitorAdapter() {
        return mackayForRPNDataMonitorAdapter;
    }

    @Override
    public boolean HaveDataMonitorSelector() {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        Operation_LL = view.findViewById(R.id.Operation_LL);
        Operation_LL.setVisibility(View.GONE);
        editHighlightSelector.setVisibility(View.GONE);
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
    public ArrayList<MackayLabelData> getDataSource() {
        return new ArrayList<>(((MackayManagerData) (mCentralPresenter.getCentralDataManager())).getLatestLabelData().values());
    }

    @Override
    public ArrayList<Boolean> setShowArrayForChart() {
        return superSetShowArrayForChart();
    }

}
