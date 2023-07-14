package com.example.mackayirb.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.mackayirb.R;
import com.example.mackayirb.fragment.PermissionAgreeFragment;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Arrays;

public class OtherUsefulFunction {

    /**
     * Check that all required permissions have been granted.
     * <p></p>
     * {@link PermissionAgreeFragment}
     */
    public static boolean checkPermissionList(Activity activity, int levelOfRequired, @NonNull String Message, @NonNull String[] PermissionList, int RequestCode, @NonNull FragmentManager manager, @Nullable String tag) {

        boolean b = true;
        boolean Rationale_lock = false;

        for (String permission:PermissionList) {
            Log.d(permission + ":" + String.valueOf(ContextCompat.checkSelfPermission(activity, permission)));
//            Toast.makeText(activity, permission + ":" + String.valueOf(ContextCompat.checkSelfPermission(activity, permission)), Toast.LENGTH_SHORT).show();
            if(ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
//                Log.d(permission);
//                Toast.makeText(activity, permission, Toast.LENGTH_SHORT).show();
                b = false;
                if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Rationale_lock = true;
                }
                break;
            }
        }

        if(!b) {
            if (levelOfRequired > 0 || Rationale_lock) {
                PermissionAgreeFragment dialog = new PermissionAgreeFragment(
                        Message,
                        PermissionList,
                        RequestCode,
                        (levelOfRequired > 1) ? false : true
                );
                dialog.show(manager, tag);
            }
        }

        return b;

    }

    /**
     * Convert Alphanumeric String to Byte Array
     * <p></p>
     * {@link Character#digit}
     */
    public static byte[] hexStringToByteArray(String s) {

        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;

    }

    /**
     * Convert Byte Array to Alphanumeric String
     * <p></p>
     * {@link Integer#toHexString}
     */
    public static String byteArrayToHexString(byte[] bytes, String split_regex, @NonNull boolean isSigned, String positive_sign, String negative_sign) {

        if(bytes == null) bytes = new byte[]{};
        if(split_regex == null) split_regex = "";
        if(positive_sign == null) positive_sign = "";
        if(negative_sign == null) negative_sign = "";

        String s = "";
        for (byte b:bytes) {
            if(isSigned) {
                s += ( (b<0) ? negative_sign : positive_sign ) + (( b<0x10 && b>=0 ) ? "0" : "" ) + Integer.toHexString( (b<0) ? b&0xff : b ) + split_regex;
            } else {
                s += (((b&0xff)<0x10)?"0":"") + Integer.toHexString(b&0xff) + split_regex;
            }
        }
        return s;

    }
    /**
     * {@link #byteArrayToHexString(byte[], String, boolean, String, String)}
     * <p>
     * isSigned == false;
     */
    public static String byteArrayToHexString(byte[] bytes, String split_regex) {
        return byteArrayToHexString(bytes, split_regex, false, null, null);
    }
    /**
     * {@link #byteArrayToHexString(byte[], String, boolean, String, String)}
     * <p>
     * isSigned == true;
     */
    public static String byteArrayToHexString(byte[] bytes, String split_regex, String positive_sign, String negative_sign) {
        return byteArrayToHexString(bytes, split_regex, true, positive_sign, negative_sign);
    }

    /**
     * Convert Byte Array to Integer
     * <p></p>
     * {@link Character#digit}
     */
    public static int byteArrayToSignedInt(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value |= (long) (bytes[bytes.length - i - 1] & 0xff) << (8 * i);
        }
        // If the most significant bit of the final byte is set, the value is negative.
        if ((bytes[0] & 0x80) != 0) {
            // Extend the sign bit to fill the entire long value.
            value |= (-1L << (8 * bytes.length));
        }
        return (int) value;
    }

    /**
     * Split the byte into two hexadecimal values
     */
    public static byte[] ByteSplitter(byte value) {
        byte highNibble = (byte) ((value & 0xF0) >> 4);
        byte lowNibble = (byte) (value & 0x0F);
        return new byte[]{highNibble, lowNibble};
    }

    /**
     * Get the color based on the data size, make sure each data has a different color.
     * <p></p>
     * {@link ColorUtils#HSLToColor}
     * <p>
     * {@link Configuration#uiMode}
     * <p>
     * {@link Configuration#UI_MODE_NIGHT_YES}
     */
    public static int GetDataColor(Resources resources, int dataIndex, int dataSize, float contrastRatio) {
        return ColorUtils.HSLToColor(
                new float[]{
                        (255.0f * dataIndex/dataSize),
                        1.0f,
                        0.5f + contrastRatio * 0.5f * (((resources.getConfiguration().uiMode & 48) == Configuration.UI_MODE_NIGHT_YES)?1.0f:-1.0f)
                });
    }
    /**
     * Get black or white according to uiMode.
     * <p></p>
     * {@link #GetDataColor(Resources resources, int 0, int 1, float 1.0f)}
     * <br>
     * H = 0.0f
     * <br>
     * L = 1.0f || 0.0f
     */
    public static int GetBWColor(Resources resources) {
        return GetDataColor(resources, 0, 1, 1.0f);
    }

    private static AlertDialog.Builder getYNDialogBuilder(Context context, String Title, @Nullable String Message, DialogInterface.OnClickListener yesListener, @Nullable DialogInterface.OnClickListener noListener) {
        // Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(Title);
        builder.setMessage(Message);

        // set Listener
        final DialogInterface.OnClickListener YesListener = yesListener;
        final DialogInterface.OnClickListener NoListener = (noListener == null) ?
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                } :
                noListener;

        // null should be your on click listener
        builder.setPositiveButton(context.getResources().getString(android.R.string.ok), YesListener);
        builder.setNegativeButton(context.getResources().getString(android.R.string.cancel), NoListener);

        return builder;
    }
    /**
     * Simplest Yes/No Dialog
     */
    public static AlertDialog getYNDialog(Context context, String Title, @Nullable String Message, DialogInterface.OnClickListener yesListener, @Nullable DialogInterface.OnClickListener noListener) {
        return getYNDialogBuilder(context, Title, Message, yesListener, noListener).create();
    }
    /**
     * Simplest Yes/No Dialog
     */
    public static AlertDialog getYNDialogWithEditText(Context context, String Title, @Nullable String Message, DialogInterface.OnClickListener yesListener, @Nullable DialogInterface.OnClickListener noListener, EditText editText) {
        AlertDialog.Builder builder = getYNDialogBuilder(context, Title, Message, yesListener, noListener);
        builder.setView(editText);
        return builder.create();
    }

    public static double getTwoPointRadian(Entry center, Entry target) {
        return Math.atan(
            (target.getY()-center.getY()) / (target.getX()-center.getX())
        );
    }

    public static double calculateDistance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static <T> T[] concatWithArrayCopy(T[] array1, T[] array2) {
        T[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }
    public static boolean[] concatWithArrayCopy(boolean[] array1, boolean[] array2) {
        boolean[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }
    public static byte[] concatWithArrayCopy(byte[] array1, byte[] array2) {
        byte[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }
    public static int[] concatWithArrayCopy(int[] array1, int[] array2) {
        int[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }
    public static float[] concatWithArrayCopy(float[] array1, float[] array2) {
        float[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }
    public static double[] concatWithArrayCopy(double[] array1, double[] array2) {
        double[] result = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

}
