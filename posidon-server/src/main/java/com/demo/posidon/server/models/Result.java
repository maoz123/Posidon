package com.demo.posidon.server.models;

import java.time.OffsetDateTime;

public class Result {

    public Result(int id, String serviceName, String dateTime){
        this.id = id;
        this.serviceName = serviceName;
        this.dateTime = dateTime;
    }

    private int id;

    private String serviceName;

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    private String dateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
