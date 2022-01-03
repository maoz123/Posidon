package com.demo.posidon.server.exception;

public class SegmentNotReadyException extends RuntimeException {
    public SegmentNotReadyException(String message){
        super(message);
    }
}
