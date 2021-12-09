package com.example.menu.services.repository;

import com.example.menu.services.model.ResponseWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeather {
    String BASE_URL = "https://api.openweathermap.org/";

    @GET("data/2.5/weather")
    Call<ResponseWeather> loadWeatherCity(@Query("q") String cityCountry, @Query("lat") String lat, @Query("lon") String lon, @Query("units") String metric, @Query("appid") String keyApi, @Query("lang") String ru);

}

