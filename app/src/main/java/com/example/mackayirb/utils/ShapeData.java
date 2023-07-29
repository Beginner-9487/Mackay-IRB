package com.example.mackayirb.utils;

public class ShapeData {

    public static final byte ARROW = 0;
    public static final byte BORDERED_ARROW = 1;
    public static final byte CIRCLE = 2;
    public static final byte BORDERED_CIRCLE = 3;
    public static final byte CROSS = 4;

    private int shapeType;
    private float x;
    private float y;
    private float intensity;
    private float direction;

    public ShapeData(byte shapeType, float x, float y, float intensity, float direction) {
        this.shapeType = shapeType;
        this.x = x;
        this.y = y;
        this.intensity = intensity;
        this.direction = direction;
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
    public float getDirection() {
        return direction;
    }
    public void setDirection(float direction) {
        this.direction = direction;
    }
}
