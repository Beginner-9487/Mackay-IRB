package com.example.mackayirb.ui.central;

import android.content.Context;
import android.util.AttributeSet;

import com.example.mackayirb.utils.MyLineChart;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MackayLineChart extends MyLineChart {

    public MackayLineChart(Context context) {
        super(context);
    }

    public MackayLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MackayLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setLineDataAttribute(List<ILineDataSet> iLineDataSet) {
        for (ILineDataSet l:iLineDataSet) {
            LineDataSet lineDataSet = (LineDataSet) l;
            float line_width = 1.8f;
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            lineDataSet.setCubicIntensity(0.2f);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setLineWidth(line_width);
            lineDataSet.setHighlightLineWidth(line_width);
            lineDataSet.setDrawHorizontalHighlightIndicator(false);

            lineDataSet.setHighLightColor(OtherUsefulFunction.getBWColor(getResources()));
            lineDataSet.setColor(OtherUsefulFunction.getDataColor(getResources(), getDataIndex(l), groupList.size(), 0.5f));
            lineDataSet.setValueTextColor(OtherUsefulFunction.getDataColor(getResources(), getDataIndex(l), groupList.size(), 0.5f));
        }
    }
}
