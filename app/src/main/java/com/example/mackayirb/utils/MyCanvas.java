package com.example.mackayirb.utils;

import static com.example.mackayirb.utils.OtherUsefulFunction.addDegree;
import static com.example.mackayirb.utils.OtherUsefulFunction.getMirrorEntry;
import static com.example.mackayirb.utils.OtherUsefulFunction.getP2Entry;
import static com.example.mackayirb.utils.OtherUsefulFunction.xAtoB;
import static com.example.mackayirb.utils.OtherUsefulFunction.yAtoB;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public abstract class MyCanvas extends View {

    private MyGraphicsMatrix imageMatrix = new MyGraphicsMatrix();
    public MyGraphicsMatrix getImageMatrix() {
        return imageMatrix;
    }

    private LinkedHashMap<Bitmap, Entry> backgroundImagesList = new LinkedHashMap<>();

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

    public void putBackgroundImage(Bitmap image, float x, float y) {
        backgroundImagesList.put(image, new Entry(x,y));
        invalidate();
    }

    public void setBackgroundImageHash(LinkedHashMap<Bitmap, Entry> hashImage) {
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Apply transformations to the canvas
        canvas.save();
        canvas.concat(imageMatrix);

        drawBackgroundImageList(canvas);
        drawShapeDataList(canvas);
        drawLineDataList(canvas);
        drawStrings(canvas);

        // Restore the canvas transformations
        canvas.restore();
    }

    private void drawBackgroundImageList(Canvas canvas) {
        for (Map.Entry<Bitmap, Entry> backgroundImage : backgroundImagesList.entrySet()) {
            Bitmap image = backgroundImage.getKey();
            Rect srcRect = new Rect(
                    0,
                    0,
                    image.getWidth(),
                    image.getHeight()
            );
            RectF destRect = new RectF(
                    (int) backgroundImage.getValue().getX(),
                    (int) backgroundImage.getValue().getY(),
                    backgroundImage.getValue().getX() + image.getWidth(),
                    backgroundImage.getValue().getY() + image.getHeight()
            );
            canvas.drawBitmap(image, srcRect, destRect, null);
        }
    }

    private void drawShapeDataList(Canvas canvas) {
        if (shapeDataList == null) {
            return;
        }
        for (Map.Entry<ShapeData, Paint> s : shapeDataList.entrySet()) {
            int shapeType = s.getKey().getShapeType();
            float centerX = s.getKey().getX();
            float centerY = s.getKey().getY();
            float intensity = s.getKey().getIntensity();
            float direction = s.getKey().getDirection();
            Paint paint = s.getValue();

            drawShapeData(canvas, paint, shapeType, centerX, centerY, intensity, direction);
        }
    }
    public void drawShapeData(Canvas canvas, Paint paint, int shapeType, float cx, float cy, float intensity, float direction) {
        switch (shapeType) {
            case ShapeData.ARROW:
                drawArrow(canvas, paint, cx, cy, intensity, direction);
                break;
            case ShapeData.BORDERED_ARROW:
                drawBorderedArrow(canvas, paint, cx, cy, intensity, direction);
                break;
            case ShapeData.CIRCLE:
                drawCircle(canvas, paint, cx, cy, intensity);
                break;
            case ShapeData.BORDERED_CIRCLE:
                drawBorderedCircle(canvas, paint, cx, cy, intensity);
                break;
            case ShapeData.CROSS:
                drawCross(canvas, paint, cx, cy, intensity);
                break;
        }
    }

    private void drawLineDataList(Canvas canvas) {
        if (shapeDataList == null) {
            return;
        }
        for (Map.Entry<LineData, Paint> lineData : lineDataList.entrySet()) {
            for (ILineDataSet lineDataSet : lineData.getKey().getDataSets()) {
                ArrayList<Entry> startEndPoint = new ArrayList<>();
                for (int i=0; i<lineDataSet.getEntryCount(); i++) {
                    if(startEndPoint.size() < 2) {
                        startEndPoint.add(lineDataSet.getEntryForIndex(i));
                    } else {
                        canvas.drawLine(
                                startEndPoint.get(0).getX(),
                                startEndPoint.get(0).getY(),
                                startEndPoint.get(1).getX(),
                                startEndPoint.get(1).getY(),
                                lineData.getValue()
                        );
                        startEndPoint.set(0, startEndPoint.get(1));
                        startEndPoint.set(1, lineDataSet.getEntryForIndex(i));
                    }
                }
            }
        }
    }

    private void drawStrings(Canvas canvas) {
        if (stringToPaint == null || stringToEntry == null) {
            return;
        }
        for (Map.Entry<String, Paint> s : stringToPaint.entrySet()) {
            drawString(canvas, s.getValue(), s.getKey(), stringToEntry.get(s.getKey()).getX(), stringToEntry.get(s.getKey()).getY());
        }
    }

    public float getBorderThickness() {
        return 0.1f;
    }
    public int getBorderColor() {
        return OtherUsefulFunction.getBWColor(BasicResourceManager.getResources());
    }
    public Paint getBorderPaint(Paint innerPaint) {
        Paint outerPaint = new Paint();
        outerPaint.setColor(getBorderColor());
        outerPaint.setStyle(innerPaint.getStyle());
        outerPaint.setAntiAlias(true);
        return outerPaint;
    }

    /**
     * direction: Radian
     */
    public void drawArrow(Canvas canvas, Paint paint, float cx, float cy, float length, float direction) {
        PointF point1 = new PointF(cx, cy);
        PointF point2 = new PointF(xAtoB(cx, length, direction), yAtoB(cy, length, direction));
        float radian = addDegree(direction, 10);
        PointF point3 = new PointF(xAtoB(point2.x, length * 0.1f, radian), yAtoB(point2.y, length * 0.1f, radian));
        radian = addDegree(direction, -10);
        PointF point4 = new PointF(xAtoB(point2.x, length * 0.1f, radian), yAtoB(point2.y, length * 0.1f, radian));

        canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint);
        canvas.drawLine(point2.x, point2.y, point3.x, point3.y, paint);
        canvas.drawLine(point2.x, point2.y, point4.x, point4.y, paint);
    }

    public float getArrowBodyWidth() {
        return 0.2f / 2f;
    }
    public float getArrowBodyLength() {
        return 0.7f;
    }
    public float getArrowHeadWidth() {
        return 0.4f / 2f;
    }
    public float getArrowHeadLength() {
        return 1f - getArrowBodyLength();
    }
    /**
     * direction: Radian
     */
    public void drawBorderedArrow(Canvas canvas, Paint innerPaint, float cx, float cy, float length, float direction) {
        Paint outerPaint = getBorderPaint(innerPaint);

        int numberOfPoint = 8 + 1;
        Path innerPath = new Path();
        Path outerPath = new Path();
        Entry[] innerPoints = new Entry[numberOfPoint];
        Entry[] outerPoints = new Entry[numberOfPoint];

        int index = 0;
        innerPoints[index] = new Entry(cx, cy);
        outerPoints[index] = new Entry(
                xAtoB(cx, length * getBorderThickness(), addDegree(direction, 180)),
                yAtoB(cy, length * getBorderThickness(), addDegree(direction, 180))
        );
        index++;
        innerPoints[index] = getP2Entry(innerPoints[index-1], length * getArrowBodyWidth(), addDegree(direction, 90));
        outerPoints[index] = getP2Entry(outerPoints[index-1], length * (getArrowBodyWidth() + getBorderThickness()), addDegree(direction, 90));
        index++;
        innerPoints[index] = getP2Entry(innerPoints[index-1], length * getArrowBodyLength(), direction);
        outerPoints[index] = getP2Entry(outerPoints[index-1], length * getArrowBodyLength(), direction);
        index++;
        innerPoints[index] = getP2Entry(innerPoints[index-1], length * (getArrowHeadWidth() - getArrowBodyWidth()), addDegree(direction, 90));
        outerPoints[index] = getP2Entry(outerPoints[index-1], length * (getArrowHeadWidth() - getArrowBodyWidth() + getBorderThickness() * (-1 + getArrowHeadLength() / getArrowHeadWidth()) ), addDegree(direction, 90));
        index++;
        innerPoints[index] = new Entry(xAtoB(cx, length, direction), yAtoB(cy, length, direction));
        outerPoints[index] = new Entry(
                xAtoB(cx, length * (1 + (float) (getBorderThickness() / Math.cos(Math.toRadians(45)))), direction),
                yAtoB(cy, length * (1 + (float) (getBorderThickness() / Math.sin(Math.toRadians(45)))), direction)
        );

        for(int i = index + 1; i<numberOfPoint; i++) {
            innerPoints[i] = getMirrorEntry(innerPoints[0], innerPoints[index], innerPoints[2 * index - i]);
            outerPoints[i] = getMirrorEntry(outerPoints[0], outerPoints[index], outerPoints[2 * index - i]);
        }

        outerPath.moveTo(outerPoints[0].getX(), outerPoints[0].getY());
        innerPath.moveTo(innerPoints[0].getX(), innerPoints[0].getY());
        for (int i = 1; i < numberOfPoint; i ++) {
            outerPath.lineTo(outerPoints[i].getX(), outerPoints[i].getY());
            innerPath.lineTo(innerPoints[i].getX(), innerPoints[i].getY());
        }
        outerPath.close();
        innerPath.close();

        canvas.drawPath(outerPath, outerPaint);
        canvas.drawPath(innerPath, innerPaint);
    }
    public void drawCircle(Canvas canvas, Paint paint, float cx, float cy, float radius) {
        canvas.drawCircle(cx, cy, radius, paint);
    }
    public void drawBorderedCircle(Canvas canvas, Paint paint, float cx, float cy, float radius) {
        canvas.drawCircle(cx, cy, radius * (1 + getBorderThickness()), getBorderPaint(paint));
        canvas.drawCircle(cx, cy, radius, paint);
    }
    public void drawCross(Canvas canvas, Paint paint, float centerX, float centerY, float centerToEdge) {
        float radian45 = (float) Math.toRadians(45);
        float radian180 = (float) Math.toRadians(180);
        PointF point1 = new PointF((float) (centerX + (Math.cos(radian45) * centerToEdge)), (float) (centerY + (Math.sin(radian45) * centerToEdge)));
        PointF point2 = new PointF((float) (centerX + (Math.cos(radian45 + radian180) * centerToEdge)), (float) (centerY + (Math.sin(radian45 + radian180) * centerToEdge)));
        PointF point3 = new PointF((float) (centerX + (Math.cos(-radian45) * centerToEdge)), (float) (centerY + (Math.sin(-radian45) * centerToEdge)));
        PointF point4 = new PointF((float) (centerX + (Math.cos(-radian45 + radian180) * centerToEdge)), (float) (centerY + (Math.sin(-radian45 + radian180) * centerToEdge)));

        canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint);
        canvas.drawLine(point3.x, point3.y, point4.x, point4.y, paint);
    }

    private void drawString(Canvas canvas, Paint paint, String text, float centerX, float centerY) {
        float textWidth = paint.measureText(text);
        float textHeight = paint.getTextSize();
        float textX = centerX - (textWidth / 2);
        float textY = centerY - textHeight;
        canvas.drawText(text, textX, textY, paint);
    }

}