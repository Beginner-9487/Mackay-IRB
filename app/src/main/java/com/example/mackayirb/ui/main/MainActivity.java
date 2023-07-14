package com.example.mackayirb.ui.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.mackayirb.ui.central.CentralChartForRPNFragment;
import com.example.mackayirb.ui.central.CentralChartFragment;
import com.example.mackayirb.ui.central.CentralFootFragment;
import com.example.mackayirb.ui.central.CentralFragment;
import com.example.mackayirb.ui.central.CentralPresenter;
import com.example.mackayirb.ui.central.CentralScanClientFragment;
import com.example.mackayirb.ui.central.CentralScanDeveloperFragment;
import com.example.mackayirb.ui.central.CentralTemp2UIFragment;
import com.example.mackayirb.ui.central.CentralTempUIFragment;
import com.example.mackayirb.ui.central.CustomViewPager;
import com.example.mackayirb.utils.BasicResourceManager;
import com.example.mackayirb.utils.Log;
import com.example.mackayirb.utils.OtherUsefulFunction;
import com.example.mackayirb.R;

import com.example.mackayirb.ui.base.BaseActivity;
import com.google.android.material.tabs.TabLayout;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.ArrayList;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements MainMvpView {

    @Inject
    BluetoothAdapter mBluetoothAdapter;

    public CustomViewPager mViewPager;

    public CustomViewPager getViewPager() {
        return mViewPager;
    }

    private Toolbar toolbar;
    private TabLayout tabLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate");

        getActivityComponent().inject(this);
        BasicResourceManager.setCurrentActivity(this);

        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.TabLayout);
        mViewPager = findViewById(R.id.ViewPager);

        switch (BasicResourceManager.SharedPreferencesManager.getModeController()) {
            case BasicResourceManager.SharedPreferencesManager.MackayClientMode:
                tabLayout.setVisibility(View.GONE);
                mViewPager.setPagingEnabled(false);
                break;
            case BasicResourceManager.SharedPreferencesManager.MackayDeveloperMode:
            case BasicResourceManager.SharedPreferencesManager.FootDeveloperMode:
                mViewPager.setPagingEnabled(true);
                break;
        }

        // Use this check to determine whether BLE is supported on the device.
        // Then you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Checks if Bluetooth is enabled
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (BasicResourceManager.Permissions.checkGroupPermissions(BasicResourceManager.Permissions.REQUEST_BLUETOOTH_CODE)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    return;
                }
                startActivityForResult(enableBtIntent, BasicResourceManager.Permissions.REQUEST_BLUETOOTH_CODE);
            }
            return;
        }
        setupPermissionAgreeFragment();
        setupToolbar();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        BasicResourceManager.setCurrentActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (BasicResourceManager.Permissions.checkGroupPermissions(BasicResourceManager.Permissions.REQUEST_BLUETOOTH_CODE)) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                        return;
                    }
                    startActivityForResult(enableBtIntent, BasicResourceManager.Permissions.REQUEST_BLUETOOTH_CODE);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == BasicResourceManager.Permissions.REQUEST_BLUETOOTH_CODE && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy");
        BasicResourceManager.removeActivity(this);
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setupPermissionAgreeFragment() {
        PermissionsFragment fragment = new PermissionsFragment();
        mViewPager.setAdapter(
                new MainViewpagerAdapter(
                        getSupportFragmentManager(),
                        new String[]{""},
                        new PermissionsFragment[]{fragment}
                )
        );
        fragment.messageSentListener = new PermissionsFragment.OnMessageSentListener() {
            @Override
            public void onMessageSent(String message) {
                if (BasicResourceManager.Permissions.checkAllPermissions()) {
                    setupViewPager();
                }
            }
        };
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check if all permissions are granted
        boolean allPermissionsGranted = true;
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        Log.d(String.valueOf(allPermissionsGranted));
        if (allPermissionsGranted) {
            // Permissions granted, you can proceed with your task
            // ...
            setupViewPager();
        } else {
            // Permissions denied, handle the situation accordingly
            // ...
            BasicResourceManager.Permissions.checkAllPermissions();
        }
    }

    public void setupViewPager() {

        MainViewpagerAdapter adapter;

        String[] titles = {};
        Fragment[] fragments;

        switch (BasicResourceManager.SharedPreferencesManager.getModeController()) {
            case BasicResourceManager.SharedPreferencesManager.MackayClientMode:
                titles = getResources().getStringArray(R.array.ViewPagerTitlesMackayClient);
                break;
            case BasicResourceManager.SharedPreferencesManager.MackayDeveloperMode:
                titles = getResources().getStringArray(R.array.ViewPagerTitlesMackayDeveloper);
                break;
            case BasicResourceManager.SharedPreferencesManager.FootDeveloperMode:
                titles = getResources().getStringArray(R.array.ViewPagerTitlesFootDeveloper);
                break;
        }

        fragments = new Fragment[titles.length];

        switch (BasicResourceManager.SharedPreferencesManager.getModeController()) {
            case BasicResourceManager.SharedPreferencesManager.MackayClientMode:
                fragments[0] = new CentralScanClientFragment();
                fragments[1] = new CentralChartForRPNFragment();
                break;
            case BasicResourceManager.SharedPreferencesManager.MackayDeveloperMode:
                fragments[0] = new CentralScanDeveloperFragment();
                fragments[1] = new CentralTempUIFragment();
                fragments[2] = new CentralChartFragment();
                fragments[3] = new CentralChartForRPNFragment();
                fragments[4] = new Fragment();
                break;
            case BasicResourceManager.SharedPreferencesManager.FootDeveloperMode:
                fragments[0] = new CentralScanDeveloperFragment();
                fragments[1] = new CentralTemp2UIFragment();
                fragments[2] = new CentralFootFragment();
                break;
        }

        adapter = new MainViewpagerAdapter(getSupportFragmentManager(), titles, fragments);

        Log.d("setAdapter");
        mViewPager.setAdapter(adapter);
    }

    public void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.AboutLibrary:
                        startAboutLibrary();
                        break;
                    case R.id.AboutDeviceList:
                        getMyMultipleSelector().show();
                        break;
                    case R.id.AboutDeveloperMode:
                        final EditText editText = new EditText(getApplicationContext());
                        OtherUsefulFunction.getYNDialogWithEditText(
                                MainActivity.this,
                                String.valueOf(BasicResourceManager.SharedPreferencesManager.getModeController()),
                                getResources().getString(R.string.ModeSwitchTogglingAlert),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            BasicResourceManager.SharedPreferencesManager.setModeController(Integer.parseInt(editText.getText().toString()));
                                            restartApp();
                                        } catch (Exception e) {}
                                    }
                                },
                                null,
                                editText
                        ).show();
                        break;
                    case R.id.CloseApp:
                        OtherUsefulFunction.getYNDialog(
                                MainActivity.this,
                                getResources().getString(R.string.CloseApp),
                                getResources().getString(R.string.AboutCloseApp),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            throw new Exception();
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                },
                                null
                        ).show();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void startAboutLibrary() {
        new LibsBuilder()
                .withActivityTheme(R.style.Theme_MackayIRB)
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription(getResources().getString(R.string.AboutDescription))
                .start(MainActivity.this);
    }

    private AlertDialog getMyMultipleSelector() {
        // Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // Log.d(String.join(", ", BasicResourceManager.SharedPreferencesManager.getAllTargetDevices()));
        String[] deviceList = BasicResourceManager.SharedPreferencesManager.getAllTargetDevices();
        boolean[] booleans = new boolean[deviceList.length];
        for (int i=0; i<deviceList.length; i++) {
            booleans[i] = false;
        }

        // Build
        final AlertDialog dialog = builder
                .setTitle(getResources().getString(R.string.DeleteSelectedDevices))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(android.R.string.ok), null)
                .setNegativeButton(getResources().getString(R.string.Fragment_SelectAll), null)
                .setMultiChoiceItems(deviceList, booleans, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                // =====================================================================================
                // MultiChoiceItems
                dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        booleans[i] = !booleans[i];
                        for (boolean b: booleans) {
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
                for (boolean b: booleans) {
                    if(b) {
                        buttonChangeAll.setText(getResources().getString(R.string.Fragment_ClearAll));
                        break;
                    }
                }
                buttonChangeAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean b = buttonChangeAll.getText().equals(getResources().getString(R.string.Fragment_SelectAll));
                        for (int i=0; i<booleans.length; i++) {
                            booleans[i] = b;
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
                        for (int i=0; i<deviceList.length; i++) {
                            if(booleans[i]) {
                                BasicResourceManager.SharedPreferencesManager.deleteDeviceToAllTargetDevices(deviceList[i]);
                                getCentralPresenter().getRemoteDevices();
                            }
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        return dialog;

    }

    public void changeToolbar(int page) {
        switch (page) {
            case 0:
                break;
        }
    }

    public void destroyAllFragments() {
        ((MainViewpagerAdapter) mViewPager.getAdapter()).clear();
    }
    public void restartApp() {
        destroyAllFragments();
        this.finish();
        this.startActivity(this.getIntent());
    }
    public void closeApp() {
        destroyAllFragments();
        MainActivity.super.onBackPressed();
        this.onDestroy();
    }

    public CentralPresenter getCentralPresenter() {
        return ((CentralFragment) ((MainViewpagerAdapter) mViewPager.getAdapter()).getItem(0)).getCentralPresenter();
    }

}
