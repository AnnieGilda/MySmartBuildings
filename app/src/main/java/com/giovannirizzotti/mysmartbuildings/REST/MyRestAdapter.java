package com.giovannirizzotti.mysmartbuildings.REST;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyRestAdapter {
    private ResultsAPI apiInterface;

    public MyRestAdapter(String ipServer, String serverPort, String ipCoapServer, String coapServerPort, boolean json) {
        String url = "http://" + ipServer + ":" + serverPort + "/coap://" + ipCoapServer + ":" + coapServerPort + "/";

        Gson gson = new GsonBuilder()
                .setDateFormat("dd'-'MM'-'yyyy'T'HH':'mm':'ss'.'SSS'Z'")
                .create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.readTimeout(120, TimeUnit.SECONDS);
        httpClient.connectTimeout(120, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);

        Retrofit.Builder builder = new Retrofit.Builder().baseUrl(url).client(httpClient.build());
        if (json)
            builder.addConverterFactory(GsonConverterFactory.create(gson));
        else
            builder.addConverterFactory(new ToStringConverterFactory());

        Retrofit retrofit = builder.build();
        apiInterface = retrofit.create(ResultsAPI.class);
    }

    public ResultsAPI getApiService() {
        return apiInterface;
    }
}
