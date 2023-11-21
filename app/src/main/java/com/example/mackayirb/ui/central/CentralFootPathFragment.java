package com.example.mackayirb.ui.central;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mackayirb.R;
import com.example.mackayirb.adapter.FootPathDataMonitorAdapter;
import com.example.mackayirb.data.central.FootLabelData;
import com.example.mackayirb.data.central.FootManagerData;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.MyExcelFile;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

public class CentralFootPathFragment extends CentralChartWithMonitorFragment<FootPathDataMonitorAdapter, FootPathLineChart> implements CentralMvpView {

    public Button buttonStart;
    public Button buttonEnd;
    public Button buttonReset;
    public Button buttonSave;
    public LinkedHashMap<Entry, ArrayList<ArrayList<Entry>>> pathEntriesMap = new LinkedHashMap<>();
    public LinkedHashMap<Integer, String> MonitorInformation = new LinkedHashMap<>();

    int SAMPLING_RATE = 20;

    @Override
    public LinearLayoutManager getLinearLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.central_foot_path;
    }

    @Override
    public void setDataMonitorAdapter() {
        dataMonitorAdapter = new FootPathDataMonitorAdapter();
    }

    boolean startLock = false;
    int startID = 0;
    @Override
    public View initView(View view) {
        view = super.initView(view);
        buttonStart = view.findViewById(R.id.StartButton);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startID = ((FootManagerData) (mCentralPresenter.getCentralDataManager())).finalFootSequenceID;
                startLock = true;
            }
        });
        buttonEnd = view.findViewById(R.id.EndButton);
        buttonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!startLock) {
                    return;
                }
                int endID = ((FootManagerData) (mCentralPresenter.getCentralDataManager())).finalFootSequenceID;
                pathEntriesMap.put(
                        new Entry(startID, endID),
                        getCurrentPath()
                );
                updateAllIcon();
                Log.d(String.valueOf(pathEntriesMap.size()));
                startLock = false;
            }
        });
        buttonReset = view.findViewById(R.id.ResetButton);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathEntriesMap = new LinkedHashMap<>();
            }
        });
        buttonSave = view.findViewById(R.id.SaveButton);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH-mm-ss");
                            String currentTime = sdf.format(calendar.getTime());

                            // Log.i(labelName);
                            MyExcelFile file = new MyExcelFile();
                            String sdCardPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator;
                            String fileName = currentTime + "_Path";
                            file.createExcelWorkbook(sdCardPath + fileName + ".xlsx");

                            int sheetIndex = 0;
                            for (Map.Entry<Entry, ArrayList<ArrayList<Entry>>> entry:pathEntriesMap.entrySet()) {
                                int startID = (int) entry.getKey().getX();
                                int endID = (int) entry.getKey().getY();
                                file.create_new_sheet(
                                        String.valueOf(startID)
                                        + " to "
                                        + String.valueOf(endID)
                                );
                                ArrayList<ArrayList<ArrayList<Entry>>> AVS = new ArrayList<>();
                                AVS.add(((FootManagerData) (mCentralPresenter.getCentralDataManager())).calc_AxAy(startID, endID, false));
                                AVS.add(((FootManagerData) (mCentralPresenter.getCentralDataManager())).calc_AxAy(startID, endID, true));
                                AVS.add(((FootManagerData) (mCentralPresenter.getCentralDataManager())).calc_VxVy(startID, endID, SAMPLING_RATE, false));
                                AVS.add(((FootManagerData) (mCentralPresenter.getCentralDataManager())).calc_VxVy(startID, endID, SAMPLING_RATE, true));
                                AVS.add(((FootManagerData) (mCentralPresenter.getCentralDataManager())).calc_SxSy(startID, endID, SAMPLING_RATE));
                                String[] AVS_STRING = new String[]{
                                        "Acceleration",
                                        "Acceleration Correction",
                                        "Velocity",
                                        "Velocity Correction",
                                        "Displacement"
                                };
                                for(int i = 0; i<AVS.size(); i++) {
                                    int columnIndex = i*4;
                                    file.write_file(
                                            sheetIndex,
                                            0,
                                            columnIndex,
                                            AVS_STRING[i]
                                    );
                                    for(int footIndex = 0; footIndex<AVS.get(i).size(); footIndex++) {
                                        file.write_file(
                                                sheetIndex,
                                                1,
                                                columnIndex,
                                                String.valueOf(BasicResourceManager.getResources().getStringArray(R.array.Foot)[footIndex])
                                        );
                                        for(int j = 0; j<AVS.get(i).get(footIndex).size(); j++) {
                                            int rowIndex = j+2;
                                            file.write_file(
                                                    sheetIndex,
                                                    rowIndex,
                                                    columnIndex,
                                                    String.valueOf(AVS.get(i).get(footIndex).get(j).getX())
                                            );
                                            file.write_file(
                                                    sheetIndex,
                                                    rowIndex,
                                                    columnIndex + 1,
                                                    String.valueOf(AVS.get(i).get(footIndex).get(j).getY())
                                            );
                                        }
                                        columnIndex += 2;
                                    }
                                }
                                sheetIndex++;
                            }
                            // Save as Excel XLSX file
                            if (file.exportDataIntoWorkbook()) {
                                Log.i(BasicResourceManager.getResources().getString(R.string.Temp_UI_save_toast));
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Toast.makeText(BasicResourceManager.getCurrentActivity(), fileName + ": " + BasicResourceManager.getResources().getString(R.string.Temp_UI_save_toast), Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {}
                                    }
                                });
                            }
                        } catch (Exception e) {
                            Log.e(e.getMessage());
                            Log.e(e.toString());
                        }
                    }
                }).start();
            }
        });
        return view;
    }

    public ArrayList<ArrayList<Entry>> getCurrentPath() {
        int endID = ((FootManagerData) (mCentralPresenter.getCentralDataManager())).finalFootSequenceID;
        ArrayList<ArrayList<Entry>> output = ((FootManagerData) (mCentralPresenter.getCentralDataManager())).calc_SxSy(startID, endID, SAMPLING_RATE);
        for (int i=0; i<output.size(); i++) {
            reverseXY(output.get(i));
        }
        return output;
    }

    public ArrayList<Entry> getStartEnd(ArrayList<Entry> entries) {
        ArrayList<Entry> output = new ArrayList<>();
        if(entries.size() > 0) {
            output.add(entries.get(0));
        }
        if(entries.size() > 1) {
            output.add(entries.get(entries.size() - 1));
        }
        return output;
    }
    public ArrayList<Entry> getPart(ArrayList<Entry> entries) {
        ArrayList<Entry> output = new ArrayList<>();
        int number = 5;
        for(int i=0; i<entries.size(); ) {
            output.add(entries.get(i));
            i += (entries.size()/number > 1) ? entries.size()/number : 1;
        }
        return output;
    }
    public ArrayList<Entry> addIcon(ArrayList<Entry> entries, int foot, boolean isGray, int dataIndex, float contrastRadioMax, float contrastRadioMin) {
        int drawableID = 0;
        switch (foot) {
            case 0:
                drawableID = R.drawable.foot_icon_left;
                break;
            case 1:
                drawableID = R.drawable.foot_icon_right;
                break;
        }
        for (int i = 0; i < entries.size(); i++) {
            FootPathLineChart.FootIcon drawable = getLineChart(). new FootIcon();
            drawable.xmlId = drawableID;
            if(isGray) {
                drawable.color = OtherUsefulFunction.getDataGray(
                        BasicResourceManager.getResources(),
                        ((contrastRadioMax - contrastRadioMin) * i / entries.size()) + contrastRadioMin
                );
            } else {
                drawable.color = OtherUsefulFunction.getDataColor(
                        BasicResourceManager.getResources(),
                        (int) Math.floor(dataIndex),
                        (int) Math.floor(pathEntriesMap.size()),
                        ((contrastRadioMax - contrastRadioMin) * i / entries.size()) + contrastRadioMin
                );
            }
            entries.get(i).setIcon(drawable);
        }
        return entries;
    }
    public void updateAllIcon() {
        int dataIndex = 0;
        for (ArrayList<ArrayList<Entry>> pathValue:pathEntriesMap.values()) {
            for (int i = 0; i < pathValue.size(); i++) {
                pathValue.set(i, addIcon(pathValue.get(i), i, false, dataIndex, 0.0f, -0.75f));
            }
            dataIndex++;
        }
    }
    public ArrayList<Entry> reverseXY(ArrayList<Entry> entries) {
        for(int i=0; i<entries.size(); i++) {
            float x = entries.get(i).getX();
            float y = entries.get(i).getY();
            entries.get(i).setX(y);
            entries.get(i).setY(x);
        }
        return entries;
    }

    @Override
    public ArrayList<ILineDataSet> setLineDataSetsForChart() {
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        int dataIndex = 0;
        for (ArrayList<ArrayList<Entry>> pathValue:pathEntriesMap.values()) {
            for (int i = 0; i < pathValue.size(); i++) {
//                Log.d(String.valueOf(pathValue.get(i).size()));
                iLineDataSets.add(new LineDataSet(
                        getStartEnd(
                                pathValue.get(i)
                        )
                        ,
                        BasicResourceManager.getResources().getStringArray(R.array.Foot)[i]));
//                iLineDataSets.add(new LineDataSet(getPart(pathValue.get(i)), BasicResourceManager.getResources().getStringArray(R.array.Foot)[i]));
//                iLineDataSets.add(new LineDataSet(pathValue.get(i), BasicResourceManager.getResources().getStringArray(R.array.Foot)[i]));
            }
            dataIndex++;
        }
        if(startLock) {
            int i = 0;
            for (ArrayList<Entry> entries: getCurrentPath()) {
                iLineDataSets.add(new LineDataSet(
                        getPart(
                                addIcon(
                                        entries,
                                        i,
                                        true,
                                        0,
                                        1.0f,
                                        -0.25f
                                )
                        )
                        ,
                        BasicResourceManager.getResources().getStringArray(R.array.Foot)[i])
                );
                i++;
            }
        }
        return iLineDataSets;
    }

    @Override
    public void showHighlightedData() {
        if(lineChart.getHighlighted() != null) {
            MonitorInformation.put(
                    new Integer(lineChart.getDataIndex(lineChart.getHighlighted()[0].getStackIndex()))
                    ,
                    "X: " + lineChart.getHighlighted()[0].getX() + ", " +
                    "Y: " + lineChart.getHighlighted()[0].getY()
            );
        }
        super.showHighlightedData();
    }

    @Override
    public void addDataIntoMonitor() {
        for (int i=0; i<MonitorInformation.size(); i++) {
            getDataMonitorAdapter().addData(MonitorInformation.get(i), 0);
        }
    }

}
