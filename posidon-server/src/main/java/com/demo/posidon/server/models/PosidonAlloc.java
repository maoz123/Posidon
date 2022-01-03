package com.demo.posidon.server.models;

public class PosidonAlloc {

    private String serviceName;

    private int maxValue;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int max_value) {
        this.maxValue = max_value;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    private int step;

    private String description;

    private String updateTime;
}
