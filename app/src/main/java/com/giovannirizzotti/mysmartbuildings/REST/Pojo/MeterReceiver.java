package com.giovannirizzotti.mysmartbuildings.REST.Pojo;

import java.util.ArrayList;
import java.util.List;

public class MeterReceiver {
    private List<Meter> output = new ArrayList<Meter>();
    public List<Meter> getOutput() {
        return output;
    }
    public void setOutput(List<Meter> output) {
        this.output = output;
    }
}