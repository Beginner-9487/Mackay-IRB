package com.example.mackayirb.ui.central;

import com.example.mackayirb.R;
import com.example.mackayirb.adapter.MackayDataMonitorAdapter;
import com.example.mackayirb.data.ble.BLEDataServer;
import com.example.mackayirb.data.central.MackayManagerData;
import com.example.mackayirb.data.central.MackayLabelData;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//import butterknife.BindView;
//import butterknife.ButterKnife;

public class CentralMackayFragment extends CentralChartWithMonitorFragment<MackayDataMonitorAdapter, MackayLineChart> implements CentralMvpView {

    LinkedHashMap<MackayLabelData, Boolean> showLabelArray = new LinkedHashMap<>();
    ArrayList<Boolean> showTypeArray = new ArrayList<>();
    ShowMode currentMode = new ShowMode();
    public class ShowMode {
        public static final byte LABEL = 0;
        public static final byte TYPE = 1;
        public byte currentMode = LABEL;
        public ShowMode() {}
    }
    public static class MultipleSelectorMode {
        public static final byte SHOW_LABEL = 0;
        public static final byte SHOW_TYPE = 1;
        public static final byte DELETE_LABEL = 2;
        public static final byte DELETE_TYPE = 3;
    }
    public void deleteSelectedData(ArrayList<Boolean> bs) {
        ((MackayManagerData) mCentralPresenter.getCentralDataManager()).deleteSelectedData(bs);
        showLabelArray.clear();
    }
    public void deleteSelectedType(ArrayList<Boolean> bs) {
        ((MackayManagerData) mCentralPresenter.getCentralDataManager()).deleteSelectedType(bs);
        showLabelArray.clear();
    }

    Button buttonZoomOut;
    Button buttonDataMonitorSelector;

    public boolean HaveDataMonitorSelector() {
        return true;
    }

