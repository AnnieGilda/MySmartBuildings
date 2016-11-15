package com.giovannirizzotti.mysmartbuildings.REST.Pojo;

import java.util.ArrayList;
import java.util.List;

public class AllMetersReceiver {
    private List<Meter> water = new ArrayList<Meter>();
    private List<Meter> gas = new ArrayList<Meter>();
    private List<Meter> electricity = new ArrayList<Meter>();

    public List<Meter> getElectricity() {
        return electricity;
    }

    public void setElectricity(List<Meter> electricity) {
        this.electricity = electricity;
    }

    public List<Meter> getGas() {
        return gas;
    }

    public void setGas(List<Meter> gas) {
        this.gas = gas;
    }

    public List<Meter> getWater() {
        return water;
    }

    public void setWater(List<Meter> water) {
        this.water = water;
    }
}