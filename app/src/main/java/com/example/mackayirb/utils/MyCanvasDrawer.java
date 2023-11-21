package com.example.mackayirb.utils;

import static com.example.mackayirb.utils.BasicResourceManager.getResources;
import static com.example.mackayirb.utils.OtherUsefulFunction.addDegree;
import static com.example.mackayirb.utils.OtherUsefulFunction.getMirrorEntry;
import static com.example.mackayirb.utils.OtherUsefulFunction.getP2Entry;
import static com.example.mackayirb.utils.OtherUsefulFunction.xAtoB;
import static com.example.mackayirb.utils.OtherUsefulFunction.yAtoB;

import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.PathParser;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.locationtech.jts.algorithm.Centroid;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.geom.*;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MyCanvasDrawer {

    public abstract static class BorderManager {
        public abstract float getBorderThickness();
        public abstract int getBorderColor();
        public abstract Paint getBorderPaint(Paint innerPaint);
    }

    public BorderManager borderManager;
    public MyCanvasDrawer(BorderManager borderManager) {
        this.borderManager = borderManager;
    }


    public void drawBackgroundImage(Canvas canvas, Bitmap bitmap, float x, float y, float sx, float sy, float rotateDegree) {
        Map.Entry<Bitmap, RectF> entry = OtherUsefulFunction.getRescaleBitmap(bitmap, x, y, sx, sy, rotateDegree);
        Rect srcRect = new Rect(
                0,
                0,
                entry.getKey().getWidth(),
                entry.getKey().getHeight()
        );
        canvas.drawBitmap(entry.getKey(), srcRect, entry.getValue(), null);
    }

    public void drawBackgroundImageList(Canvas canvas, HashMap<Bitmap, float[]> backgroundImagesList) {
        for (Map.Entry<Bitmap, float[]> backgroundImage : backgroundImagesList.entrySet()) {
            float x = backgroundImage.getValue()[0];
            float y = backgroundImage.getValue()[1];
            float sx = backgroundImage.getValue()[2];
            float sy = backgroundImage.getValue()[3];
            float rotateDegree = backgroundImage.getValue()[4];
            drawBackgroundImage(
                    canvas,
                    backgroundImage.getKey(),
                    x, y, sx, sy, rotateDegree
            );
        }
    }

    public void drawShapeDataList(Canvas canvas, LinkedHashMap<ShapeData, Paint> shapeDataList) {
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

    public void drawLineDataList(Canvas canvas, LinkedHashMap<LineData, Paint> lineDataList) {
        if (lineDataList == null) {
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

    public void drawStrings(Canvas canvas, LinkedHashMap<String, Paint> stringToPaint, LinkedHashMap<String, Entry> stringToEntry) {
        if (stringToPaint == null || stringToEntry == null) {
            return;
        }
        for (Map.Entry<String, Paint> s : stringToPaint.entrySet()) {
            drawString(canvas, s.getValue(), s.getKey(), stringToEntry.get(s.getKey()).getX(), stringToEntry.get(s.getKey()).getY());
        }
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
        Paint outerPaint = borderManager.getBorderPaint(innerPaint);

        int numberOfPoint = 8 + 1;
        Path innerPath = new Path();
        Path outerPath = new Path();
        Entry[] innerPoints = new Entry[numberOfPoint];
        Entry[] outerPoints = new Entry[numberOfPoint];

        int index = 0;
        innerPoints[index] = new Entry(cx, cy);
        outerPoints[index] = new Entry(
                xAtoB(cx, length * borderManager.getBorderThickness(), addDegree(direction, 180)),
                yAtoB(cy, length * borderManager.getBorderThickness(), addDegree(direction, 180))
        );
        index++;
        innerPoints[index] = getP2Entry(innerPoints[index-1], length * getArrowBodyWidth(), addDegree(direction, 90));
        outerPoints[index] = getP2Entry(outerPoints[index-1], length * (getArrowBodyWidth() + borderManager.getBorderThickness()), addDegree(direction, 90));
        index++;
        innerPoints[index] = getP2Entry(innerPoints[index-1], length * getArrowBodyLength(), direction);
        outerPoints[index] = getP2Entry(outerPoints[index-1], length * getArrowBodyLength(), direction);
        index++;
        innerPoints[index] = getP2Entry(innerPoints[index-1], length * (getArrowHeadWidth() - getArrowBodyWidth()), addDegree(direction, 90));
        outerPoints[index] = getP2Entry(outerPoints[index-1], length * (getArrowHeadWidth() - getArrowBodyWidth() + borderManager.getBorderThickness() * (-1 + getArrowHeadLength() / getArrowHeadWidth()) ), addDegree(direction, 90));
        index++;
        innerPoints[index] = new Entry(xAtoB(cx, length, direction), yAtoB(cy, length, direction));
        outerPoints[index] = new Entry(
                xAtoB(cx, length * (1 + (float) (borderManager.getBorderThickness() / Math.cos(Math.toRadians(45)))), direction),
                yAtoB(cy, length * (1 + (float) (borderManager.getBorderThickness() / Math.sin(Math.toRadians(45)))), direction)
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

        if(outerPaint != null) {
            canvas.drawPath(outerPath, outerPaint);
        }
        canvas.drawPath(innerPath, innerPaint);
    }
    public void drawCircle(Canvas canvas, Paint paint, float cx, float cy, float radius) {
        canvas.drawCircle(cx, cy, radius, paint);
    }
    public void drawBorderedCircle(Canvas canvas, Paint paint, float cx, float cy, float radius) {
        if(borderManager.getBorderPaint(paint) != null) {
            canvas.drawCircle(cx, cy, radius * (1 + borderManager.getBorderThickness()), borderManager.getBorderPaint(paint));
        }
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

    public void drawString(Canvas canvas, Paint paint, String text, float centerX, float centerY) {
        float textWidth = paint.measureText(text);
        float textHeight = paint.getTextSize();
        float textX = centerX - (textWidth / 2);
        float textY = centerY - textHeight;
        canvas.drawText(text, textX, textY, paint);
    }

    public String getPolygonStringByEntry(ArrayList<Entry> entries) {
        String polygon = "POLYGON ((";
        for (Entry entry:entries) {
            polygon += entry.getX() + " " + entry.getY() + ", ";
        }
        polygon += entries.get(0).getX() + " " + entries.get(0).getY();
        return polygon += "))";
    }
    public ArrayList<Entry> getEntryByGeometry(Geometry geometry) {
        ArrayList<Entry> entries = new ArrayList<>();
        for (Coordinate coordinate : geometry.getCoordinates()) {
            entries.add(new Entry((float) coordinate.x, (float) coordinate.y));
        }
        return entries;
    }
    public ArrayList<Entry> getEntriesByDrawable(int drawableID, float accuracy) throws XmlPullParserException, IOException {
        XmlResourceParser parser = getResources().getXml(drawableID);
        String pathData = null;

        while (parser.getEventType() != XmlResourceParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlResourceParser.START_TAG) {
                if (parser.getName().equals("path")) {
                    pathData = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "pathData");
                    break; // Stop parsing once you find the pathData
                }
            }
            parser.next();
        }

        if(pathData == null) {
            return new ArrayList<>();
        }

        Path path = PathParser.createPathFromPathData(pathData);
        PathMeasure pathMeasure = new PathMeasure(path, false);
        float pathLength = pathMeasure.getLength();
        float[] position = new float[2];
        ArrayList<Entry> entries = new ArrayList<>();

        for (float distance = 0; distance < pathLength; distance += accuracy) {
            pathMeasure.getPosTan(distance, position, null);
            float x = position[0];
            float y = position[1];
            entries.add(new Entry(x, y));
        }

        return entries;
    }
    public Entry getEntriesWidthHeight(ArrayList<Entry> entries) {
        ArrayList<Float> arrayListX = new ArrayList<>();
        ArrayList<Float> arrayListY = new ArrayList<>();
        for (Entry entry:entries) {
            arrayListX.add(entry.getX());
            arrayListY.add(entry.getY());
        }
        float[] arrayX = new float[arrayListX.size()];
        float[] arrayY = new float[arrayListY.size()];
        for (int i=0; i<arrayX.length; i++) {
            arrayX[i] = arrayListX.get(i);
            arrayY[i] = arrayListY.get(i);
        }
        Entry max = new Entry(
                OtherUsefulFunction.getMaxOf(arrayX),
                OtherUsefulFunction.getMaxOf(arrayY)
        );
        Entry min = new Entry(
                OtherUsefulFunction.getMinOf(arrayX),
                OtherUsefulFunction.getMinOf(arrayY)
        );
        return new Entry(max.getX() - min.getX(), max.getY() - min.getY());
    }

    public ArrayList<Entry> getOffsetEntries(ArrayList<Entry> entries, float offsetDistance) {
        String originalWKT = getPolygonStringByEntry(entries);
        GeometryFactory factory = new GeometryFactory();
        ArrayList<Entry> offsetEntries = new ArrayList<>();

        try {
            // Parse the original polygon from a Well-Known Text (WKT) string
            WKTReader reader = new WKTReader(factory);
            Geometry originalGeometry = reader.read(originalWKT);

            // Offset the polygon
            Geometry offsetGeometry = BufferOp.bufferOp(originalGeometry, offsetDistance);

            // Iterate through the coordinates of the offset polygon and store them as entries
            offsetEntries = getEntryByGeometry(offsetGeometry);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return offsetEntries;
    }
    public ArrayList<Entry> getScaleAndRotateEntries(ArrayList<Entry> entries, Entry scaleFactor, float rotateDegree) {
        String originalWKT = getPolygonStringByEntry(entries);
        GeometryFactory factory = new GeometryFactory();
        ArrayList<Entry> output = new ArrayList<>();

        try {
            WKTReader reader = new WKTReader(factory);

            // Parse the original polygon from a Well-Known Text (WKT) string
            Geometry originalGeometry = reader.read(originalWKT);

            // Calculate the centroid of the polygon (center point)
            Coordinate centroid = Centroid.getCentroid(originalGeometry.getBoundary());

            // Define rotation angle in radians and scale factor
            double r = Math.toRadians(rotateDegree);

            // Create an AffineTransformation to rotate and scale the polygon
            AffineTransformation transformation = AffineTransformation
                    .rotationInstance(r, centroid.x, centroid.y)
                    .scale(scaleFactor.getX(), scaleFactor.getY());

            // Apply the transformation to the original polygon
            originalGeometry.apply(transformation);
            output = getEntryByGeometry(originalGeometry);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }

    public void drawGivenEntryList(Canvas canvas, Paint innerPaint, ArrayList<Entry> entries, Entry position, boolean hasBorder, Entry scale, float rotateDegree) {
        entries = getScaleAndRotateEntries(entries, scale, rotateDegree);
        if(hasBorder) {
            drawGivenEntryList(canvas, borderManager.getBorderPaint(innerPaint), getOffsetEntries(entries, borderManager.getBorderThickness()), position);
        }
        drawGivenEntryList(canvas, innerPaint, entries, position);
    }
    public void drawGivenEntryList(Canvas canvas, Paint paint, ArrayList<Entry> entries, Entry position) {
        Path path = new Path();

        if(entries.size() == 0) {
            return;
        }

        path.moveTo(entries.get(0).getX() + position.getX(), entries.get(0).getY() + position.getY());
        for (int i = 1; i < entries.size(); i ++) {
            path.lineTo(entries.get(i).getX() + position.getX(), entries.get(i).getY() + position.getY());
        }
        path.close();
        canvas.drawPath(path, paint);
    }

}
