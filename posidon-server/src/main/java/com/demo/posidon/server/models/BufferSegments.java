package com.demo.posidon.server.models;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BufferSegments {
    public BufferSegments(){
        segments[0] = new Segment(this);
        segments[1] = new Segment(this);
    }

    private volatile AtomicBoolean isUpdating = new AtomicBoolean(false);
    private volatile boolean isNextSegmentReady = false;
    private volatile boolean isReady = false;

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public void setNextSegmentReady(boolean nextSegmentReady) {
        isNextSegmentReady = nextSegmentReady;
    }

    public boolean isNextSegmentReady() {
        return isNextSegmentReady;
    }

    private volatile int currentPosition = 0;

    private Segment[] segments = new Segment[2];

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public AtomicBoolean getIsUpdating() {
        return isUpdating;
    }

    public ReentrantReadWriteLock getLock() {
        return lock;
    }

    public Segment getCurrentSegment(){
        return segments[currentPosition];
    }

    public Segment getNextSegment(){
        return segments[(currentPosition+1)%2];
    }

    public void switchPosition(){
        currentPosition = (currentPosition+1)%2;
    }
}
