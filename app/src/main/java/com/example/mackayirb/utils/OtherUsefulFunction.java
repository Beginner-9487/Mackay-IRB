package com.example.mackayirb.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentManager;

import com.example.mackayirb.fragment.PermissionAgreeFragment;
import com.github.mikephil.charting.data.Entry;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class OtherUsefulFunction {

    /**
     * Check that all required permissions have been granted.
     * <p></p>
     * {@link PermissionAgreeFragment}
     */
    public static boolean checkPermissionList(Activity activity, int levelOfRequired, @NonNull String Message, @NonNull String[] PermissionList, int RequestCode, @NonNull FragmentManager manager, @Nullable String tag) {

        boolean b = true;

        for (String permission : PermissionList) {
//            Log.d(permission + ":" + String.valueOf(ContextCompat.checkSelfPermission(activity, permission)));
//            Toast.makeText(activity, permission + ":" + String.valueOf(ContextCompat.checkSelfPermission(activity, permission)), Toast.LENGTH_SHORT).show();
            boolean bluetoothPermissions = permission.equals(Manifest.permission.BLUETOOTH_SCAN) || permission.equals(Manifest.permission.BLUETOOTH_CONNECT);
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
//                Log.d(permission);
//                Toast.makeText(activity, permission, Toast.LENGTH_SHORT).show();
                if (!(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) && bluetoothPermissions)) {
                    b = false;
                    break;
                }
            } else if(bluetoothPermissions) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, BasicResourceManager.Permissions.REQUEST_BLUETOOTH_CODE);
            }
        }

        if (!b) {
            if (levelOfRequired > 0) {
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
     * Convert Byte Array to Unsigned Integer
     * <p></p>
     * {@link Character#digit}
     */
    public static long byteArrayToUnsignedInt(byte[] bytes) {
        long value = 0;
        for (int i = 0; i < bytes.length; i++) {
            value |= (long) (bytes[bytes.length - i - 1] & 0xff) << (8 * i);
        }
        return value;
    }
    /**
     * Convert Byte Array to Integer
     * <p></p>
     * {@link Character#digit}
     */
    public static long byteArrayToSignedInt(byte[] bytes) {
        long value = byteArrayToUnsignedInt(bytes);
        // If the most significant bit of the final byte is set, the value is negative.
        if ((bytes[0] & 0x80) != 0) {
            // Extend the sign bit to fill the entire long value.
            value |= (-1L << (8 * bytes.length));
        }
        return value;
    }

    /**
     * Split the byte into two hexadecimal values
     */
    public static byte[] byteSplitter(byte value) {
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
    public static int getDataColor(Resources resources, int dataIndex, int dataSize, float contrastRatio) {
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
     * {@link #getDataColor(Resources resources, int 0, int 1, float 1.0f)}
     * <br>
     * H = 0.0f
     * <br>
     * L = 1.0f || 0.0f
     */
    public static int getBWColor(Resources resources) {
        return getDataColor(resources, 0, 1, 1.0f);
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

    public static <T> T[] concatWithArrayCopy(T[] ...arrays) {
        T[] result = (T[]) new Object[0];
        int initLength;
        for (T[] array:arrays) {
            initLength = result.length;
            result = Arrays.copyOf(result, result.length + array.length);
            System.arraycopy(array, 0, result, initLength, array.length);
        }
        return result;
    }
    public static boolean[] concatWithArrayCopy(boolean[] ...arrays) {
        boolean[] result = new boolean[0];
        int initLength;
        for (boolean[] array:arrays) {
            initLength = result.length;
            result = Arrays.copyOf(result, result.length + array.length);
            System.arraycopy(array, 0, result, initLength, array.length);
        }
        return result;
    }
    public static byte[] concatWithArrayCopy(byte[] ...arrays) {
        byte[] result = new byte[0];
        int initLength;
        for (byte[] array:arrays) {
            initLength = result.length;
            result = Arrays.copyOf(result, result.length + array.length);
            System.arraycopy(array, 0, result, initLength, array.length);
        }
        return result;
    }
    public static int[] concatWithArrayCopy(int[] ...arrays) {
        int[] result = new int[0];
        int initLength;
        for (int[] array:arrays) {
            initLength = result.length;
            result = Arrays.copyOf(result, result.length + array.length);
            System.arraycopy(array, 0, result, initLength, array.length);
        }
        return result;
    }
    public static float[] concatWithArrayCopy(float[] ...arrays) {
        float[] result = new float[0];
        int initLength;
        for (float[] array:arrays) {
            initLength = result.length;
            result = Arrays.copyOf(result, result.length + array.length);
            System.arraycopy(array, 0, result, initLength, array.length);
        }
        return result;
    }
    public static double[] concatWithArrayCopy(double[] ...arrays) {
        double[] result = new double[0];
        int initLength;
        for (double[] array:arrays) {
            initLength = result.length;
            result = Arrays.copyOf(result, result.length + array.length);
            System.arraycopy(array, 0, result, initLength, array.length);
        }
        return result;
    }

    public static boolean contains(byte[] array, byte value) {
        for (byte elem:array) {
            if(elem == value) {
                return true;
            }
        }
        return false;
    }

    public static float getMinOf(float[] array) {
        float min = Float.MAX_VALUE;
        for (float a: array) {
            if(a < min) {
                min = a;
            }
        }
        return min;
    }
    public static float getMaxOf(float[] array) {
        float max = 0;
        for (float a: array) {
            if(a > max) {
                max = a;
            }
        }
        return max;
    }

    public static float addDegree(float target, float degree) {
        return (float) (target + Math.toRadians(degree));
    }
    public static float xAtoB(float x, float length, float direction) {
        return x + length * (float) Math.cos(direction);
    }
    public static float yAtoB(float y, float length, float direction) {
        return y + length * (float) Math.sin(direction);
    }
    public static Entry getP2Entry(Entry p1, float length, float direction) {
        return new Entry(
                xAtoB(p1.getX(), length, direction),
                yAtoB(p1.getY(), length, direction)
        );
    }
    public static Entry getMirrorEntry(Entry m1, Entry m2, Entry source) {
        float[] abc = OtherUsefulFunction.getABCByTwoPoint(m1, m2);
        float a = abc[0];
        float b = abc[1];
        float c = abc[2];
        float temp = -2 * (a * source.getX() + b * source.getY() + c) /
                (a * a + b * b);
        float x = temp * a + source.getX();
        float y = temp * b + source.getY();
//        Log.d(String.valueOf(source.getX()) + "," + String.valueOf(source.getY()) + ";" + String.valueOf(x) + "," + String.valueOf(y));
        return new Entry(x, y);
    }
    public static float[] getABCByTwoPoint(Entry p1, Entry p2) {
        return new float[]{
                (p1.getY() - p2.getY()),
                -(p1.getX() - p2.getX()),
                (p1.getX() * p2.getY()) - (p2.getX() * p1.getY())
        };
    }

    public static byte[] reverseArray(byte[] array) {
        for(int i = 0; i < array.length / 2; i++) {
            // Swapping the elements
            byte j = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = j;
        }
        return array;
    }

    public static class ByteIterator {
        int pos = 0;
        byte[] data;
        public ByteIterator(byte[] array) {
            this.data = array;
        }
        public byte next() {
            return data[pos++];
        }
        public int index() {
            return pos;
        }
        public byte[] array(boolean reverse, int length) {
            byte[] values = new byte[length];
            for (int i=0; i<length; i++) {
                values[i] = next();
            }
            return (reverse) ? reverseArray(values) : values;
        }
    }

    public static byte getRandomByte() {
        return (byte) ((Math.random() - 0.5f) * 254f);
    }
    public static byte[] getRandomByteArray(int length) {
        byte[] array = new byte[length];
        for (int i=0; i<length; i++) {
            array[i] = getRandomByte();
        }
        return array;
    }
    public static byte[] getSignedIntToByteArray(boolean reverse, int integer, int capacity) {
        byte[] array = new byte[capacity];
        int index = 0;
        byte[] buffer = ByteBuffer.allocate(4).putInt(integer).array();
        buffer = (reverse) ? reverseArray(buffer) : buffer;
        for (byte b:buffer) {
            if(reverse) {
                if(index < capacity) {
                    array[index] = b;
                }
            } else {
                if((index + capacity) >= 4) {
                    array[((index + capacity) - 4)] = b;
                }
            }
            index++;
        }
        return array;
    }
    public static byte[] getSignedIntToByteArray(boolean reverse, int integer, int capacity, int length) {
        byte[] array = new byte[length*capacity];
        for (int i=0; i<length; i++) {
            int index = 0;
            for (byte b:getSignedIntToByteArray(reverse, integer, capacity)) {
                array[(i*capacity) + index] = b;
                index++;
            }
        }
        return array;
    }
    public static byte[] getSignedIntSequenceToByteArray(boolean reverse, int min, int step, int capacity, int length) {
        byte[] array = new byte[length*capacity];
        for (int i=0; i<length; i++) {
            int index = 0;
            for (byte b:getSignedIntToByteArray(reverse, min + (step * i), capacity)) {
                array[(i*capacity) + index] = b;
                index++;
            }
        }
        return array;
    }

}
