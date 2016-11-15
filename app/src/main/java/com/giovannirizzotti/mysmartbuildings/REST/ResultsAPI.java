package com.giovannirizzotti.mysmartbuildings.REST;

import com.giovannirizzotti.mysmartbuildings.REST.Pojo.AllMetersReceiver;
import com.giovannirizzotti.mysmartbuildings.REST.Pojo.MeterReceiver;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by giovannirizzotti on 21/09/16.
 */
public interface ResultsAPI {
    @GET("setlighting")
    Call<String> setLighting();

    @GET("opendoors")
    Call<String> openDoors();

    @GET("watermeters")
    Call<MeterReceiver> waterMeter();

    @GET("gasmeters")
    Call<MeterReceiver> gasMeter();

    @GET("electricitymeters")
    Call<MeterReceiver> electricityMeter();

    @GET("allmeters")
    Call<AllMetersReceiver> allMeters();
}