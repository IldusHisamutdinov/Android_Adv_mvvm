package com.example.menu.services.repository;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static Retrofit instance = null;
    private static final String BASE_URL = "https://api.openweathermap.org/";

    public static OpenWeather getInstance() {
        if(instance == null){
            instance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance.create(OpenWeather.class);
    }
}
