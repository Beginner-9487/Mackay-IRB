package com.example.mackayirb.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.example.mackayirb.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;

public abstract class MyLineChart extends LineChart {

    public MyLineChart(Context context) {
        super(context);
    }

    public MyLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        getLegend().setStackSpace(0.0f);    // 才不會使多條線段組合的這個方法露餡
        getLegend().setTextColor(OtherUsefulFunction.getBWColor(getResources()));
        getXAxis().setTextColor(OtherUsefulFunction.getBWColor(getResources()));
        getAxisLeft().setTextColor(OtherUsefulFunction.getBWColor(getResources()));
        getAxisRight().setTextColor(OtherUsefulFunction.getBWColor(getResources()));
        getDescription().setTextColor(OtherUsefulFunction.getBWColor(getResources()));
    }

    /**
     * 請參見 {@link MyLineChart#setMyData(LineData data)}
     */
    public LineData splitData = new LineData();
    /**
     * 請參見 {@link MyLineChart#setMyData(LineData data)}
     */
    public ArrayList<ArrayList<Entry>> entryList = new ArrayList<>();
    /**
     * 請參見 {@link MyLineChart#setMyData(LineData data)}
     */
    public ArrayList<Integer> groupList = new ArrayList<>();

    /**
     * 使 LineChart 能夠往任一方向繪製 (原本只能往右繪製) <br>
     * <br>
     * 具體作法是:
     * <br>
     * 1. 檢查傳入的 LineData 的每一比 Line <br>
     * 2. 使用 groupList.add(entryList.size()); 可以標記該 Line 在 entryList 的起始位置 <br>
     * 3. 一旦發現往左的 Line，就將其打斷，並使用 entryList.get(entryList.size()-1).add(e); 進行保存 <br>
     * 4. 這筆 Line 檢查完畢後，就使用，就使用 entryList.add(new ArrayList<Entry>()); 建立一個新的群組以準備檢查下一個 Line <br>
     * <br>
     * 例如傳入:
     * <br>
     * {<br>
     * [(0,y),(2,y),(1,y),(3,y)],<br>
     * [(2,y),(1,y),(0,y),(4,y),(9,y)],<br>
     * [(3,y),(4,y)],<br>
     * }<br>
     * 輸出為:
     * <br>
     * entryList =
     * {<br>
     * [[(0,y),(2,y)],[(1,y),(2,y)],[(1,y),(3,y)]],<br>
     * [[(0,y),(1,y),(2,y)],[(0,y),(4,y),(9,y)]],<br>
     * [[(3,y),(4,y)]],<br>
     * }<br>
     * groupList =
     * [0,3,5]
     * @param data
     */
    synchronized public void setMyData(LineData data) {

        splitData = new LineData();
        entryList = new ArrayList<>();
        groupList = new ArrayList<>();

        Boolean isAscending = null;

        // ==================================================================================
        // Get split Data
        for (int i = 0; i < data.getDataSets().size(); i++) {
            if(data.getDataSets().get(i).getEntryCount() == 0) {
                continue;
            }
            groupList.add(entryList.size());

            ArrayList<Entry> temp = new ArrayList<>();
            temp.add(data.getDataSets().get(i).getEntryForIndex(0));
            isAscending = null;
            for (int j = 1; j < data.getDataSets().get(i).getEntryCount(); j++) {
                // The ascending range is right here, up to the descending range.
                if (data.getDataSets().get(i).getEntryForIndex(j).getX() > data.getDataSets().get(i).getEntryForIndex(j-1).getX()) {
                    if (isAscending != null && isAscending.booleanValue() == false) {
                        entryList.add(new ArrayList<Entry>());
                        for (Entry e:temp) {
                            entryList.get(entryList.size()-1).add(e);
                        }
                        j--;
                        temp = new ArrayList<>();
                    }
                    isAscending = true;
                    temp.add(data.getDataSets().get(i).getEntryForIndex(j));
                // The descending range is right here, up to the ascending range.
                } else if (data.getDataSets().get(i).getEntryForIndex(j).getX() < data.getDataSets().get(i).getEntryForIndex(j-1).getX()) {
                    if (isAscending != null && isAscending.booleanValue() == true) {
                        entryList.add(new ArrayList<Entry>());
                        for (Entry e:temp) {
                            entryList.get(entryList.size()-1).add(e);
                        }
                        j--;
                        temp = new ArrayList<>();
                    }
                    isAscending = false;
                    temp.add(0, data.getDataSets().get(i).getEntryForIndex(j)); // reverse order
                // The first data of this round.
                } else {
                    if (isAscending != null && isAscending.booleanValue() == true) {
                        temp.add(data.getDataSets().get(i).getEntryForIndex(j));
                    } else {
                        temp.add(0, data.getDataSets().get(i).getEntryForIndex(j));
                    }
                }
                // The end of all data and this direction of the data are different from the previous round of data.
                if(j == data.getDataSets().get(i).getEntryCount() - 1 && temp.size() > 0) {
                    entryList.add(new ArrayList<Entry>());
                    for (Entry e : temp) {
                        entryList.get(entryList.size() - 1).add(e);
                    }
                }
            }
        }

        // ==================================================================================
        // Init
        int dataIndex = 0;
        ArrayList<ILineDataSet> iLineDataSet = new ArrayList<>();
        for (int i=0; i<entryList.size(); i++) {
            if(dataIndex < (groupList.size()-1) && i == groupList.get(dataIndex+1)) {
                dataIndex++;
            }
            LineDataSet lineDataSet = new LineDataSet(entryList.get(i), data.getDataSetLabels()[dataIndex]);
            iLineDataSet.add(lineDataSet);
        }
        splitData = new LineData(new ArrayList<ILineDataSet>(iLineDataSet));
        setLineDataAttribute(splitData.getDataSets());

        setAllShowArray();
        refreshChart();
    }
    public abstract void setLineDataAttribute(List<ILineDataSet> iLineDataSet);
    public int getDataIndex(ILineDataSet lineDataSet) {
        int dataIndex = 0;
        for (int i=0; i<splitData.getDataSets().size(); i++) {
            if(dataIndex < (groupList.size()-1) && i == groupList.get(dataIndex+1)) {
                dataIndex++;
            }
            if(splitData.getDataSets().get(i).equals(lineDataSet)) {
                return dataIndex;
            }
        }
        return -1;
    }
    public int getDataIndex(int highlightedIndex) {
        // Log.d("size(): " + String.valueOf(groupList.size()) + groupList.toString());
        for (int i=0; i<groupList.size()-1; i++) {
            if(highlightedIndex >= groupList.get(i).intValue() && highlightedIndex < groupList.get(i+1).intValue()) {
                return i;
            }
        }
        return 0;
    }

    ArrayList<Boolean> showArray = new ArrayList<>();
    LineData finalShowData = new LineData();
    synchronized public void setAllShowArray(ArrayList<Boolean> showArray) {
        this.showArray = showArray;
        setAllShowArray();
    }
    synchronized public void setAllShowArray() {
        while (showArray.size() < groupList.size()) {
            showArray.add(true);
        }
        finalShowData = new LineData();

        for (int i=0, j=0; i<entryList.size();) {
            if(j < groupList.size() - 1) {
                while (i < groupList.get(j + 1)) {
                    if(showArray.get(j).booleanValue()) {
                        finalShowData.addDataSet(((LineDataSet) splitData.getDataSets().get(i)));
                    }
                    i++;
                }
            } else {
                while (i < entryList.size()) {
                    if(showArray.get(j).booleanValue()) {
                        finalShowData.addDataSet(((LineDataSet) splitData.getDataSets().get(i)));
                    }
                    i++;
                }
            }
            j++;
        }

        refreshChart();
    }

    synchronized public void refreshChart() {
        setData(finalShowData);

        // ==================================================================================
        // Set Label
        int dataIndex = 0;
        for (int i=0, j=0; i<entryList.size(); i++) {
            if(dataIndex < (groupList.size()-1) && i == groupList.get(dataIndex+1)) {
                dataIndex++;
            }
            if(showArray.size() == 0 || dataIndex >= showArray.size()) {
                break;
            }
            if(showArray.get(dataIndex).booleanValue()) {
                if(i != groupList.get(dataIndex)) {
                    getLegend().getEntries()[i-j].label = null;
                    getLegend().getEntries()[i-j].form = Legend.LegendForm.NONE;
                }
            } else {
                j++;
            }
        }

        this.invalidate();  // refresh
    }

    public void highlightValue(float x) {
        highlightValue(x, 0);
    }

    @Override
    public void highlightValue(float x, float y, int dataSetIndex, boolean callListener) {
        Float absDiffX = null;
        int index = 0;
        for(int i = 0; i< finalShowData.getDataSets().size(); i++) {
            for (int j = 0; j< finalShowData.getDataSets().get(i).getEntryCount(); j++) {
                if(absDiffX == null) {
                    absDiffX = Math.abs(x - finalShowData.getDataSets().get(i).getEntryForIndex(j).getX());
                } else if (absDiffX > Math.abs(x - finalShowData.getDataSets().get(i).getEntryForIndex(j).getX())) {
                    absDiffX = Math.abs(x - finalShowData.getDataSets().get(i).getEntryForIndex(j).getX());
                    index = i;
                }
            }
        }
        super.highlightValue(x, Float.NaN, index, callListener);
    }

    public float contentScaleXRatio = 1.00f;
    public float contentScaleYRatio = 0.84f;
    public Entry getChartEntryByValueEntry(Entry value, Entry max, Entry min) {
        float iconOffsetX = 0f;
        float iconOffsetY = 0f;

        ViewPortHandler viewPortHandler = getViewPortHandler();
        float contentWidth = viewPortHandler.contentWidth();
        float contentHeight = viewPortHandler.contentHeight();
        float scaleX = viewPortHandler.getScaleX();
        float scaleY = viewPortHandler.getScaleY();
        float offsetLeft = viewPortHandler.offsetLeft();
        float offsetTop = viewPortHandler.offsetTop();

        return new Entry(
                offsetLeft
                + contentWidth * scaleX * contentScaleXRatio * (
                        OtherUsefulFunction.getRatio(value.getX(), max.getX(), min.getX())
                )
                + contentWidth * scaleX * (
                        OtherUsefulFunction.getRatio(viewPortHandler.getTransX() / scaleX , contentWidth , 0)
                )
                + contentWidth * (scaleX - 1f) * OtherUsefulFunction.getRatio(2 * min.getX(), max.getX(), min.getX())
                + iconOffsetX,
                offsetTop
                + 0.5f * contentHeight
                + (contentHeight * scaleY * contentScaleYRatio * (
                (
                        (1f - OtherUsefulFunction.getRatio(value.getY(), max.getY(), min.getY())) - 0.5f
                )
                ))
                + (contentHeight * scaleY * (
                (
                        OtherUsefulFunction.getRatio(viewPortHandler.getTransY() / scaleY , contentHeight, 0)
                )
                ))
                + contentHeight * (scaleY - 1f) * OtherUsefulFunction.getRatio(2 * min.getY(), max.getY(), min.getY())
                + iconOffsetY
        );
    }

    public boolean checkInsideContent(Entry entry) {
        ViewPortHandler viewPortHandler = getViewPortHandler();
        float contentWidth = viewPortHandler.contentWidth();
        float contentHeight = viewPortHandler.contentHeight();
        if(entry == null) {
            return false;
        }
        return (
                entry.getX() < viewPortHandler.offsetLeft() ||
                entry.getX() > viewPortHandler.offsetLeft() + contentWidth ||
                entry.getY() < viewPortHandler.offsetTop() ||
                entry.getY() > viewPortHandler.offsetTop() + contentHeight
        );
    }

    public Entry getIconRealScaleInChart(Entry expectEntry, Entry iconWidthHeight, Entry max, Entry min) {
        ViewPortHandler viewPortHandler = getViewPortHandler();
        float contentWidth = viewPortHandler.contentWidth();
        float contentHeight = viewPortHandler.contentHeight();
        float scaleX = viewPortHandler.getScaleX();
        float scaleY = viewPortHandler.getScaleY() * contentScaleYRatio;
        return new Entry(
                expectEntry.getX() / ((iconWidthHeight.getX() / contentWidth) * (max.getX() - min.getX())) * scaleX,
                expectEntry.getY() / ((iconWidthHeight.getY() / contentHeight) * (max.getY() - min.getY())) * scaleY
        );
    }

    public void drawIconAtPoint(MyCanvasDrawer myCanvasDrawer, Canvas canvas, Bitmap bitmap, Entry entry) {
        drawIconAtPoint(
                myCanvasDrawer,
                canvas,
                bitmap,
                entry,
                null, null, null
        );
    }
    public void drawIconAtPoint(MyCanvasDrawer myCanvasDrawer, Canvas canvas, Bitmap bitmap, Entry entry, @Nullable Float scaleX, @Nullable Float scaleY) {
        drawIconAtPoint(
                myCanvasDrawer,
                canvas,
                bitmap,
                entry,
                scaleX,
                scaleY,
                null
        );
    }
    public void drawIconAtPoint(MyCanvasDrawer myCanvasDrawer, Canvas canvas, Bitmap bitmap, Entry entry, @Nullable Float scaleX, @Nullable Float scaleY, @Nullable Float direction) {
        scaleX = scaleX != null ? scaleX : 1f;
        scaleY = scaleY != null ? scaleY : 1f;
        direction = direction != null ? direction : 0f;
        myCanvasDrawer.drawBackgroundImage(
                canvas,
                bitmap,
                entry.getX(),
                entry.getY(),
                scaleX,
                scaleY,
                direction
        );
    }

}
