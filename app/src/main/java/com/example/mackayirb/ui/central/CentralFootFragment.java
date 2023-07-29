package com.example.mackayirb.ui.central;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.ColorUtils;

import com.example.mackayirb.R;
import com.example.mackayirb.data.central.FootDataManager;
import com.example.mackayirb.data.central.FootLabelData;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.example.mackayirb.utils.ShapeData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
        canvasView.putBackgroundImage(image1, 0,  0);
        canvasView.putBackgroundImage(image2, image1.getWidth(),  0);

        return view;
    }

    @Override
    public void doSomethingFrequently() {
        try {
            FootDataManager footDataManager = ((FootDataManager) (mCentralPresenter.getCentralDataManager()));
            ArrayList<FootLabelData.Foot> eachDeviceFoot = new ArrayList<>();
            ArrayList<ArrayList<FootLabelData.Foot>> eachDeviceFeet = new ArrayList<>();
            for (int i=0; i<footDataManager.deviceData.size(); i++) {
                eachDeviceFoot.addAll(footDataManager.deviceData.get(i).labelData.get(0).getNewestFeet());
                eachDeviceFeet.add(footDataManager.deviceData.get(i).labelData.get(0).getFeet());
            }
            parseShapeData(eachDeviceFoot);
            canvasView.setShapeDataHash(shapeData);
            parseLineData(eachDeviceFeet);
            canvasView.setLineDataHash(lineData);
        } catch (Exception e) {}
    }

    // TODO Basic
    public float getCalibrationSlope() { return 0.f; }
    public float getCalibrationYIntercept() { return 70.f; }
    public float getCalibrationIntensity(float value, float max, float min) {
        return (value - min) / (max - min) * getCalibrationSlope() + getCalibrationYIntercept();
    }

    public Paint getBasicPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        return paint;
    }

    // TODO parseShapeData
    LinkedHashMap<ShapeData, Paint> shapeData = new LinkedHashMap<>();
    public void parseShapeData(ArrayList<FootLabelData.Foot> feet) {
        shapeData = new LinkedHashMap<>();
        for (FootLabelData.Foot foot:feet) {
            final ArrayList<Entry> SensorList = foot.getSensorList();
            handleTemperatures(SensorList, foot);
            handlePressures(SensorList, foot);
            handleShearForces(SensorList, foot);
        }
    }
    public void handleShearForces(final ArrayList<Entry> SensorList, FootLabelData.Foot foot) {
        float[] shearForces = foot.mainFloatList.get(FootLabelData.MainFloatList.ShearForce);
        float max = OtherUsefulFunction.getMaxOf(shearForces);
        float min = OtherUsefulFunction.getMinOf(shearForces);

        int index = 0;
        for (float shearForce:shearForces) {
            Paint paint = getBasicPaint();
            paint.setColor(
                    getDataColor(
                            BasicResourceManager.getResources(),
                            (int) (shearForce - min),
                            (int) ((max - min) * 1.f),
                            0.f
                    )
            );
            shapeData.put(
                    new ShapeData(
                            ShapeData.BORDERED_ARROW,
                            SensorList.get(index).getX(),
                            SensorList.get(index).getY(),
                            getCalibrationIntensity(shearForce, max, min),
                            1.0f
                    ),
                    paint
            );
            index++;
        }
    }
    public void handlePressures(final ArrayList<Entry> SensorList, FootLabelData.Foot foot) {
        float[] pressures = foot.mainFloatList.get(FootLabelData.MainFloatList.Pressures);
        float max = OtherUsefulFunction.getMaxOf(pressures);
        float min = OtherUsefulFunction.getMinOf(pressures);

        int index = 0;
        for (float pressure:pressures) {
            Paint paint = getBasicPaint();
            paint.setColor(
                    getDataColor(
                            BasicResourceManager.getResources(),
                            (int) (pressure - min),
                            (int) ((max - min) * 1.f),
                            0.5f
                    )
            );
            shapeData.put(
                    new ShapeData(
                            ShapeData.BORDERED_CIRCLE,
                            SensorList.get(index).getX(),
                            SensorList.get(index).getY(),
                            30.f,
                            1.0f
                    ),
                    paint
            );
            index++;
        }
    }
    public void handleTemperatures(final ArrayList<Entry> SensorList, FootLabelData.Foot foot) {
        float[] temperatures = foot.mainFloatList.get(FootLabelData.MainFloatList.Temperatures);
        float max = OtherUsefulFunction.getMaxOf(temperatures);
        float min = OtherUsefulFunction.getMinOf(temperatures);

        int index = 0;
        for (float temperature:temperatures) {
            Paint paint = getBasicPaint();
            paint.setColor(
                    getDataColor(
                            BasicResourceManager.getResources(),
                            (int) (temperature - min),
                            (int) ((max - min) * 1.f),
                            -0.5f
                    )
            );
            shapeData.put(
                    new ShapeData(
                            ShapeData.BORDERED_CIRCLE,
                            SensorList.get(index).getX(),
                            SensorList.get(index).getY(),
                            50.f,
                            1.0f
                    ),
                    paint
            );
            index++;
        }
    }
    public static int getDataColor(Resources resources, int dataIndex, int dataSize, float contrastRatio) {
        return ColorUtils.HSLToColor(
                new float[]{
                        (255.0f * dataIndex/dataSize),
                        1.0f,
                        0.5f + contrastRatio * 0.5f
                });
    }

    // TODO parseLineData
    LinkedHashMap<LineData, Paint> lineData = new LinkedHashMap();
    public void parseLineData(ArrayList<ArrayList<FootLabelData.Foot>> sortedFeet) {
        lineData = new LinkedHashMap<>();

        for (ArrayList<FootLabelData.Foot> feet:sortedFeet) {
            handleShearForceCenter(feet);
            handlePressureCenter(feet);
            handleTemperatureCenter(feet);
        }
    }
    public void handleShearForceCenter(ArrayList<FootLabelData.Foot> feet) {
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        ArrayList<Entry> centers = new ArrayList<>();
        Paint paint = getBasicPaint();
        paint.setColor(Color.RED);
        for (FootLabelData.Foot foot:feet) {
            centers.add(foot.mainCenterEntry[FootLabelData.MainFloatList.ShearForce]);
        }
        lineData.put(new LineData(iLineDataSets), paint);
    }
    public void handlePressureCenter(ArrayList<FootLabelData.Foot> feet) {
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        ArrayList<Entry> centers = new ArrayList<>();
        Paint paint = getBasicPaint();
        paint.setColor(Color.RED);
        for (FootLabelData.Foot foot:feet) {
            centers.add(foot.mainCenterEntry[FootLabelData.MainFloatList.Pressures]);
        }
        lineData.put(new LineData(iLineDataSets), paint);
    }
    public void handleTemperatureCenter(ArrayList<FootLabelData.Foot> feet) {
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        ArrayList<Entry> centers = new ArrayList<>();
        Paint paint = getBasicPaint();
        paint.setColor(Color.RED);
        for (FootLabelData.Foot foot:feet) {
            centers.add(foot.mainCenterEntry[FootLabelData.MainFloatList.Temperatures]);
        }
        lineData.put(new LineData(iLineDataSets), paint);
    }

}
