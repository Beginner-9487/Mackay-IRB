package com.example.mackayirb.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class MyCanvas extends View {

    private MyGraphicsMatrix imageMatrix = new MyGraphicsMatrix();
    public MyGraphicsMatrix getImageMatrix() {
        return imageMatrix;
    }

    private LinkedHashMap<Bitmap, float[]> backgroundImagesList = new LinkedHashMap<>();

    private LinkedHashMap<ShapeData, Paint> shapeDataList = new LinkedHashMap<>();
    public Set<ShapeData> getShapeDataSet() {
        return shapeDataList.keySet();
    }
    private LinkedHashMap<LineData, Paint> lineDataList = new LinkedHashMap<>();

    public MyCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    private LinkedHashMap<String, Paint> stringToPaint = new LinkedHashMap<>();
    private LinkedHashMap<String, Entry> stringToEntry = new LinkedHashMap<>();

    private MyCanvasDrawer myCanvasDrawer;

    public void setMyCanvasDrawer(MyCanvasDrawer myCanvasDrawer) {
        this.myCanvasDrawer = myCanvasDrawer;
    }
    public MyCanvasDrawer getBorderManager() {
        return this.myCanvasDrawer;
    }

    public void putBackgroundImage(Bitmap image, float x, float y, float sx, float sy, float rotateDegree) {
        backgroundImagesList.put(image, new float[]{x, y, sx, sy, rotateDegree});
        invalidate();
    }

    public void setBackgroundImageHash(LinkedHashMap<Bitmap, float[]> hashImage) {
        backgroundImagesList = hashImage;
        invalidate();
    }

    public void setShapeDataHash(LinkedHashMap<ShapeData, Paint> shapeDataList) {
        this.shapeDataList = shapeDataList;
        invalidate();
    }

    public void setLineDataHash(LinkedHashMap<LineData, Paint> lineDataList) {
        this.lineDataList = lineDataList;
        invalidate();
    }

    public void setStringToPaint(LinkedHashMap<String, Paint> hashStoP, LinkedHashMap<String, Entry> hashStoE) {
        stringToPaint = hashStoP;
        stringToEntry = hashStoE;
        invalidate();
    }

    public void setGraphLTRB(Entry[] graphLTRB, float left, float top, float right, float bottom) {
        if (left < graphLTRB[0].getX()) {
            graphLTRB[0].setX(left);
        }
        if (top < graphLTRB[0].getY()) {
            graphLTRB[0].setY(top);
        }
        if (right > graphLTRB[1].getX()) {
            graphLTRB[1].setX(right);
        }
        if (bottom > graphLTRB[1].getY()) {
            graphLTRB[1].setY(bottom);
        }
        // Log.d("graphLTRB[0]: " + String.valueOf(graphLTRB[0].getX()) + ", " + String.valueOf(graphLTRB[0].getY()));
        // Log.d("graphLTRB[1]: " + String.valueOf(graphLTRB[1].getX()) + ", " + String.valueOf(graphLTRB[1].getY()));
    }
    public Entry[] getLTRBFromAllGraph() {
        Entry[] graphLTRB = new Entry[]{
                new Entry(0f,0f),
                new Entry(0f,0f)
        };
//        Log.d("start: " + graphLTRB[0] + "," + graphLTRB[1]);
        for (Map.Entry<Bitmap, float[]> bitmapEntryEntry:backgroundImagesList.entrySet()) {
            float x = bitmapEntryEntry.getValue()[0];
            float y = bitmapEntryEntry.getValue()[1];
            float sx = bitmapEntryEntry.getValue()[2];
            float sy = bitmapEntryEntry.getValue()[3];
            float rotateDegree = bitmapEntryEntry.getValue()[4];
            RectF rectF = OtherUsefulFunction.getRescaleBitmap(bitmapEntryEntry.getKey(), x, y, sx, sy, rotateDegree).getValue();
            setGraphLTRB(
                    graphLTRB,
                    rectF.left,
                    rectF.top,
                    rectF.right,
                    rectF.bottom
            );
        }
//        Log.d("bitmap: " + graphLTRB[0] + "," + graphLTRB[1]);
        for (Map.Entry<ShapeData, Paint> shapeDataPaintEntry:shapeDataList.entrySet()) {
            setGraphLTRB(
                    graphLTRB,
                    shapeDataPaintEntry.getKey().getX() - shapeDataPaintEntry.getKey().getIntensity(),
                    shapeDataPaintEntry.getKey().getY() - shapeDataPaintEntry.getKey().getIntensity(),
                    shapeDataPaintEntry.getKey().getX() + shapeDataPaintEntry.getKey().getIntensity(),
                    shapeDataPaintEntry.getKey().getY() + shapeDataPaintEntry.getKey().getIntensity()
            );
        }
//        Log.d("shape: " + graphLTRB[0] + "," + graphLTRB[1]);
//        lineDataList
        for (Map.Entry<String, Entry> stringEntryEntry:stringToEntry.entrySet()) {
            setGraphLTRB(
                    graphLTRB,
                    stringEntryEntry.getValue().getX() - stringEntryEntry.getKey().length(),
                    stringEntryEntry.getValue().getY() - stringEntryEntry.getKey().length(),
                    stringEntryEntry.getValue().getX() + stringEntryEntry.getKey().length(),
                    stringEntryEntry.getValue().getY() + stringEntryEntry.getKey().length()
            );
        }
//        Log.d("string: " + graphLTRB[0] + "," + graphLTRB[1]);
        return graphLTRB;
    }
    public void makeLinearTransformationForMax() {
        Entry[] graphLTRB = getLTRBFromAllGraph();
        makeLinearTransformation(
                false,
                -graphLTRB[0].getX(),
                -graphLTRB[0].getY(),
                this.getWidth() / (graphLTRB[1].getX() - graphLTRB[0].getX()),
                this.getHeight() / (graphLTRB[1].getY() - graphLTRB[0].getY()),
                0,
                0
        );
    }
    public void makeLinearTransformationForFit() {
        Entry[] graphLTRB = getLTRBFromAllGraph();

        float graph_width = (graphLTRB[1].getX() - graphLTRB[0].getX());
        float graph_height = (graphLTRB[1].getY() - graphLTRB[0].getY());

        float aspect_ratio_graph = graph_width / graph_height;
        float aspect_ratio_this = this.getWidth() / this.getHeight();
        float x_scale = (aspect_ratio_this > aspect_ratio_graph) ? (this.getHeight() * aspect_ratio_graph) / graph_width : this.getWidth() / graph_width;
        float y_scale = (aspect_ratio_this < aspect_ratio_graph) ? (this.getWidth() / aspect_ratio_graph) / graph_height : this.getHeight() / graph_height;

        makeLinearTransformation(
                false,
                -graphLTRB[0].getX(),
                -graphLTRB[0].getY(),
                x_scale,
                y_scale,
                0,
                0
        );
    }
    public void makeLinearTransformationByTouchEvent(MotionEvent event, TouchManager touchManager) {
        try {
            Entry t1 = new Entry(event.getX(0), event.getY(0));
            Entry t2 = new Entry(event.getX(1), event.getY(1));
            Entry tCenter = new Entry((t1.getX() + t2.getX()) / 2.0f, (t1.getY() + t2.getY()) / 2.0f);
            makeLinearTransformation(
                    true,
                    ((t1.getX() - touchManager.getTouch(0).getX()) + (t2.getX() - touchManager.getTouch(1).getX())) / 2.0f,
                    ((t1.getY() - touchManager.getTouch(0).getY()) + (t2.getY() - touchManager.getTouch(1).getY())) / 2.0f,
                    Math.abs(t1.getX() - t2.getX()) / Math.abs(touchManager.getTouch(0).getX() - touchManager.getTouch(1).getX()),
                    Math.abs(t1.getY() - t2.getY()) / Math.abs(touchManager.getTouch(0).getY() - touchManager.getTouch(1).getY()),
                    tCenter.getX(),
                    tCenter.getY()
            );
        } catch (Exception e) {}
    }
    public void makeLinearTransformation(boolean pre, float dx, float dy, float sx, float sy, float scx, float scy) {
        if(!pre) {
            getImageMatrix().setTranslate(0,0);
        }
        getImageMatrix().preScale(sx, sy, scx, scy);
        getImageMatrix().preTranslate(dx,dy);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Apply transformations to the canvas
        canvas.save();
        canvas.concat(imageMatrix);

        myCanvasDrawer.drawBackgroundImageList(canvas, backgroundImagesList);
        myCanvasDrawer.drawShapeDataList(canvas, shapeDataList);
        myCanvasDrawer.drawLineDataList(canvas, lineDataList);
        myCanvasDrawer.drawStrings(canvas, stringToPaint, stringToEntry);

        // Restore the canvas transformations
        canvas.restore();
    }

}