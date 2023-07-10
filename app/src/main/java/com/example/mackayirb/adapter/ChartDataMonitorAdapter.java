package com.example.mackayirb.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mackayirb.R;
import com.example.mackayirb.data.central.MackayLabelData;
import com.example.mackayirb.utils.OtherUsefulFunction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ChartDataMonitorAdapter extends RecyclerView.Adapter<ChartDataMonitorAdapter.DataViewHolder> {

    private DecimalFormat df = new DecimalFormat("0");
    private ChartDataMonitorAdapter.DataItemClickListener mListener;
    private View view;
    private final List<MackayLabelData> mLabelData = new ArrayList<>();

    private float x = 0.0f;
    public void setX(float X) {
        x = X;
    }

    private int mode = NormalMode;
    public final static int NormalMode = 0;
    public final static int RPNMode = 1;
    public ChartDataMonitorAdapter setMode(int Mode) {
        mode = Mode;
        return this;
    }

    @Override
    public ChartDataMonitorAdapter.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = R.layout.central_chart_item;
        switch (mode) {
            case NormalMode:
                layout = R.layout.central_chart_item;
                break;
            case RPNMode:
                layout = R.layout.central_chart_item_rpn;
                break;
        }

        view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        ChartDataMonitorAdapter.DataViewHolder viewHolder = new ChartDataMonitorAdapter.DataViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ChartDataMonitorAdapter.DataViewHolder holder, final int position) {
        MackayLabelData labelData = mLabelData.get(position);

        if(!isCardVisible(holder, labelData)){ return; }
        int textColor = OtherUsefulFunction.GetDataColor(holder.resources, position, mLabelData.size(), 0.5f);

        setCardLabelName(holder, labelData, textColor);
        setCardType(holder, labelData, textColor);
        setCardLabelX(holder, labelData, textColor);
        setCardLabelY(holder, labelData, textColor);

        setCardX(holder, textColor);
        setCardYList(holder, labelData, textColor);
    }

    public boolean isCardVisible(ChartDataMonitorAdapter.DataViewHolder holder, MackayLabelData labelData) {
        switch (mode) {
            case NormalMode:
                if(!labelData.show) {
                    holder.dataCard.setVisibility(View.GONE);
                    return false;
                }
        }
        holder.dataCard.setVisibility(View.VISIBLE);
        return true;
    }
    public void setCardLabelName(ChartDataMonitorAdapter.DataViewHolder holder, MackayLabelData labelData, int TextColor) {
        switch (mode) {
            case NormalMode:
                holder.labelName.setText(holder.resources.getString(R.string.LabelName) + ": " + labelData.labelName);
                holder.labelName.setTextColor(TextColor);
        }
    }
    public void setCardType(ChartDataMonitorAdapter.DataViewHolder holder, MackayLabelData labelData, int TextColor) {
        holder.type.setText(holder.resources.getString(R.string.Type) + ": " + labelData.getRelatedUnit(MackayLabelData.TypeLabel) + " (" + String.valueOf(labelData.type) + ")");
        holder.type.setTextColor(TextColor);
    }
    public void setCardLabelX(ChartDataMonitorAdapter.DataViewHolder holder, MackayLabelData labelData, int TextColor) {
        holder.labelX.setText(labelData.getRelatedUnit(MackayLabelData.XLabel) + " (" + labelData.getRelatedUnit(MackayLabelData.XUnit) + ") : ");
        holder.labelX.setTextColor(TextColor);
    }
    public void setCardLabelY(ChartDataMonitorAdapter.DataViewHolder holder, MackayLabelData labelData, int TextColor) {
        holder.labelY.setText(labelData.getRelatedUnit(MackayLabelData.SpecialLabel) + " (" + labelData.getRelatedUnit(MackayLabelData.YUnit) + ") : ");
        holder.labelY.setTextColor(TextColor);
    }
    public void setCardX(ChartDataMonitorAdapter.DataViewHolder holder, int TextColor) {
        df.setMaximumFractionDigits(340);
        holder.dataX.setText(df.format(x));
        holder.dataX.setTextColor(TextColor);
    }
    public void setCardYList(ChartDataMonitorAdapter.DataViewHolder holder, MackayLabelData labelData, int TextColor) {
        df.setMaximumFractionDigits(340);
        String yString = "";
        boolean lock = true;
        for (Float y:labelData.getYByX(x)) {
            // Log.e(String.valueOf(y));
            if(lock) { lock = false; }
            else { yString += "\n"; }
            yString += df.format(y);
        }
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_list_item_1, yString.split("\n"));
        // holder.dataYList.setAdapter(adapter);
        holder.dataYList.setText(yString);
        holder.dataYList.setTextColor(TextColor);
    }

    @Override
    public int getItemCount() {
        return mLabelData.size();
    }

    public MackayLabelData getDataAtIndex(int index) {
        if (index < mLabelData.size()) {
            return mLabelData.get(index);
        }

        return null;
    }

    public void addData(MackayLabelData labelData) {
        if (labelData != null && !mLabelData.contains(labelData)) {
            mLabelData.add(labelData);
        }
    }

    public void clearData() {
        mLabelData.clear();
    }

    public void setData(ArrayList<MackayLabelData> LabelData) {
        clearData();
        for (int i=0; i<LabelData.size(); i++) {
            addData(LabelData.get(i));
        }
    }

    public void setListener(ChartDataMonitorAdapter.DataItemClickListener listener) {
        mListener = listener;
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        Resources resources;

        androidx.cardview.widget.CardView dataCard;
        TextView labelName;
        TextView type;
        TextView labelX;
        TextView dataX;
        TextView labelY;
        TextView dataYList;

        public DataViewHolder(View itemView) {
            super(itemView);
            resources = itemView.getResources();

            dataCard = (androidx.cardview.widget.CardView) itemView.findViewById(R.id.dataCard);
            labelName = (TextView) itemView.findViewById(R.id.labelName);
            type = (TextView) itemView.findViewById(R.id.type);
            labelX = (TextView) itemView.findViewById(R.id.labelX);
            dataX = (TextView) itemView.findViewById(R.id.dataX);
            labelY = (TextView) itemView.findViewById(R.id.labelY);
            dataYList = (TextView) itemView.findViewById(R.id.dataYList);
        }
    }

    public interface DataItemClickListener { }
}
