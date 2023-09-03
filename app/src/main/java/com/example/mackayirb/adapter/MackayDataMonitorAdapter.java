package com.example.mackayirb.adapter;

import android.view.View;
import android.widget.TextView;

import com.example.mackayirb.R;
import com.example.mackayirb.data.central.MackayLabelData;
import com.example.mackayirb.utils.OtherUsefulFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MackayDataMonitorAdapter extends LineChartDataMonitorAdapter<MackayLabelData, MackayDataMonitorAdapter.DataViewHolder> {

    @Override
    public int LayoutId() {
        return R.layout.listitem_mackay;
    }

    @Override
    public DataViewHolder getDataViewHolder(View view) {
        return new MackayDataMonitorAdapter.DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChartDataMonitorAdapter.DataViewHolder h, final int position) {
        MackayDataMonitorAdapter.DataViewHolder holder = (MackayDataMonitorAdapter.DataViewHolder) h;
        Set<Map.Entry<MackayLabelData, Integer>> keySet = getLabelDataWithColor().entrySet();
        List<Map.Entry<MackayLabelData, Integer>> listKeys = new ArrayList<>(keySet);

        MackayLabelData labelData = listKeys.get(position).getKey();
        DataViewHolder newHolder = (DataViewHolder) holder;

        if(!isCardVisible(newHolder, labelData)){ return; }
        int textColor = listKeys.get(position).getValue();

        setCardLabelName(newHolder, labelData, textColor);
        setCardType(newHolder, labelData, textColor);
        setCardLabelX(newHolder, labelData, textColor);
        setCardLabelY(newHolder, labelData, textColor);

        setCardX(newHolder, textColor);
        setCardYList(newHolder, labelData, textColor);
    }

    public boolean isCardVisible(DataViewHolder holder, MackayLabelData labelData) {
        holder.dataCard.setVisibility(View.VISIBLE);
        return true;
    }
    public void setCardLabelName(DataViewHolder holder, MackayLabelData labelData, int TextColor) {
        holder.labelName.setText(holder.resources.getString(R.string.LabelName) + ": " + labelData.labelName);
        holder.labelName.setTextColor(TextColor);
    }

    public void setCardType(DataViewHolder holder, MackayLabelData labelData, int TextColor) {
        holder.type.setText(holder.resources.getString(R.string.Type) + ": " + labelData.getRelatedUnit(MackayLabelData.TypeLabel) + " (" + String.valueOf(labelData.type) + ")");
        holder.type.setTextColor(TextColor);
    }
    public void setCardLabelX(DataViewHolder holder, MackayLabelData labelData, int TextColor) {
        holder.labelX.setText(labelData.getRelatedUnit(MackayLabelData.XLabel) + " (" + labelData.getRelatedUnit(MackayLabelData.XUnit) + ") : ");
        holder.labelX.setTextColor(TextColor);
    }
    public void setCardLabelY(DataViewHolder holder, MackayLabelData labelData, int TextColor) {
        holder.labelY.setText(labelData.getRelatedUnit(MackayLabelData.SpecialLabel) + " (" + labelData.getRelatedUnit(MackayLabelData.YUnit) + ") : ");
        holder.labelY.setTextColor(TextColor);
    }
    public void setCardX(DataViewHolder holder, int TextColor) {
        getDf().setMaximumFractionDigits(340);
        holder.dataX.setText(getDf().format(getX()));
        holder.dataX.setTextColor(TextColor);
    }
    public void setCardYList(DataViewHolder holder, MackayLabelData labelData, int TextColor) {
        getDf().setMaximumFractionDigits(340);
        String yString = "";
        boolean lock = true;
        for (Float y:labelData.getYByX(getX())) {
            // Log.e(String.valueOf(y));
            if(lock) { lock = false; }
            else { yString += "\n"; }
            yString += getDf().format(y);
        }
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, yString.split("\n"));
        // holder.dataYList.setAdapter(adapter);
        holder.dataYList.setText(yString);
        holder.dataYList.setTextColor(TextColor);
    }

    public class DataViewHolder extends ChartDataMonitorAdapter.DataViewHolder {
        TextView labelName;
        TextView type;
        TextView labelX;
        TextView dataX;
        TextView labelY;
        TextView dataYList;
        public DataViewHolder(View itemView) {
            super(itemView);
            labelName = (TextView) itemView.findViewById(R.id.labelName);
            type = (TextView) itemView.findViewById(R.id.type);
            labelX = (TextView) itemView.findViewById(R.id.labelX);
            dataX = (TextView) itemView.findViewById(R.id.dataX);
            labelY = (TextView) itemView.findViewById(R.id.labelY);
            dataYList = (TextView) itemView.findViewById(R.id.dataYList);
        }
    }

}
