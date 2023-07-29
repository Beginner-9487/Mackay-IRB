package com.example.mackayirb.ui.central;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.MyCanvas;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.example.mackayirb.utils.ShapeData;
import com.example.mackayirb.utils.TouchManager;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FootCanvas extends MyCanvas {

    private List<Entry> selectedShapeDataList = new ArrayList<>();
    private TouchManager touchManager = new TouchManager();

    private boolean isTwoFingerGesture = false;

    private Paint paintPositionInfo = new Paint();
    public FootCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paintPositionInfo.setColor(OtherUsefulFunction.getBWColor(BasicResourceManager.getResources()));
        paintPositionInfo.setStyle(Paint.Style.STROKE);
        paintPositionInfo.setStrokeWidth(5f);
        paintPositionInfo.setAntiAlias(true);
    }

    @Override
    public int getBorderColor() {
        return Color.BLACK;
    }

    public Paint getCrossPaint() {
        Paint paint = new Paint();
        paint.setColor(OtherUsefulFunction.getBWColor(BasicResourceManager.getResources()));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        paint.setAntiAlias(true);
        return paint;
    }
    @Override
    public void setShapeDataHash(LinkedHashMap<ShapeData, Paint> shapeDataList) {
        for (Entry selected:selectedShapeDataList) {
            shapeDataList.put(
                    new ShapeData(
                            ShapeData.CROSS,
                            selected.getX(),
                            selected.getY(),
                            15.f,
                            1.0f
                    ),
                    getCrossPaint()
            );
        }
        super.setShapeDataHash(shapeDataList);
    }

    // Optional: Handle touch events if required
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchManager.setTouchByEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if(isTwoFingerGesture) {
                    try {
                        Entry t1 = new Entry(event.getX(0), event.getY(0));
                        Entry t2 = new Entry(event.getX(1), event.getY(1));
                        Entry tCenter = new Entry((t1.getX() + t2.getX()) / 2.0f, (t1.getY() + t2.getY()) / 2.0f);
                        getImageMatrix().preTranslate(
                                ((t1.getX() - touchManager.getTouch(0).getX()) + (t2.getX() - touchManager.getTouch(1).getX())) / 2.0f,
                                ((t1.getY() - touchManager.getTouch(0).getY()) + (t2.getY() - touchManager.getTouch(1).getY())) / 2.0f
                        );
                        getImageMatrix().preScale(
                                Math.abs(t1.getX() - t2.getX()) / Math.abs(touchManager.getTouch(0).getX() - touchManager.getTouch(1).getX()),
                                Math.abs(t1.getY() - t2.getY()) / Math.abs(touchManager.getTouch(0).getY() - touchManager.getTouch(1).getY()),
                                tCenter.getX(),
                                tCenter.getY()
                        );
                    } catch (Exception e) {}
                }

                touchManager.setTouchByEvent(event);

                if(event.getPointerCount() > 1) {
                    isTwoFingerGesture = true;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touchManager.setTouchByEvent(event);
                if(!isTwoFingerGesture) {
                    handleShapeDataSelection();
                    invalidate();
                }
                isTwoFingerGesture = false;
                break;
        }

        return true;
    }

    private void handleShapeDataSelection() {
        if (getShapeDataSet() == null || getShapeDataSet().size() == 0) {
            return;
        }
        Entry closestShapeData = new Entry(Float.MAX_VALUE, Float.MAX_VALUE);;
        float closestDistance = Float.MAX_VALUE;

        float tx = getImageMatrix().getInvertX(touchManager.getTouch(0).getX(),touchManager.getTouch(0).getY());
        float ty = getImageMatrix().getInvertY(touchManager.getTouch(0).getX(),touchManager.getTouch(0).getY());
        for (ShapeData shapeData : getShapeDataSet()) {
            float centerX = shapeData.getX();
            float centerY = shapeData.getY();

            float distance = (float) OtherUsefulFunction.calculateDistance(tx, ty, centerX, centerY);
            if (distance < closestDistance) {
                closestShapeData.setX(shapeData.getX());
                closestShapeData.setY(shapeData.getY());
                closestDistance = distance;
            }
        }

        if (closestShapeData == null) {
            return;
        }
        try {
            boolean lock = true;
            for (Entry selected:selectedShapeDataList) {
                if(selected.getX() == closestShapeData.getX() && selected.getY() == closestShapeData.getY()) {
                    selectedShapeDataList.remove(selected);
                    lock = false;
                }
            }
            if(lock) {
                selectedShapeDataList.add(closestShapeData);
                setSelectedInformation();
            }
        } catch (Exception e) {}
    }
    private void setSelectedInformation() {
        LinkedHashMap<String, Paint> hashStoP = new LinkedHashMap<>();
        LinkedHashMap<String, Entry> hashStoE = new LinkedHashMap<>();
        for (Entry entry:selectedShapeDataList) {
            String positionInfo = Math.round(entry.getX() * 100f) / 100f + ", " + Math.round(entry.getY() * 100f) / 100f;
            hashStoP.put(positionInfo, paintPositionInfo);
            hashStoE.put(positionInfo, entry);
        }
        setStringToPaint(hashStoP, hashStoE);
    }

}