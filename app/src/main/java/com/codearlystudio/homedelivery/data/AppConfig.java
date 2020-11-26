package com.codearlystudio.homedelivery.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppConfig {
    private static final String BASE_URL = Constants.ROOT_BASE_URL;
    public static Retrofit getRetrofit() {
        return new Retrofit.Builder().baseUrl(AppConfig.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }
}