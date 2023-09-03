package com.example.mackayirb.ui.central;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mackayirb.R;
import com.example.mackayirb.adapter.FootDataMonitorAdapter;
import com.example.mackayirb.data.central.FootManagerData;
import com.example.mackayirb.data.central.FootLabelData;
import com.example.mackayirb.data.central.MackayManagerData;
import com.example.mackayirb.data.central.MackayLabelData;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Map;

public class CentralFootIMUFragment extends CentralChartWithMonitorFragment<FootDataMonitorAdapter> implements CentralMvpView {

    public ArrayList<ArrayList<String>> AllData = FootLabelData.getAllViceNameList();
    public ArrayList<ArrayList<ArrayList<Entry>>> entryList = new ArrayList<>();

    @Override
    public LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chart_with_recycle;
    }

    @Override
    public void setDataMonitorAdapter() {
        dataMonitorAdapter = new FootDataMonitorAdapter();
    }

    @Override
    public ArrayList<ILineDataSet> setLineDataSetsForChart() {
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        this.entryList = ((FootManagerData) (mCentralPresenter.getCentralDataManager())).getEntryListSequencedByPosition();
//        Log.d("0: " + String.valueOf(entryList.size()));
        int i = 0;
        for (ArrayList<ArrayList<Entry>> entryList : this.entryList) {
            int j = 0;
//            Log.d("entryList: " + String.valueOf(entryList.size()));
            for (ArrayList<Entry> entries : entryList) {
                iLineDataSets.add(new LineDataSet(entries, AllData.get(i).get(j)));
//                Log.d("j: " + String.valueOf(j));
                j++;
            }
//            Log.d("i: " + String.valueOf(j));
            i++;
        }
//        Log.d("1: " + String.valueOf(entryList.size()));
        return iLineDataSets;
    }

    @Override
    public void addDataIntoMonitor() {
//        Log.d("A: " + String.valueOf(this.entryList.size()));
        for (ArrayList<ArrayList<Entry>> entries : this.entryList) {
//            Log.d("B: " + String.valueOf(entries.size()));
//            Log.d("C: " + String.valueOf(entries.get(0).size()));
            getDataMonitorAdapter().addData(entries, 0);
        }
    }

}
