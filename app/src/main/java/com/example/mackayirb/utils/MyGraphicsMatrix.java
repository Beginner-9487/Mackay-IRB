package com.example.mackayirb.utils;

import android.graphics.Matrix;

public class MyGraphicsMatrix extends Matrix {

    public float[] getArray() {
        float[] values = new float[9];
        super.getValues(values);
//        Log.d(String.valueOf(values.length));
//        Log.d(toShortString());
//        String string = "";
//        for (float f:values) {
//            string += String.valueOf(f) + ", ";
//        }
//        Log.d(string);
        return values;
    }

    public float[][] get2DArray() {
        float[] values = getArray();
//        Log.d(toShortString());
        float[][] values2D = new float[3][3];
        for(int i=0; i<3; i++) {
            values2D[i] = new float[]{values[0+(i*3)], values[1+(i*3)], values[2+(i*3)]};
//            Log.d(values2D[i][0] + "," + values2D[i][1] + "," + values2D[i][2]);
        }
        return values2D;
    }

    public float[][] getInvert(float[][] matrix) {
        float determinant = getDeterminant(matrix);
        if (determinant == 0) {
//            throw new IllegalArgumentException("Matrix is not invertible");
            Log.d("Matrix is not invertible");
            return get2DArray();
        }

        float[][] adjugate = getAdjugateMatrix(matrix);

        float[][] inverseMatrix = new float[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                inverseMatrix[i][j] = adjugate[i][j] / determinant;
            }
        }

        return inverseMatrix;
    }

    private static float getDeterminant(float[][] matrix) {
        float a = matrix[0][0];
        float b = matrix[0][1];
        float c = matrix[0][2];
        float d = matrix[1][0];
        float e = matrix[1][1];
        float f = matrix[1][2];
        float g = matrix[2][0];
        float h = matrix[2][1];
        float i = matrix[2][2];

//        Log.d(a + "," + b + "," + c + "," + d + "," + e + "," + f + "," + g + "," + h + "," + i );
        return a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g);
    }

    private static float[][] getAdjugateMatrix(float[][] matrix) {
        float a = matrix[0][0];
        float b = matrix[0][1];
        float c = matrix[0][2];
        float d = matrix[1][0];
        float e = matrix[1][1];
        float f = matrix[1][2];
        float g = matrix[2][0];
        float h = matrix[2][1];
        float i = matrix[2][2];

        float[][] adjugate = new float[matrix.length][matrix[0].length];
        adjugate[0][0] = e * i - f * h;
        adjugate[0][1] = -(b * i - c * h);
        adjugate[0][2] = b * f - c * e;
        adjugate[1][0] = -(d * i - f * g);
        adjugate[1][1] = a * i - c * g;
        adjugate[1][2] = -(a * f - c * d);
        adjugate[2][0] = d * h - e * g;
        adjugate[2][1] = -(a * h - b * g);
        adjugate[2][2] = a * e - b * d;

        return adjugate;
    }

    public float getXY(boolean invert, int type, float x, float y) {
        float[][] values = (invert) ? getInvert(get2DArray()) : get2DArray();
        return x * values[type][0] + y * values[type][1] + 1 * values[type][2];
    }

    public float getX(float x, float y) {
        return getXY(false, 0, x, y);
    }

    public float getY(float x, float y) {
        return getXY(false, 1, x, y);
    }

    public float getInvertX(float x, float y) {
        return getXY(true, 0, x, y);
    }

    public float getInvertY(float x, float y) {
        return getXY(true, 1, x, y);
    }
}
