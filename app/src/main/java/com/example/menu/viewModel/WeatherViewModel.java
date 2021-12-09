package com.example.menu.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.menu.repo.WeatherRepo;
import com.example.menu.services.model.ResponseWeather;

public class WeatherViewModel extends AndroidViewModel {
    private WeatherRepo weatherRepo;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        weatherRepo = new WeatherRepo(application);
    }

    public MutableLiveData<ResponseWeather> getWeatherInfo(String cityName, String lat, String lon, String metric, String keyApi, String lang){
        return weatherRepo.loadWeatherInfo(cityName, lat, lon, metric, keyApi, lang);
    }
}
