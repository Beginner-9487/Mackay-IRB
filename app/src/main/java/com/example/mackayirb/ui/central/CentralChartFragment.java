package com.example.mackayirb.ui.central;

import com.example.mackayirb.R;
import com.example.mackayirb.adapter.ChartDataMonitorAdapter;
import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.data.central.MackayDataManager;
import com.example.mackayirb.data.central.MackayDeviceData;
import com.example.mackayirb.data.central.MackayLabelData;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.MyLineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//import butterknife.BindView;
//import butterknife.ButterKnife;

public class CentralChartFragment extends CentralFragment implements CentralMvpView {

    Button buttonZoomOut;
    Button buttonDataMonitorSelector;
    MyLineChart mLineChart;
    EditText editHighlightSelector;
    RecyclerView mDataMonitor;
    ChartDataMonitorAdapter mDataMonitorAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCentralPresenter.initForCentralDataManager(CentralPresenter.MackayDataManager);
    }

    @Override
    public void doSomethingFrequently() {
        updateChart();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_chart;
    }
    public int getDataMonitorMode() {
        return ChartDataMonitorAdapter.NormalMode;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = initView(inflater, container, savedInstanceState);
        initDataMonitor(view, getDataMonitorMode());
        return view;
    }
    public View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        buttonDataMonitorSelector = view.findViewById(R.id.DataMonitorSelector);
        buttonZoomOut = view.findViewById(R.id.ZoomOut_Button);
        buttonZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeHighlight();
                mLineChart.zoomOut();
            }
        });
        mLineChart = view.findViewById(R.id.LineChart);
        mLineChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHighlightedData();
            }
        });
        mLineChart.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                // Check if the view's size has changed
                if (right - left != oldRight - oldLeft || bottom - top != oldBottom - oldTop) {
                    // View size has changed, do something
                    // For example, update the layout or perform some calculations
                    mLineChart.refreshChart();
                    // Log.e(String.valueOf(left) + ", " + top + ", " + right + ", " + bottom + ", " + oldLeft + ", " + oldTop + ", " + oldRight + ", " + oldBottom);
                }
            }
        });
        editHighlightSelector = view.findViewById(R.id.HighlightSelector);
        editHighlightSelector.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    // Log.e(charSequence.toString());
                    setHighlight(Float.valueOf(charSequence.toString()));
                } catch (Exception e) {
                    Log.e(e.getMessage());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) { }
        });
        return view;
    }
    public void initDataMonitor(View view, int Mode) {
        mDataMonitorAdapter = new ChartDataMonitorAdapter().setMode(Mode);
        mDataMonitor = view.findViewById(R.id.DataMonitor);
        mDataMonitor.setAdapter(mDataMonitorAdapter);
        mDataMonitor.setHasFixedSize(true);
        mDataMonitor.setItemAnimator(new DefaultItemAnimator());
        mDataMonitor.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDataMonitorAdapter.setListener(new ChartDataMonitorAdapter.DataItemClickListener() {});
    }

    public void setAllShowArray(ArrayList<Boolean> booleans) {
        ((MackayDataManager) mCentralPresenter.getCentralDataManager()).setAllShowArray(booleans);
        mLineChart.setAllShowArray(booleans);
    }
    public void setTypeShowArray(ArrayList<Boolean> booleans) {
        ((MackayDataManager) mCentralPresenter.getCentralDataManager()).setTypeShowArray(booleans);
        mLineChart.setAllShowArray(((MackayDataManager) mCentralPresenter.getCentralDataManager()).getAllShowArray());
    }

    ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
    public void updateChart() {
        setLineData();
        mLineChart.refreshChart();
        setDataMonitorSelector();
        showHighlightedData();
    }
    public void setLineData() {
        try {
            iLineDataSets.clear();
            for (MackayDeviceData mackayDeviceData : ((MackayDataManager) (mCentralPresenter.getCentralDataManager())).getDeviceData()) {
                for (final MackayLabelData centralLabelData : mackayDeviceData.labelData) {
                    iLineDataSets.add(new LineDataSet(centralLabelData.getSpecialEntries(), centralLabelData.labelName));
                }
            }
            mLineChart.setMyData(new LineData(iLineDataSets));
            mLineChart.setAllShowArray(((MackayDataManager) mCentralPresenter.getCentralDataManager()).getAllShowArray());
        } catch (Exception e) {}
    }
    public void setDataMonitorSelector() {
        if(buttonDataMonitorSelector != null) {
            buttonDataMonitorSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDataMonitorManager().show();
                }
            });
        }
    }

    private boolean checkShow = true;
    public void setLayoutId(boolean CheckShow) {
        checkShow = CheckShow;
    }
    public void showHighlightedData() {
        if(mLineChart.getHighlighted() == null) {
            if(mDataMonitorAdapter != null) {
                // mLineChart.zoomOut();    // auto zoom out
            }
        } else {
            if(mDataMonitorAdapter != null) {
                mDataMonitorAdapter.setX(mLineChart.getHighlighted()[0].getX());
                mDataMonitorAdapter.clearData();
                for (MackayLabelData l : ((MackayDataManager) mCentralPresenter.getCentralDataManager()).getAllLabelData()) {
                    if(!checkShow || l.show) {
                        mDataMonitorAdapter.addData(l);
                    }
                }
                mDataMonitorAdapter.notifyDataSetChanged();
            }
        }
    }
    public void removeHighlight() {
        mLineChart.highlightValue(null);
        showHighlightedData();
    }
    public void setHighlight(float x, int dataSetIndex) {
        mLineChart.highlightValue(x, dataSetIndex);
        showHighlightedData();
    }
    public void setHighlight(float x) {
        mLineChart.highlightValue(x);
        showHighlightedData();
    }

    @Override
    public void showBLEDevice(BluetoothDevice bt) {

    }

    @Override
    public void showBLEData(BLEDataServer.BLEData bleData) {

    }

    private AlertDialog getDataMonitorManager() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        String[] typeArray = getResources().getStringArray(R.array.AboutDataMonitorSelector);
        boolean[] typeBooleans = new boolean[typeArray.length];
        for (int i=0; i<typeBooleans.length; i++) {
            typeBooleans[i] = false;
        }

        final AlertDialog dialog = builder
                .setTitle(getResources().getString(R.string.AboutDataMonitorManager))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(android.R.string.cancel), null)
                .setMultiChoiceItems(typeArray, typeBooleans, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                // =====================================================================================
                // MultiChoiceItems
                dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            getMyMultipleSelector(i).show();
                        } catch (Exception e) {}
                        dialog.dismiss();
                    }
                });

                // =====================================================================================
                // Cancel
                final Button buttonCancel = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                buttonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        return dialog;
    }

    boolean[] selectionArrayBuffer;
    private AlertDialog getMyMultipleSelector(final int type) {
        String Title;
        boolean Cancelable;

        // Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        String[] typeArray = getResources().getStringArray(R.array.AboutDataMonitorSelector);
        Title = typeArray[type];
        Cancelable = true;

        // Build
        String[] labelNameArrayBuffer;
        switch(type) {
            case 0:
            case 2:
                selectionArrayBuffer = new boolean[((MackayDataManager) mCentralPresenter.getCentralDataManager()).getAllShowArray().size()];
                labelNameArrayBuffer = ((MackayDataManager) mCentralPresenter.getCentralDataManager()).getAllLabelNameArray().toArray(new String[0]);
                break;
            case 1:
            case 3:
                selectionArrayBuffer = new boolean[((MackayDataManager) mCentralPresenter.getCentralDataManager()).getDataTypes().length];
                labelNameArrayBuffer = ((MackayDataManager) mCentralPresenter.getCentralDataManager()).getDataTypes();
                break;
            default:
                labelNameArrayBuffer = new String[0];
        }
        if(selectionArrayBuffer.equals(null)) {
            return null;
        }
        for (int i=0; i<selectionArrayBuffer.length; i++) {
            switch(type) {
                case 0:
                    selectionArrayBuffer[i] = ((MackayDataManager) mCentralPresenter.getCentralDataManager()).getAllShowArray().get(i).booleanValue();
                    break;
                case 1:
                    selectionArrayBuffer[i] = true;
                    break;
                case 2:
                case 3:
                    selectionArrayBuffer[i] = false;
                    break;
                default:
            }
        }
        final AlertDialog dialog = builder
                .setTitle(Title)
                .setCancelable(Cancelable)
                .setPositiveButton(getResources().getString(android.R.string.ok), null)
                .setNegativeButton(getResources().getString(R.string.Fragment_SelectAll), null)
                .setNeutralButton(R.string.Fragment_Reset, null)
                .setMultiChoiceItems(labelNameArrayBuffer, selectionArrayBuffer, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                // =====================================================================================
                // MultiChoiceItems
                dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectionArrayBuffer[i] = !selectionArrayBuffer[i];
                        for (boolean b: selectionArrayBuffer) {
                            if(b) {
                                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setText(getResources().getString(R.string.Fragment_ClearAll));
                                break;
                            } else {
                                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setText(getResources().getString(R.string.Fragment_SelectAll));
                            }
                        }
                    }
                });

                // =====================================================================================
                // Reset
                Button buttonReset = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                buttonReset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch(type) {
                            case 0:
                            case 2:
                                for (int i=0; i<((MackayDataManager) mCentralPresenter.getCentralDataManager()).getAllShowArray().size(); i++) {
                                    selectionArrayBuffer[i] = ((MackayDataManager) mCentralPresenter.getCentralDataManager()).getAllShowArray().get(i).booleanValue();
                                    dialog.getListView().setItemChecked(i, selectionArrayBuffer[i]);
                                }
                                break;
                            case 1:
                            case 3:
                                for (int i=0; i<selectionArrayBuffer.length; i++) {
                                    selectionArrayBuffer[i] = true;
                                    dialog.getListView().setItemChecked(i, selectionArrayBuffer[i]);
                                }
                                break;
                            default:
                        }
                        for (boolean b: selectionArrayBuffer) {
                            if(b) {
                                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setText(getResources().getString(R.string.Fragment_ClearAll));
                                break;
                            } else {
                                ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setText(getResources().getString(R.string.Fragment_SelectAll));
                            }
                        }
                    }
                });

                // =====================================================================================
                // Change All
                final Button buttonChangeAll = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                for (boolean b: selectionArrayBuffer) {
                    if(b) {
                        buttonChangeAll.setText(getResources().getString(R.string.Fragment_ClearAll));
                        break;
                    }
                }
                buttonChangeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean b = buttonChangeAll.getText().equals(getResources().getString(R.string.Fragment_SelectAll));
                        for (int i=0; i<selectionArrayBuffer.length; i++) {
                            selectionArrayBuffer[i] = b;
                            dialog.getListView().setItemChecked(i, b);
                        }
                        buttonChangeAll.setText((b)?getResources().getString(R.string.Fragment_ClearAll):getResources().getString(R.string.Fragment_SelectAll));
                    }
                });

                // =====================================================================================
                // OK
                Button buttonOk = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ArrayList<Boolean> bs = new ArrayList<>();
                        for (boolean b: selectionArrayBuffer) {
                            bs.add(b);
                        }
                        switch(type) {
                            case 0:
                                setAllShowArray(bs);
                                break;
                            case 1:
                                setTypeShowArray(bs);
                                break;
                            case 2:
                                ((MackayDataManager) mCentralPresenter.getCentralDataManager()).deleteSelectedData(bs);
                                break;
                            case 3:
                                ((MackayDataManager) mCentralPresenter.getCentralDataManager()).deleteSelectedType(bs);
                                break;
                            default:
                        }
                        updateChart();
                        dialog.dismiss();
                    }
                });
            }
        });

        return dialog;

    }
}
