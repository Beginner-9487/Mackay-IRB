package com.example.mackayirb.ui.central;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mackayirb.R;
import com.example.mackayirb.data.central.FootDataManager;
import com.example.mackayirb.data.central.FootDeviceData;
import com.example.mackayirb.data.central.FootLabelData;
import com.example.mackayirb.utils.FootCanvas;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.example.mackayirb.utils.ShapeData;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CentralFootFragment extends CentralFragment implements CentralMvpView {

    private FootCanvas canvasView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.init(context);
        mCentralPresenter.initForCentralDataManager(CentralPresenter.FootDataManager);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_foot_chart;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        canvasView = view.findViewById(R.id.FootCanvas);
        Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.foot_left);
        Bitmap image2 = BitmapFactory.decodeResource(getResources(), R.drawable.foot_right);
        canvasView.setBackgroundImages(image1, 0,  0);
        canvasView.setBackgroundImages(image2, image1.getWidth(),  0);

        return view;
    }

    @Override
    public void doSomethingFrequently() {
        try {
            FootDataManager footDataManager = ((FootDataManager) (mCentralPresenter.getCentralDataManager()));
            parseShapeData(footDataManager.deviceData.get(0).labelData.get(0).getNewestFeet());
            parseLineData(footDataManager.deviceData.get(0).labelData.get(0).getFeetSortedByPosition());
            canvasView.setData(shapeData, lineData);
        } catch (Exception e) {}
    }

    List<ShapeData> shapeData = new ArrayList<>();
    LineData lineData = new LineData();
    public void parseShapeData(ArrayList<FootLabelData.Foot> feet) {
        List<ShapeData> data = new ArrayList<>();
//        Log.d(String.valueOf(feet.size()));
        for (FootLabelData.Foot foot:feet) {
            ArrayList<Entry> xy = FootLabelData.SensorPositions.getPositions(foot.position);
            int index = 0;
//            Log.d(String.valueOf(foot.pressures.size()));
            for (Float pressure:foot.pressures) {
                data.add(new ShapeData(
                        ShapeData.POINT,
                        xy.get(index).getX(),
                        xy.get(index).getY(),
                        pressure,
                        1.0f
                ));
                index++;
            }
        }
//        Log.d(String.valueOf(data.size()));
        shapeData = data;
    }

    public void parseLineData(ArrayList<ArrayList<FootLabelData.Foot>> sortedFeet) {
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        int index = 0;
//        Log.d(String.valueOf(sortedFeet.size()));
        for (ArrayList<FootLabelData.Foot> feet:sortedFeet) {
//            Log.d(String.valueOf(feet.size()));
            ArrayList<Entry> centers = new ArrayList<>();
            for (FootLabelData.Foot foot:feet) {
                centers.add(foot.pressureCenter);
            }
//            Log.d(String.valueOf(centers.size()));
            iLineDataSets.add(new LineDataSet(centers, String.valueOf(index)));
            index++;
        }
        lineData = new LineData(iLineDataSets);
    }

}
