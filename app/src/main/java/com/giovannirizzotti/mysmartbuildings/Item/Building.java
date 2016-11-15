package com.giovannirizzotti.mysmartbuildings.Item;

public class Building {
    private final String name;
    private final String coapPort;
    private final String vpnName;

    public Building(String name, String coapPort, String data1) {
        this.name = name;
        this.coapPort = coapPort;
        this.vpnName = data1;
    }

    public String getName() {
        return name;
    }

    public String getVpnName() {
        return vpnName;
    }

    public String getCoapPort() {
        return coapPort;
    }
}