    @Override
    public void setDataMonitorAdapter() {
        dataMonitorAdapter = new MackayDataMonitorAdapter();
    }
    @Override
    public int getLayoutId() {
        return R.layout.central_mackay;
    }
    public View initView(View view) {
        view = super.initView(view);

        if(HaveDataMonitorSelector()) {
            buttonDataMonitorSelector = view.findViewById(R.id.DataMonitorSelector);
            buttonDataMonitorSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getDataMonitorManager().show();
                }
            });
        }
        buttonZoomOut = view.findViewById(R.id.ZoomOut_Button);
        buttonZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeHighlight();
                lineChart.zoomOut();
            }
        });
        return view;
    }

    @Override
    public ArrayList<ILineDataSet> setLineDataSetsForChart() {
        ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
        showLabelArray.clear();
        for (MackayLabelData centralLabelData : getDataSource()) {
            iLineDataSets.add(new LineDataSet(centralLabelData.getSpecialEntries(), centralLabelData.labelName));
            showLabelArray.put(centralLabelData, true);
//            Log.d("iLineDataSets: " + String.valueOf(iLineDataSets.size()));
//            Log.d("showLabelArray: " + String.valueOf(showLabelArray.size()));
        }
        return iLineDataSets;
    }
    public ArrayList<MackayLabelData> getDataSource() {
        return ((MackayManagerData) (mCentralPresenter.getCentralDataManager())).getAllLabelData();
    }

    @Override
    public ArrayList<Boolean> setShowArrayForChart() {
        ArrayList<Boolean> showArray = new ArrayList<>();
        switch (currentMode.currentMode) {
            case ShowMode.LABEL:
                for (Boolean b:showLabelArray.values()) {
                    showArray.add(b.booleanValue());
                }
            case ShowMode.TYPE:
                for (MackayLabelData mackayLabelData:showLabelArray.keySet()) {
                    try {
                        showArray.add(showTypeArray.get(mackayLabelData.type));
                    } catch (Exception e) {}
                }
        }
        return showArray;
    }

    public ArrayList<Boolean> superSetShowArrayForChart() {
        return super.setShowArrayForChart();
    }

    public void setAllShowArray(ArrayList<Boolean> booleans) {
        try {
            int index = 0;
            for (Map.Entry<MackayLabelData, Boolean> entry:showLabelArray.entrySet()) {
                entry.setValue(booleans.get(index));
                index++;
            }
        } catch (Exception e) {}
    }
    public void setTypeShowArray(ArrayList<Boolean> booleans) {
        showTypeArray = booleans;
    }

    @Override
    public void addDataIntoMonitor() {
        int index = 0;
        for (Map.Entry<MackayLabelData, Boolean> l : showLabelArray.entrySet()) {
            boolean isShow = false;
            switch (currentMode.currentMode) {
                case ShowMode.LABEL:
                    if(l.getValue().booleanValue()) {
                        isShow = true;
                    }
                case ShowMode.TYPE:
                    try {
                        if(showTypeArray.get(l.getKey().type)) {
                            isShow = true;
                        }
                    } catch (Exception e) {}
            }
            if(isShow) {
                if(getDataMonitorAdapter() == null) {
                    continue;
                }
                getDataMonitorAdapter().addData(
                        l.getKey(),
                        OtherUsefulFunction.getDataColor(BasicResourceManager.getResources(), index, showLabelArray.size(), 0.5f)
                );
            }
            index++;
        }
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
            case MultipleSelectorMode.SHOW_LABEL:
            case MultipleSelectorMode.DELETE_LABEL:
                selectionArrayBuffer = new boolean[showLabelArray.size()];
                labelNameArrayBuffer = ((MackayManagerData) mCentralPresenter.getCentralDataManager()).getAllLabelNameArray().toArray(new String[0]);
                break;
            case MultipleSelectorMode.SHOW_TYPE:
            case MultipleSelectorMode.DELETE_TYPE:
                selectionArrayBuffer = new boolean[MackayLabelData.getDataTypes().length];
                labelNameArrayBuffer = MackayLabelData.getDataTypes();
                break;
            default:
                labelNameArrayBuffer = new String[0];
        }
        if(selectionArrayBuffer.equals(null)) {
            return null;
        }
        for (int i=0; i<selectionArrayBuffer.length; i++) {
            switch(type) {
                case MultipleSelectorMode.SHOW_LABEL:
                    Set<MackayLabelData> keySet = showLabelArray.keySet();
                    List<MackayLabelData> listKeys = new ArrayList<>(keySet);
                    selectionArrayBuffer[i] = showLabelArray.get(listKeys.get(i)).booleanValue();
                    break;
                case MultipleSelectorMode.SHOW_TYPE:
                    selectionArrayBuffer[i] = true;
                    break;
                case MultipleSelectorMode.DELETE_LABEL:
                case MultipleSelectorMode.DELETE_TYPE:
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
                            case MultipleSelectorMode.SHOW_LABEL:
                            case MultipleSelectorMode.DELETE_LABEL:
                                for (int i = 0; i<showLabelArray.size(); i++) {
                                    Set<MackayLabelData> keySet = showLabelArray.keySet();
                                    List<MackayLabelData> listKeys = new ArrayList<>(keySet);
                                    selectionArrayBuffer[i] = showLabelArray.get(listKeys.get(i)).booleanValue();
                                    dialog.getListView().setItemChecked(i, selectionArrayBuffer[i]);
                                }
                                break;
                            case MultipleSelectorMode.SHOW_TYPE:
                            case MultipleSelectorMode.DELETE_TYPE:
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
                            case MultipleSelectorMode.SHOW_LABEL:
                                setAllShowArray(bs);
                                currentMode.currentMode = MultipleSelectorMode.SHOW_LABEL;
                                break;
                            case MultipleSelectorMode.SHOW_TYPE:
                                setTypeShowArray(bs);
                                currentMode.currentMode = MultipleSelectorMode.SHOW_TYPE;
                                break;
                            case MultipleSelectorMode.DELETE_LABEL:
                                deleteSelectedData(bs);
                                break;
                            case MultipleSelectorMode.DELETE_TYPE:
                                deleteSelectedType(bs);
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
