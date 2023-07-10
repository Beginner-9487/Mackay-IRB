package com.example.mackayirb.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FootCanvas extends View {

    private List<ShapeData> shapeDataList;
    private List<Entry> selectedShapeDataList;
    private LineData lineData;

    private HashMap<Bitmap, Entry> backgroundImages;

    private Paint arrowPaint;
    private Paint circlePaint;
    private Paint pointPaint;
    private Paint crossMarkPaint;
    private Paint selectedShapeTextPaint;
    private Paint linePaint;

    private TouchManager touchManager = new TouchManager();

    private MyGraphicsMatrix imageMatrix = new MyGraphicsMatrix();

    public FootCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        arrowPaint = new Paint();
        arrowPaint.setColor(Color.RED);
        arrowPaint.setStyle(Paint.Style.STROKE);
        arrowPaint.setStrokeWidth(5f);

        circlePaint = new Paint();
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(5f);

        pointPaint = new Paint();
        pointPaint.setColor(Color.GREEN);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(5f);

        crossMarkPaint = new Paint();
        crossMarkPaint.setColor(Color.BLACK);
        crossMarkPaint.setStyle(Paint.Style.STROKE);
        crossMarkPaint.setStrokeWidth(5f);

        selectedShapeTextPaint = new Paint();
        selectedShapeTextPaint.setColor(Color.WHITE);
        selectedShapeTextPaint.setTextSize(30f);

        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2f);

        // Initialize the list of background images
        backgroundImages = new HashMap<>();

        // Initialize the selected shape data list and line data list
        selectedShapeDataList = new ArrayList<>();
        lineData = new LineData();
    }

    public void setBackgroundImages(Bitmap image, float x, float y) {
        backgroundImages.put(image, new Entry(x,y));
        invalidate();
    }

    public void setData(List<ShapeData> shapeDataList, LineData lineData) {
        this.shapeDataList = shapeDataList;
        this.lineData = lineData;
//        Log.d("shapeDataList: " + String.valueOf(shapeDataList.size()));
//        Log.d("lineData: " + String.valueOf(lineData.getDataSets().size()));
//        Log.d("lineData.getEntryCount: " + String.valueOf(lineData.getDataSets().get(0).getEntryCount()));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Apply transformations to the canvas
        canvas.save();
        canvas.concat(imageMatrix);

        // Draw the background images
        for (Map.Entry<Bitmap, Entry> backgroundImage : backgroundImages.entrySet()) {
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

        // Draw the shapes on top of the background
        if (shapeDataList != null) {
            for (ShapeData shapeData : shapeDataList) {
                drawShapeData(canvas, shapeData);
            }
        }

        // Draw cross marks and display position information for selected shape data
        for (Entry selectedShapeData : selectedShapeDataList) {
            for (ShapeData shapeData:shapeDataList) {
                if(selectedShapeData.getX() == shapeData.getX() && selectedShapeData.getY() == shapeData.getY()) {
//                    Log.d("drawPositionText");
                    // Draw cross mark
                    drawCrossMark(canvas, shapeData.getX(), shapeData.getY(), shapeData.getIntensity());
                    // Display position information
                    drawIntensityText(canvas, shapeData.getX(), shapeData.getY(), shapeData.getIntensity());
                }
            }
        }

        // Draw lines linking two ShapeData
        if (lineData != null) {
            for (ILineDataSet lineDataSet : lineData.getDataSets()) {
                ArrayList<Entry> startEndPoint = new ArrayList<>();
                for (int i=0; i<lineDataSet.getEntryCount(); i++) {
                    if(startEndPoint.size() < 2) {
                        startEndPoint.add(lineDataSet.getEntryForIndex(i));
                    } else {
//                        Log.d("drawLine: " + startEndPoint.get(0).toString() + ";" + startEndPoint.get(1).toString());
                        canvas.drawLine(
                                startEndPoint.get(0).getX(),
                                startEndPoint.get(0).getY(),
                                startEndPoint.get(1).getX(),
                                startEndPoint.get(1).getY(),
                                linePaint
                        );
                        drawPositionText(canvas, startEndPoint.get(0).getX(), startEndPoint.get(0).getY());
                        drawPositionText(canvas, startEndPoint.get(1).getX(), startEndPoint.get(1).getY());
                        startEndPoint.set(0, startEndPoint.get(1));
                        startEndPoint.set(1, lineDataSet.getEntryForIndex(i));
                    }
                }
            }
        }

        // Restore the canvas transformations
        canvas.restore();
    }

    private boolean isTwoFingerGesture = false;
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
                        imageMatrix.preTranslate(
                                ((t1.getX() - touchManager.getTouch(0).getX()) + (t2.getX() - touchManager.getTouch(1).getX())) / 2.0f,
                                ((t1.getY() - touchManager.getTouch(0).getY()) + (t2.getY() - touchManager.getTouch(1).getY())) / 2.0f
                        );
                        imageMatrix.preScale(
                                Math.abs(t1.getX() - t2.getX()) / Math.abs(touchManager.getTouch(0).getX() - touchManager.getTouch(1).getX()),
                                Math.abs(t1.getY() - t2.getY()) / Math.abs(touchManager.getTouch(0).getY() - touchManager.getTouch(1).getY()),
                                tCenter.getX(),
                                tCenter.getY()
                        );
//                        imageMatrix.preRotate(
//                                (float) Math.toDegrees(
//                                        OtherUsefulFunction.getTwoPointRadian(t1, t2) -
//                                        OtherUsefulFunction.getTwoPointRadian(touchManager.getTouch(0), touchManager.getTouch(1))
//                                ),
//                                tCenter.getX(),
//                                tCenter.getY()
//                        );
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

//        Log.d("getAction: " + String.valueOf(action));
//        Log.d("getPointerCount: " + String.valueOf(event.getPointerCount()));
        return true;
    }

    private void handleShapeDataSelection() {
        if (shapeDataList != null && shapeDataList.size() > 0) {
            Entry closestShapeData = new Entry(Float.MAX_VALUE, Float.MAX_VALUE);;
            float closestDistance = Float.MAX_VALUE;

            float tx = imageMatrix.getInvertX(touchManager.getTouch(0).getX(),touchManager.getTouch(0).getY());
            float ty = imageMatrix.getInvertY(touchManager.getTouch(0).getX(),touchManager.getTouch(0).getY());
            for (ShapeData shapeData : shapeDataList) {
                float centerX = shapeData.getX();
                float centerY = shapeData.getY();

                float distance = calculateDistance(tx, ty, centerX, centerY);
                if (distance < closestDistance) {
                    closestShapeData.setX(shapeData.getX());
                    closestShapeData.setY(shapeData.getY());
                    closestDistance = distance;
                }
            }

            if (closestShapeData != null) {
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
                    }

                    // Show a toast message with the selected shape data information
                    String shapeInfo = String.valueOf(selectedShapeDataList.size());
//                    Toast.makeText(getContext(), "Selected: " + shapeInfo, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {}
            }
        }
    }

    private float calculateDistance(float x1, float y1, float x2, float y2) {
//        Log.d("(" + x1 + "," + y1 + "); (" + x2 + "," + y2 + "); ");
//        x1 = imageMatrix.getX(x1,y1);
//        y1 = imageMatrix.getY(x1,y1);
//        x2 = imageMatrix.getX(x2,y2);
//        y2 = imageMatrix.getY(x2,y2);
//        Log.d("(" + x1 + "," + y1 + "); (" + x2 + "," + y2 + "); ");
        return (float) OtherUsefulFunction.calculateDistance(x1, y1, x2, y2);
    }

    public float getCalibrationIntensity(float intensity) {
        float maxIntensity = 0;
        float minIntensity = Float.MAX_VALUE;
        for (ShapeData shapeData:shapeDataList) {
            if(shapeData.getIntensity() > maxIntensity) {
                maxIntensity = shapeData.getIntensity();
            }
            if(shapeData.getIntensity() < minIntensity) {
                minIntensity = shapeData.getIntensity();
            }
        }
        return (intensity - minIntensity) / (maxIntensity - minIntensity) * 50.f + 50.f;
    }

    private void drawShapeData(Canvas canvas, ShapeData shapeData) {
        int shapeType = shapeData.getShapeType();
        float centerX = shapeData.getX();
        float centerY = shapeData.getY();
        float intensity = shapeData.getIntensity();
        float orientation = shapeData.getOrientation();

        switch (shapeType) {
            case ShapeData.ARROW:
                drawArrow(canvas, centerX, centerY, intensity, orientation);
                break;
            case ShapeData.CIRCLE:
                drawCircle(canvas, centerX, centerY, intensity);
                break;
            case ShapeData.POINT:
                drawPoint(canvas, centerX, centerY, intensity);
                break;
        }
    }

    private void drawArrow(Canvas canvas, float centerX, float centerY, float intensity, float orientationRadian) {
        // Customize the arrow shape drawing based on your requirements
        // This is just a simple example
        float length = getCalibrationIntensity(intensity);
        PointF point1 = new PointF(centerX, centerY);
        PointF point2 = new PointF((float) (centerX + (Math.cos(orientationRadian) * length)), (float) (centerY + (Math.sin(orientationRadian) * length)));
        float radian = (float) (orientationRadian + Math.toRadians(10));
        PointF point3 = new PointF((float) (point2.x + (Math.cos(orientationRadian) * length * 0.1f)), (float) (point2.y + (Math.sin(orientationRadian) * length * 0.1f)));
        radian = (float) (orientationRadian - Math.toRadians(10));
        PointF point4 = new PointF((float) (point2.x + (Math.cos(orientationRadian) * length * 0.1f)), (float) (point2.y + (Math.sin(orientationRadian) * length * 0.1f)));

        canvas.drawLine(point1.x, point1.y, point2.x, point2.y, arrowPaint);
        canvas.drawLine(point2.x, point2.y, point3.x, point3.y, arrowPaint);
        canvas.drawLine(point2.x, point2.y, point4.x, point4.y, arrowPaint);
    }

    private void drawCircle(Canvas canvas, float centerX, float centerY, float intensity) {
        // Customize the circle shape drawing based on your requirements
        // This is just a simple example
        float radius = getCalibrationIntensity(intensity) / 2.0f;
        RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawOval(rectF, circlePaint);
    }

    private void drawPoint(Canvas canvas, float centerX, float centerY, float intensity) {
        // Customize the point shape drawing based on your requirements
        // This is just a simple example
        pointPaint.setStrokeWidth(getCalibrationIntensity(intensity));
        canvas.drawPoint(centerX, centerY, pointPaint);
    }

    private void drawCrossMark(Canvas canvas, float centerX, float centerY, float intensity) {
        // Customize the arrow shape drawing based on your requirements
        // This is just a simple example
        float radian45 = (float) Math.toRadians(45);
        float radian180 = (float) Math.toRadians(180);
        float centerToEdge = getCalibrationIntensity(intensity) / 2.0f;
        PointF point1 = new PointF((float) (centerX + (Math.cos(radian45) * centerToEdge)), (float) (centerY + (Math.sin(radian45) * centerToEdge)));
        PointF point2 = new PointF((float) (centerX + (Math.cos(radian45 + radian180) * centerToEdge)), (float) (centerY + (Math.sin(radian45 + radian180) * centerToEdge)));
        PointF point3 = new PointF((float) (centerX + (Math.cos(-radian45) * centerToEdge)), (float) (centerY + (Math.sin(-radian45) * centerToEdge)));
        PointF point4 = new PointF((float) (centerX + (Math.cos(-radian45 + radian180) * centerToEdge)), (float) (centerY + (Math.sin(-radian45 + radian180) * centerToEdge)));

        canvas.drawLine(point1.x, point1.y, point2.x, point2.y, arrowPaint);
        canvas.drawLine(point3.x, point3.y, point4.x, point4.y, arrowPaint);
    }

    private void drawPositionText(Canvas canvas, float centerX, float centerY) {
        String positionInfo = "Position: " + Math.round(centerX * 100f) / 100f + ", " + Math.round(centerY * 100f) / 100f;
        float textWidth = selectedShapeTextPaint.measureText(positionInfo);
        float textHeight = selectedShapeTextPaint.getTextSize();
        float textX = centerX - (textWidth / 2);
        float textY = centerY - textHeight;
        canvas.drawText(positionInfo, textX, textY, selectedShapeTextPaint);
    }

    private void drawIntensityText(Canvas canvas, float centerX, float centerY, float intensity) {
//        String positionInfo = "Position: " + centerX + ", " + centerY;
        String positionInfo = String.valueOf(Math.round(intensity * 100f) / 100f);
//        Log.d(String.valueOf(centerX) + "," + String.valueOf(centerY));
        float textWidth = selectedShapeTextPaint.measureText(positionInfo);
        float textHeight = selectedShapeTextPaint.getTextSize();
        float textX = centerX - (textWidth / 2);
//        float textY = centerY - (getCalibrationIntensity(intensity) * 0.3f) - textHeight;
        float textY = centerY - textHeight;
        canvas.drawText(positionInfo, textX, textY, selectedShapeTextPaint);
    }

}