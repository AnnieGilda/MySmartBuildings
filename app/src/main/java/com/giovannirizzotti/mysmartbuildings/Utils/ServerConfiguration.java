package com.giovannirizzotti.mysmartbuildings.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.giovannirizzotti.mysmartbuildings.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by giovanni on 24/09/16.
 */

public class ServerConfiguration {
    private String proxyServerIP;
    private String coapServerIP;
    private String proxyServerPort;
    private String coapServerPort;
    private int serverNumber = 1;
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private boolean requireConfiguration = false;

    public ServerConfiguration(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("ServerConfiguration", MODE_PRIVATE);
        this.editor =  sharedPreferences.edit();
        this.proxyServerIP = sharedPreferences.getString(context.getString(R.string.preference_proxy_ip), "");
        this.coapServerIP = sharedPreferences.getString(context.getString(R.string.preference_coap_ip), "");
        this.proxyServerPort = sharedPreferences.getString(context.getString(R.string.preference_proxy_port), "");
        this.coapServerPort = sharedPreferences.getString(context.getString(R.string.preference_coap_port), "");
        this.serverNumber = sharedPreferences.getInt(context.getString(R.string.preference_server_number), 1);
        requireConfiguration();
    }

    private void requireConfiguration() {
        if (this.proxyServerIP.length() == 0 || this.proxyServerPort.length() == 0 || this.coapServerIP.length() == 0 || this.coapServerPort.length() == 0 || serverNumber< 1)
            requireConfiguration = true;
    }

    public void setAll(String proxy_ip, String proxy_port, String coap_ip, String coap_port, int serverNumber) {
        this.proxyServerIP = proxy_ip;
        this.proxyServerPort = proxy_port;
        this.coapServerIP = coap_ip;
        this.coapServerPort = coap_port;
        this.serverNumber = serverNumber;
        editor.putString(context.getString(R.string.preference_proxy_ip), proxyServerIP);
        editor.putString(context.getString(R.string.preference_proxy_port), proxyServerPort);
        editor.putString(context.getString(R.string.preference_coap_ip), coapServerIP);
        editor.putString(context.getString(R.string.preference_coap_port), coapServerPort);
        editor.putInt(context.getString(R.string.preference_server_number), serverNumber);
        editor.commit();
        requireConfiguration();
    }

    public boolean getRequireConfiguration() {
        return this.requireConfiguration;
    }

    public String getProxyServerIP() {
        return proxyServerIP;
    }

    public void setProxyServerIP(String proxyServerIP) {
        this.proxyServerIP = proxyServerIP;
        editor.putString(context.getString(R.string.preference_proxy_ip), this.proxyServerIP);
        editor.commit();
        requireConfiguration();
    }

    public String getCoapServerIP() {
        return coapServerIP;
    }

    public void setCoapServerIP(String coapServerIP) {
        this.coapServerIP = coapServerIP;
        editor.putString(context.getString(R.string.preference_coap_ip), this.coapServerIP);
        editor.commit();
        requireConfiguration();
    }

    public String getProxyServerPort() {
        return proxyServerPort;
    }

    public void setProxyServerPort(String proxyServerPort) {
        this.proxyServerPort = proxyServerPort;
        editor.putString(context.getString(R.string.preference_proxy_port), this.proxyServerPort);
        editor.commit();
        requireConfiguration();
    }

    public String getCoapServerPort() {
        return coapServerPort;
    }

    public void setCoapServerPort(String coapServerPort) {
        this.coapServerPort = coapServerPort;
        editor.putString(context.getString(R.string.preference_coap_port), this.coapServerPort);
        editor.commit();
        requireConfiguration();
    }

    public int getServerNumber() {
        return this.serverNumber;
    }

    public void setServerNumber(int serverNumber) {
        this.serverNumber = serverNumber;
    }
}
