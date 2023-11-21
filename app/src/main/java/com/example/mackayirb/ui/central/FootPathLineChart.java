package com.example.mackayirb.ui.central;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.MyCanvasDrawer;
import com.example.mackayirb.utils.MyLineChart;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FootPathLineChart extends MyLineChart {
    public FootPathLineChart(Context context) {
        super(context);
    }

    public FootPathLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FootPathLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        getLegend().setEnabled(false);
//        setMaxVisibleValueCount(1000);
//        setAutoScaleMinMaxEnabled(true);
        mRenderer = new FootPathChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    public void setLineDataAttribute(List<ILineDataSet> iLineDataSet) {
        for (ILineDataSet l:iLineDataSet) {
            LineDataSet lineDataSet = (LineDataSet) l;
            float line_width = 0f;
            lineDataSet.setMode(LineDataSet.Mode.STEPPED);
            lineDataSet.setDrawCircles(false);
            lineDataSet.setLineWidth(line_width);
            lineDataSet.setHighlightLineWidth(line_width);

            lineDataSet.setHighLightColor(OtherUsefulFunction.getBWColor(getResources()));
            lineDataSet.setDrawValues(false);
            lineDataSet.setColor(OtherUsefulFunction.getBWColor(BasicResourceManager.getResources()));
            lineDataSet.setDrawIcons(true);
            lineDataSet.setVisible(false);  // Set false to prevent the line from being drawn (In fact, setting false will stop showing everything about this lineDataSet such as lines, values, icons, but I only want to show icons, so I overridden the onDraw() method to solve this problem.)
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        // releases the bitmap in the renderer to avoid oom error
        if (mRenderer != null && mRenderer instanceof FootPathChartRenderer) {
            ((FootPathChartRenderer) mRenderer).releaseBitmap();
        }
        super.onDetachedFromWindow();
    }

    MyCanvasDrawer myCanvasDrawer = new MyCanvasDrawer(new MyCanvasDrawer.BorderManager() {
        @Override
        public float getBorderThickness() {
            return 2;
        }

        @Override
        public int getBorderColor() {
            return OtherUsefulFunction.getBWColor(BasicResourceManager.getResources());
        }

        @Override
        public Paint getBorderPaint(Paint innerPaint) {
            return new Paint();
        }
    });

    public class FootIcon extends Drawable {

        public final static float footSizeX = 0.085f;
        public final static float footSizeY = 0.25f;
        public int xmlId;
//        public float rotateDegree = (float) (Math.random() * 360f);
        public float rotateDegree = 0;
        public float getRotateRadian() {
            return (float) Math.toRadians(rotateDegree);
        }
        public int color = 0;
        public float accuracy = 2f;

        @Override
        public void draw(@NonNull Canvas canvas) {

        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        ArrayList<Float> arrayListX = new ArrayList<>();
        ArrayList<Float> arrayListY = new ArrayList<>();
        for (ArrayList<Entry> entries:entryList) {
            for (Entry entry:entries) {
                arrayListX.add(entry.getX());
                arrayListY.add(entry.getY());
            }
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

        ViewPortHandler viewPortHandler = getViewPortHandler();
        Entry fittedZoomScale = new Entry(
                (max.getX() - min.getX()) / ((max.getX() - min.getX()) + FootIcon.footSizeX),
                (max.getY() - min.getY()) / ((max.getY() - min.getY()) + FootIcon.footSizeY)
        );
        Entry chartWH = new Entry(
                (viewPortHandler.getContentRect().centerX() - viewPortHandler.getContentRect().left) * (0.36f * FootIcon.footSizeX / ((max.getX() - min.getX()) + FootIcon.footSizeX)),
                (viewPortHandler.getContentRect().centerY() - viewPortHandler.getContentRect().top) * (0.36f * FootIcon.footSizeY / ((max.getY() - min.getY()) + FootIcon.footSizeY))
        );
//        Log.d(String.valueOf(chartWH));
        chartWH.setX((chartWH.getX() == Float.POSITIVE_INFINITY || chartWH.getX() == Float.NEGATIVE_INFINITY) ? 0f: chartWH.getX());
        chartWH.setY((chartWH.getY() == Float.POSITIVE_INFINITY || chartWH.getY() == Float.NEGATIVE_INFINITY) ? 0f: chartWH.getY());
        viewPortHandler.setMaximumScaleX(fittedZoomScale.getX());
        viewPortHandler.setMaximumScaleY(fittedZoomScale.getY());
        viewPortHandler.setDragOffsetX(chartWH.getX());
        viewPortHandler.setDragOffsetY(chartWH.getY());

        for (int i=0; i<splitData.getDataSets().size(); i++) {
            for (int j=0; j<splitData.getDataSets().get(i).getEntryCount(); j++) {
                Entry iconEntry = ((FootPathChartRenderer) mRenderer).getIconEntry(splitData.getDataSets().get(i), j);
                // Log.d("iconEntry: " + String.valueOf(splitData.getDataSets().get(i).getEntryForIndex(j)) + "," + String.valueOf(iconEntry));
                if(
//                        checkInsideContent(iconEntry) ||
                        iconEntry == null
                ) {
                    continue;
                }
                try {
                    FootIcon footIcon = (FootIcon) splitData.getDataSets().get(i).getEntryForIndex(j).getIcon();
                    ArrayList<Entry> iconEntries = myCanvasDrawer.getEntriesByDrawable(footIcon.xmlId, footIcon.accuracy);
                    Paint paint = new Paint();
                    paint.setColor(footIcon.color);
                    myCanvasDrawer.drawGivenEntryList(
                            canvas,
                            paint,
                            iconEntries,
                            iconEntry,
                            true,
                            getIconRealScaleInChart(
                                    new Entry(FootIcon.footSizeX, FootIcon.footSizeY),
                                    myCanvasDrawer.getEntriesWidthHeight(iconEntries),
                                    max,
                                    min
                            ),
//                            new Entry(1,1),
                            footIcon.rotateDegree
                    );
                } catch (XmlPullParserException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

//        viewPortHandler.setMaximumScaleX(0);
//        viewPortHandler.setMaximumScaleY(0);

    }

}