package com.example.mackayirb.adapter;

import android.view.View;

import com.example.mackayirb.R;
import com.example.mackayirb.data.central.MackayLabelData;

public class MackayForRPNDataMonitorAdapter extends MackayDataMonitorAdapter {

    @Override
    public int LayoutId() {
        return R.layout.listitem_mackay_rpn;
    }

    @Override
    public boolean isCardVisible(DataViewHolder holder, MackayLabelData labelData) {
        holder.dataCard.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public void setCardLabelName(DataViewHolder holder, MackayLabelData labelData, int TextColor) {
    }

}
