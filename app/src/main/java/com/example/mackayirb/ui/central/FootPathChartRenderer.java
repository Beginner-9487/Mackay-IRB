package com.example.mackayirb.ui.central;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.example.mackayirb.utils.Log;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.renderer.LineChartRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.List;

public class FootPathChartRenderer extends LineChartRenderer {
    public FootPathChartRenderer(LineDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    public Entry getIconsOffset(ILineDataSet dataSet) {
        MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
        return new Entry(
                Utils.convertDpToPixel(iconsOffset.x),
                Utils.convertDpToPixel(iconsOffset.y)
        );
    }

    public float[] getPositions(ILineDataSet dataSet) {
        mXBounds.set(mChart, dataSet);
        Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        try {
            return trans.generateTransformedValuesLine(dataSet, mAnimator.getPhaseX(), mAnimator
                    .getPhaseY(), mXBounds.min, mXBounds.max);
        } catch (Exception e) {
            return new float[]{};
        }
    }

    public Entry getIconEntry(ILineDataSet dataSet, int i) {
        i *= 2;
        float[] positions = getPositions(dataSet);
        if(i+1 >= positions.length) {
            return null;
        }
        float x = getPositions(dataSet)[i];
        float y = getPositions(dataSet)[i+1];
        return new Entry(
                x + getIconsOffset(dataSet).getX(),
                y + getIconsOffset(dataSet).getY()
        );
    }

}