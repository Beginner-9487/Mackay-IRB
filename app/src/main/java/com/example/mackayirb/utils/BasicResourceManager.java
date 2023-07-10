package com.example.mackayirb.utils;

import static com.example.mackayirb.utils.BasicResourceManager.SharedPreferencesManager.sharedPreference;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.mackayirb.R;

import java.util.ArrayList;
import java.util.Arrays;

public class BasicResourceManager {

    public static boolean isTesting = false;

    // TODO Init
    static Resources resources;
    public static void setBasic(Resources mResources, SharedPreferences mSharedPreference) {
        resources = mResources;
        sharedPreference = mSharedPreference;
    }
    public static Resources getResources() {
        return resources;
    }
    public static SharedPreferences getSharedPreference() {
        return sharedPreference;
    }

    static Activity currentActivity;
    static Fragment currentFragment;
    public static void setCurrentActivity(Activity activity) {
        currentActivity = activity;
    }
    public static Activity getCurrentActivity() {
        return currentActivity;
    }
    public static void removeActivity(Activity activity) {
        if (activity.equals(currentActivity)) {
            currentActivity = null;
        }
    }
    public static void setCurrentFragment(Fragment activity) {
        currentFragment = activity;
    }
    public static Fragment getCurrentFragment() {
        return currentFragment;
    }
    public static void removeCurrentFragment() {
        currentFragment = null;
    }

    // TODO SharedPreferences
    public static class SharedPreferencesManager {
        static SharedPreferences sharedPreference;
        public static final String ElementRegex = "\n";
        public static final String AllTargetDevices = "0";
        public static final String ModeController = "1";

        public static void UpdateSharedPreferencesString(String target, String Values) {
            SharedPreferences.Editor editor = sharedPreference.edit();
            editor.putString(target, Values);
            editor.commit();
        }
        public static void UpdateSharedPreferencesInt(String target, int Values) {
            SharedPreferences.Editor editor = sharedPreference.edit();
            editor.putInt(target, Values);
            editor.commit();
        }

        // =================================================================================
        // TODO Advanced SharedPreferences

        // TODO AllTargetDevices
        public static String[] getAllTargetDevices() {
            return sharedPreference.getString(AllTargetDevices, "").split(ElementRegex);
        }
        public static void addDeviceToAllTargetDevices(String deviceAddress) {
            String newDevicesList = "";
            boolean lock = false;
            for (String address:getAllTargetDevices()) {
                if(address.equals(deviceAddress)) {
                    return;
                } else if (!address.equals("")) {
                    newDevicesList += ((lock)?ElementRegex:"") + address;
                    lock = true;
                }
            }
            UpdateSharedPreferencesString(AllTargetDevices, newDevicesList + ((lock)?ElementRegex:"") + deviceAddress);
        }
        public static void deleteDeviceToAllTargetDevices(String deviceAddress) {
            String newDevicesList = "";
            boolean lock = false;
            for (String address:getAllTargetDevices()) {
                if(!address.equals(deviceAddress)) {
                    newDevicesList += ((lock)?ElementRegex:"") + address;
                    lock = true;
                }
            }
            UpdateSharedPreferencesString(AllTargetDevices, newDevicesList);
        }

        public static final int MackayClientMode = 0;
        public static final int MackayDeveloperMode = 1;
        public static final int FootDeveloperMode = 2;
        // TODO ModeController
        public static int getModeController() {
            switch (sharedPreference.getInt(ModeController, MackayClientMode)) {
                case MackayClientMode:
                case MackayDeveloperMode:
                case FootDeveloperMode:
                    break;
                default:
                    return MackayDeveloperMode;
            }
            return sharedPreference.getInt(ModeController, MackayClientMode);
        }
        public static void setModeController(int value) {
            UpdateSharedPreferencesInt(ModeController, value);
        }
    }

    // TODO Intents
    public static class BLEIntents {
        public static final String ACTION_CENTRAL_MODE = "example.mackayirb.action.central.MAIN";
    }

    // TODO Permissions
    public static class Permissions {

        public static boolean checkPermissions(String title, String[] permissions, int code) {
            return OtherUsefulFunction.checkPermissionList(
                    getCurrentFragment().getActivity(),
                    2,
                    title,
                    permissions,
                    code,
                    getCurrentFragment().getActivity().getSupportFragmentManager(),
                    getCurrentFragment().getResources().getString(R.string.BluetoothPermissionTag)
            );
        }

        public static final int REQUEST_BLUETOOTH_CODE = 1;
        public static final String[] BLUETOOTH_PERMISSIONS = new String[]{
                Manifest.permission.BLUETOOTH_SCAN,
//                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
//                Manifest.permission.BLUETOOTH_PRIVILEGED
        };
        /**
         * Check Bluetooth Permissions have been granted.
         * <p></p>
         * {@link OtherUsefulFunction#checkPermissionList(Activity, int, String, String[], int, FragmentManager, String)}
         */
        public static boolean checkBluetoothPermissions() {
            return checkPermissions(
                    getCurrentFragment().getResources().getString(R.string.BluetoothPermissionAgreeFragment),
                    BLUETOOTH_PERMISSIONS,
                    REQUEST_BLUETOOTH_CODE
            );
        }

        public static final int REQUEST_LOCATION_CODE = 10;
        public static final String[] LOCATION_PERMISSIONS = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        };
        /**
         * Check Location Permissions have been granted.
         * <p></p>
         * {@link OtherUsefulFunction#checkPermissionList(Activity, int, String, String[], int, FragmentManager, String)}
         */
        public static boolean checkLocationPermissions() {
            return checkPermissions(
                    getCurrentFragment().getResources().getString(R.string.LocationPermissionAgreeFragment),
                    LOCATION_PERMISSIONS,
                    REQUEST_LOCATION_CODE
            );
        }

        public static final int REQUEST_EXTERNAL_STORAGE_CODE = 0;
        public static final String[] STORAGE_PERMISSIONS = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        /**
         * Check External Storage Permissions have been granted.
         * <p></p>
         * {@link OtherUsefulFunction#checkPermissionList(Activity, int, String, String[], int, FragmentManager, String)}
         */
        public static boolean checkExternalStoragePermissions() {
            return checkPermissions(
                    getCurrentFragment().getResources().getString(R.string.ExternalStoragePermissionAgreeFragment),
                    STORAGE_PERMISSIONS,
                    REQUEST_EXTERNAL_STORAGE_CODE
            );
        }

        public static boolean checkAllPermissions() {
            if(isTesting) { return true; }

            ArrayList<String> list = new ArrayList<>(Arrays.asList(BLUETOOTH_PERMISSIONS));
            list.addAll(new ArrayList<>(Arrays.asList(LOCATION_PERMISSIONS)));
            list.addAll(new ArrayList<>(Arrays.asList(STORAGE_PERMISSIONS)));
//            Log.d(String.valueOf(list.size()));
            return checkPermissions(
                    getCurrentFragment().getResources().getString(R.string.AllPermissionsAgreeFragment),
                    (String[]) list.toArray(new String[list.size()]),
                    REQUEST_EXTERNAL_STORAGE_CODE
            );
        }

    }
}
