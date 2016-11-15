package com.giovannirizzotti.mysmartbuildings.REST.Pojo;

public class Meter {
    private String value;
    private String meterName;

    public Meter(String meterName, String value) {
        this.meterName = meterName;
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getMeterName() {
        return meterName;
    }
    public void setMeterName(String meterName) {
        this.meterName = meterName;
    }
}
