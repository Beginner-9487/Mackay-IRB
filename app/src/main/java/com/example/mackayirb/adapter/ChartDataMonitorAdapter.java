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
import java.util.Map;
import java.util.Set;

public abstract class ChartDataMonitorAdapter<DataViewHolder extends ChartDataMonitorAdapter.DataViewHolder> extends RecyclerView.Adapter<ChartDataMonitorAdapter.DataViewHolder> {

    private DecimalFormat df = new DecimalFormat("0");
    public DecimalFormat getDf() {
        return df;
    }
    private ChartDataMonitorAdapter.DataItemClickListener mListener;
    private View view;

    public abstract int LayoutId();

    public abstract DataViewHolder getDataViewHolder(View view);

    @Override
    public ChartDataMonitorAdapter.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = LayoutId();

        view = LayoutInflater.from(parent.getContext())
                .inflate(layout, parent, false);

        ChartDataMonitorAdapter.DataViewHolder viewHolder = (ChartDataMonitorAdapter.DataViewHolder) getDataViewHolder(view);

        return viewHolder;
    }

    public void setListener(ChartDataMonitorAdapter.DataItemClickListener listener) {
        mListener = listener;
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        Resources resources;
        androidx.cardview.widget.CardView dataCard;
        public DataViewHolder(View itemView) {
            super(itemView);
            resources = itemView.getResources();
            dataCard = (androidx.cardview.widget.CardView) itemView.findViewById(R.id.dataCard);
        }
    }

    public interface DataItemClickListener { }
}
