package com.example.mackayirb.adapter;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mackayirb.data.central.CentralLabelData;
import com.example.mackayirb.data.central.MackayLabelData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public abstract class LineChartDataMonitorAdapter<LabelData, DataViewHolder extends ChartDataMonitorAdapter.DataViewHolder> extends ChartDataMonitorAdapter<DataViewHolder> {

    private final LinkedHashMap<LabelData, Integer> labelDataWithColor = new LinkedHashMap<>();
    public LinkedHashMap<LabelData, Integer> getLabelDataWithColor() {
        return labelDataWithColor;
    }

    private float x = 0.0f;
    public void setX(float x) {
        this.x = x;
    }
    public float getX() {
        return this.x;
    }

    @Override
    public int getItemCount() {
        return labelDataWithColor.size();
    }

    public LabelData getDataAtIndex(int index) {
        if (index < this.labelDataWithColor.size()) {
            Set<LabelData> keySet = labelDataWithColor.keySet();
            List<LabelData> listKeys = new ArrayList<LabelData>(keySet);
            return listKeys.get(index);
        }

        return null;
    }

    public void addData(LabelData labelData, int color) {
        Set<LabelData> keySet = this.labelDataWithColor.keySet();
        if (labelData != null && !keySet.contains(labelData)) {
            this.labelDataWithColor.put(labelData, color);
        }
    }

    public void clearData() {
        this.labelDataWithColor.clear();
    }

}
