package com.example.mackayirb.ui.central;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.ColorUtils;

import com.example.mackayirb.R;
import com.example.mackayirb.data.central.FootManagerData;
import com.example.mackayirb.data.central.FootLabelData;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.example.mackayirb.utils.ShapeData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CentralFootMapFragment extends CentralFragment implements CentralMvpView {

    private FootMapCanvas canvasView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.init(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.central_foot_map;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = super.onCreateView(inflater, container, savedInstanceState);

        canvasView = view.findViewById(R.id.FootCanvas);
        Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.foot_left);
        Bitmap image2 = BitmapFactory.decodeResource(getResources(), R.drawable.foot_right);
        Bitmap image3 = ((BasicResourceManager.getResources().getConfiguration().uiMode & 48) == Configuration.UI_MODE_NIGHT_YES)
                ? BitmapFactory.decodeResource(getResources(), R.drawable.xyz_white)
                : BitmapFactory.decodeResource(getResources(), R.drawable.xyz_black);
        canvasView.putBackgroundImage(
                image1,
                image1.getWidth() / 2f,
                image1.getHeight() / 2f,
                1f,
                1f,
                0
        );
        canvasView.putBackgroundImage(
                image2,
                image1.getWidth() + (image2.getWidth() / 2f),
                image2.getHeight() / 2f,
                1f,
                1f,
                0
        )
        ;
        canvasView.putBackgroundImage(
                image3,
                image1.getWidth() + image2.getWidth() + (image3.getWidth() / 2f),
                image3.getHeight() / 2f,
                1f,
                1f,
                0
        );

        return view;
    }

    @Override
    public void doSomethingFrequently() {
        try {
            if(!this.equals(BasicResourceManager.getCurrentFragment())) {
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
//                            Log.d("map");
                            FootManagerData footDataManager = ((FootManagerData) (mCentralPresenter.getCentralDataManager()));
                            ArrayList<FootLabelData.Foot> eachDeviceFoot = new ArrayList<>();
//                            ArrayList<ArrayList<FootLabelData.Foot>> eachDeviceFeet = new ArrayList<>();
                            for (int i=0; i<footDataManager.deviceData.size(); i++) {
                                if(footDataManager.deviceData.get(i).labelData.size() == 0) {
                                    break;
                                }
                                eachDeviceFoot.addAll(footDataManager.deviceData.get(i).labelData.get(0).getNewestFeet());
//                            eachDeviceFeet.add(footDataManager.deviceData.get(i).labelData.get(0).getFeet());
                            }
                            parseShapeData(eachDeviceFoot);
                            canvasView.setShapeDataHash(shapeData);
//                            parseLineData(eachDeviceFeet);
//                            canvasView.setLineDataHash(lineData);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {}
    }

    // TODO Basic
    public static class Types{
        public static final byte ShearForce = 0;
        public static final byte Pressures = 1;
        public static final byte Temperatures = 2;
        public static final byte[] Types = new byte[]{Temperatures, Pressures, ShearForce};
        public static class Ranges {
            public static float[][][] ValueRanges = new float[][][]{
                    new float[][]{
                            new float[]{
                                    (float) Math.sqrt(Math.pow(1.5, 2) * 2), (float) Math.sqrt(Math.pow(10, 2) * 2)
                            }
                    },
                    new float[][]{
                            new float[]{
                                    2f, -25f
                            }
                    },
                    new float[][]{
                            new float[]{
                                    25f, 40f
                            }
                    }
            };
            public static float[][][] MaxRanges = new float[][][]{
                    new float[][]{
                            new float[]{
                                    // ValueRanges[ShearForce][0][1], (float) Math.sqrt(Math.pow(0xFFFF, 2) * 2),
                            }
                    },
                    new float[][]{
                            new float[]{
                                    ValueRanges[Pressures][0][1], -0xFFFF
                            }
                    },
                    new float[][]{
                            new float[]{
                                    ValueRanges[Temperatures][0][1], 0xFF
                            }
                    }
            };
            public static float[][][] MinRanges = new float[][][]{
                    new float[][]{
                            new float[]{
                                    (float) Math.sqrt(Math.pow(0x0000, 2) * 2), ValueRanges[ShearForce][0][0],
                                    ValueRanges[ShearForce][0][1], (float) Math.sqrt(Math.pow(0xFFFF, 2) * 2),
                            }
                    },
                    new float[][]{
                            new float[]{
                                    0xFFFF, ValueRanges[Pressures][0][0]
                            }
                    },
                    new float[][]{
                            new float[]{
                                    0x00, ValueRanges[Temperatures][0][0]
                            }
                    }
            };
            public static byte[] ColorStep = new byte[]{0, 6, 8};
            public static boolean[] ColorInverse = new boolean[]{true, true, true};
            public static int findTargetRangeIndex(float[] array, float value) {
                for(int i=0; i<array.length; i+=2) {
                    if(
                            (array[i] <= value && value <= array[i+1]) ||
                            (array[i+1] <= value && value <= array[i])
                    ) {
                        return i;
                    }
                }
                return -1;
            }
            public static float[] findTargetRangeArray(float[][][] ranges, byte type, float value) {
                float[] targetArray = new float[]{};
                for (float[] array: ranges[type]) {
                    if(findTargetRangeIndex(array, value) != -1) {
                        targetArray = array;
                        break;
                    }
                }
                return targetArray;
            }
            public static final float VALUE_MAX = Float.MAX_VALUE;
            public static final float VALUE_MIN = 0;
            public static final float VALUE_NULL = -1f;
            public static float getDiffOfValueToMin(byte type, float value) {
                float[] targetArray = findTargetRangeArray(ValueRanges,type, value);
                if(targetArray.length == 0) {
                    if(findTargetRangeArray(MaxRanges, type, value).length != 0) {
                        return VALUE_MAX;
                    }
                    if(findTargetRangeArray(MinRanges, type, value).length != 0) {
                        return VALUE_MIN;
                    }
                    return VALUE_NULL;
                }
                int index = findTargetRangeIndex(targetArray, value);
                float sum = 0f;
                for(int i=0; i<index; i+=2) {
                    sum += Math.abs(targetArray[i] - targetArray[i+1]);
                }
                return sum + Math.abs(value - targetArray[index]);
            }
            public static final float MAX_MAX = Float.MAX_VALUE;
            public static float getDiffOfMaxToMin(byte type, float value) {
                float[] targetArray = findTargetRangeArray(ValueRanges, type, value);
                if(targetArray.length == 0) {
                    return MAX_MAX;
                }
                float sum = 0f;
                for(int i=0; i<targetArray.length; i+=2) {
                    sum += Math.abs(targetArray[i] - targetArray[i+1]);
                }
                return sum;
            }
            public static float getRatioOfValueRange(byte type, float value) {
                float v = getDiffOfValueToMin(type, value);
                if(v == VALUE_NULL) {
                    return VALUE_NULL;
                }
                return v / getDiffOfMaxToMin(type, value);
            }
        }
    }
    public byte getShape(byte type) {
        switch (type) {
            case Types.ShearForce:
                return ShapeData.BORDERED_ARROW;
            case Types.Pressures:
            case Types.Temperatures:
                return ShapeData.BORDERED_CIRCLE;
        }
        return ShapeData.CROSS;
    }
    public float getColorContrastRatio(byte type, float value) {
        switch (type) {
            case Types.ShearForce:
                return 0f;
            case Types.Pressures:
                return 0.5f;
            case Types.Temperatures:
                return -0.5f;
        }
        return 0f;
    }
    public float getCalibrationIntensity(byte type, float value) {
        switch (type) {
            case Types.ShearForce:
//                return 70f;
                return 70f * Types.Ranges.getRatioOfValueRange(type, value);
            case Types.Pressures:
                return 30f;
            case Types.Temperatures:
                return 50f;
        }
        return 0f;
    }

    public Paint getBasicPaint() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        return paint;
    }

    public static int getDataColor(float ratio, byte step, float contrastRatio, boolean inverse) {
        if(ratio == Types.Ranges.VALUE_NULL) {
            return OtherUsefulFunction.getBWColor(BasicResourceManager.getResources());
        }
        if(step != 0) {
            int p = (int) Math.floor(step * ratio);
            float h = (255.0f / (step-1)) * p;
            h = (h > 255f) ? 255f : h;
            h = (h < 0f) ? 0f : h;
            h = (inverse) ? (255f - h) : h;
            // Log.d(String.valueOf(ratio) + ": " + String.valueOf(h) + ": " + String.valueOf(step) + ": " + String.valueOf(p));
            return ColorUtils.HSLToColor(
                    new float[]{
                            h,
                            1.0f,
                            0.5f + contrastRatio * 0.5f
                    });
        }
        return ColorUtils.HSLToColor(
                new float[]{
                        0,
                        0.0f,
                        0.5f + contrastRatio * 0.5f
                });
    }

    // TODO parseShapeData
    LinkedHashMap<ShapeData, Paint> shapeData = new LinkedHashMap<>();
    public void parseShapeData(ArrayList<FootLabelData.Foot> feet) {
        shapeData = new LinkedHashMap<>();
//        Log.d(String.valueOf(feet.size()));
        for (FootLabelData.Foot foot:feet) {
            final ArrayList<Entry> SensorList = foot.getSensorList();

            for (int index=0; index<foot.getNumberOfSensor(); index++) {
                // Log.d(String.valueOf(index));
                for (byte type: Types.Types) {
                    float value = 0f;
                    float direction = 0f;
                    switch (type) {
                        case Types.Temperatures:
                            value = foot.mapFloatList.get(FootLabelData.MapFloatList.Temperatures)[index];
                            break;
                        case Types.Pressures:
                            value = foot.mapFloatList.get(FootLabelData.MapFloatList.Pressures)[index];
                            break;
                        case Types.ShearForce:
                            Entry entry = foot.getVectorMagnitudeDirection(index, FootLabelData.MapFloatList.ShearForceX, FootLabelData.MapFloatList.ShearForceY, new Entry(0, 0));
                            value = entry.getX();
                            direction = (float) (entry.getY() + Math.PI);
                            break;
                    }
                    // Log.d(String.valueOf(type) + ":" + String.valueOf(value) + ":" + String.valueOf(getCalibrationIntensity(type, value)) + ":" + String.valueOf(direction));
                    Paint paint = getBasicPaint();
                    // Log.d(String.valueOf(type) + ": " + String.valueOf(value));
                    paint.setColor(
                            getDataColor(
                                    Types.Ranges.getRatioOfValueRange(type, value),
                                    Types.Ranges.ColorStep[type],
                                    getColorContrastRatio(type, value),
                                    Types.Ranges.ColorInverse[type]
                            )
                    );
                    shapeData.put(
                            new ShapeData(
                                    getShape(type),
                                    SensorList.get(index).getX(),
                                    SensorList.get(index).getY(),
                                    getCalibrationIntensity(type, value),
                                    direction
                            ),
                            paint
                    );
                }
            }
        }
    }

}
