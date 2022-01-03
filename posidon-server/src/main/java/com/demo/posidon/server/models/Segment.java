package com.demo.posidon.server.models;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Segment {
    public Segment(BufferSegments segments){
        this.bufferSegments = segments;
    }

    public BufferSegments getBufferSegments() {
        return bufferSegments;
    }

    public void setBufferSegments(BufferSegments bufferSegments) {
        this.bufferSegments = bufferSegments;
    }

    public AtomicBoolean getIsUpdating() {
        return isUpdating;
    }

    public void setIsUpdating(AtomicBoolean isUpdating) {
        this.isUpdating = isUpdating;
    }

    private BufferSegments bufferSegments;

    public AtomicInteger getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(AtomicInteger currentValue) {
        this.currentValue = currentValue;
    }

    public AtomicInteger getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(AtomicInteger maxValue) {
        this.maxValue = maxValue;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    private volatile AtomicInteger currentValue = new AtomicInteger(0);

    private volatile AtomicInteger maxValue = new AtomicInteger(0);

    private int step;

    public ReentrantReadWriteLock getLock() {
        return lock;
    }

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void setLock(ReentrantReadWriteLock lock) {
        this.lock = lock;
    }

    public AtomicBoolean getIsUpdated() {
        return isUpdating;
    }

    public void setIsUpdated(AtomicBoolean isUpdated) {
        this.isUpdating = isUpdated;
    }

    private volatile AtomicBoolean isUpdating = new AtomicBoolean(false);
}