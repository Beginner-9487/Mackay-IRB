package com.example.mackayirb.adapter;

import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mackayirb.R;
import com.example.mackayirb.data.central.FootLabelData;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FootIMUDataMonitorAdapter extends LineChartDataMonitorAdapter<ArrayList<ArrayList<Entry>>, FootIMUDataMonitorAdapter.DataViewHolder> {

    public static ArrayList<ArrayList<String>> AllData = FootLabelData.getAllImuNameList();

    @Override
    public int LayoutId() {
        return R.layout.listitem_foot_imu;
    }

    @Override
    public FootIMUDataMonitorAdapter.DataViewHolder getDataViewHolder(View view) {
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChartDataMonitorAdapter.DataViewHolder h, int position) {
        FootIMUDataMonitorAdapter.DataViewHolder holder = (FootIMUDataMonitorAdapter.DataViewHolder) h;

//        Log.d("position: " + String.valueOf(position));

        final String[] FootViceDataLabels = BasicResourceManager.getResources().getStringArray(R.array.FootIMUDataLabels);

        holder.adapter = new NormalAdapter();

        Set<ArrayList<ArrayList<Entry>>> keySet = getLabelDataWithColor().keySet();
        List<ArrayList<ArrayList<Entry>>> listKeys = new ArrayList<>(keySet);

//        Log.d("X: " + String.valueOf((int) getX()));
        holder.adapter.add("Index: " + String.valueOf(getX()), OtherUsefulFunction.getBWColor(BasicResourceManager.getResources()), null);
//        Log.d("listKeys.size(): " + String.valueOf(listKeys.size()));
        try {
            int i=0;
            for (String s:FootViceDataLabels) {
//                Log.d("i: " + String.valueOf(i));
//                Log.d("listKeys.get(position).size(): " + String.valueOf(listKeys.get(position).size()));
//                Log.d("listKeys.get(position).get(i).size(): " + String.valueOf(listKeys.get(position).get(i).size()));
                holder.adapter.add(s + ": " + String.valueOf(listKeys.get(position).get(i).get((int) getX()).getY()), OtherUsefulFunction.getBWColor(BasicResourceManager.getResources()), null);
                i++;
            }
        } catch (Exception e) {
//            Log.e(String.valueOf(position) + "," + String.valueOf((int) getX()));
        }

        holder.recyclerView_ViceItemList.setAdapter(holder.adapter);
    }

    public class DataViewHolder extends ChartDataMonitorAdapter.DataViewHolder {
        RecyclerView recyclerView_ViceItemList;
        NormalAdapter adapter;
        public DataViewHolder(View itemView) {
            super(itemView);
            recyclerView_ViceItemList = itemView.findViewById(R.id.ViceItemList);
            DisplayMetrics displaymetrics = new DisplayMetrics();
            BasicResourceManager.getCurrentActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels;
            int buttonWidth = width/2;
            recyclerView_ViceItemList.setMinimumWidth(buttonWidth);
            recyclerView_ViceItemList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }
}
