package com.example.mackayirb.utils;

public class ShapeData {

    public static final byte ARROW = 0;
    public static final byte CIRCLE = 1;
    public static final byte POINT = 2;

    private int shapeType;
    private float x;
    private float y;
    private float intensity;
    private float orientation;

    public ShapeData(byte shapeType, float x, float y, float intensity, float orientationRadian) {
        this.shapeType = shapeType;
        this.x = x;
        this.y = y;
        this.intensity = intensity;
        this.orientation = orientationRadian;
    }

    public int getShapeType() {
        return shapeType;
    }
    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }
    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }
    public float getIntensity() {
        return intensity;
    }
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    public float getOrientation() {
        return orientation;
    }
    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }
}
