package com.example.mackayirb.adapter;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mackayirb.R;
import com.example.mackayirb.data.central.FootLabelData;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FootPathDataMonitorAdapter extends LineChartDataMonitorAdapter<String, FootPathDataMonitorAdapter.DataViewHolder> {

    @Override
    public int LayoutId() {
        return R.layout.listitem_foot_path;
    }

    @Override
    public FootPathDataMonitorAdapter.DataViewHolder getDataViewHolder(View view) {
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartDataMonitorAdapter.DataViewHolder h, int position) {
        FootPathDataMonitorAdapter.DataViewHolder holder = (FootPathDataMonitorAdapter.DataViewHolder) h;

        holder.textView.setText(String.valueOf(position) + ": " + getDataAtIndex(position));
        holder.textView.setTextColor(OtherUsefulFunction.getBWColor(BasicResourceManager.getResources()));
    }

    public class DataViewHolder extends ChartDataMonitorAdapter.DataViewHolder {
        TextView textView;
        public DataViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.Info);
        }
    }
}
