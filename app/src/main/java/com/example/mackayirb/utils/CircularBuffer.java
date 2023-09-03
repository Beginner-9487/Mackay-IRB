package com.example.mackayirb.utils;

import java.util.ArrayList;
import java.util.List;

public class CircularBuffer {
    private final List<byte[]> buffer;
    private final int bufferSize;
    private int head;
    private int tail;
    private boolean isFull;

    public CircularBuffer(int bufferSize) {
        this.bufferSize = bufferSize;
        this.buffer = new ArrayList<>(bufferSize);
        for (int i = 0; i < bufferSize; i++) {
            buffer.add(null);
        }
        this.head = 0;
        this.tail = 0;
        this.isFull = false;
    }

    public synchronized void addData(byte[] data) {
        buffer.set(head, data);
        head = (head + 1) % bufferSize;
        if (head == tail) {
            isFull = true;
            tail = (tail + 1) % bufferSize;
        }
    }

    public synchronized byte[] getData() {
        if (!isFull && head == tail) {
            return null; // No data available in the buffer
        }
        return buffer.get(tail);
    }

    public synchronized byte[] popData() {
        if (!isFull && head == tail) {
            return null; // No data available in the buffer
        }
        byte[] data = buffer.get(tail);
        buffer.set(tail, null);
        tail = (tail + 1) % bufferSize;
        isFull = false;
        return data;
    }

    public synchronized int getHead() {
        return head;
    }
}