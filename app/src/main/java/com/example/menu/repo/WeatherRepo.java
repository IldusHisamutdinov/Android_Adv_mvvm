package com.example.menu.repo;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.example.menu.BuildConfig;
import com.example.menu.services.model.ResponseWeather;
import com.example.menu.services.repository.OpenWeather;
import com.example.menu.services.repository.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepo {
    private Application application;
    private OpenWeather openWeather;
    private MutableLiveData<Boolean> success;

    public void setSuccess(Boolean result) {
        success = new MutableLiveData<>();
        this.success.setValue(result);
    }

    public MutableLiveData<Boolean> getSuccess() {
        return success;
    }

    public WeatherRepo(Application application) {
        this.application = application;
        openWeather = RetrofitService.getInstance();
    }

    public MutableLiveData<ResponseWeather> loadWeatherInfo(String city, String lat, String lon, String metric, String keyApi, String lang) {
        final MutableLiveData<ResponseWeather> weatherMutableLiveData = new MutableLiveData<>();

        openWeather.loadWeatherCity(city, lat, lon, metric, BuildConfig.WEATHER_API_KEY, lang)
                .enqueue(new Callback<ResponseWeather>() {
                    @Override
                    public void onResponse(Call<ResponseWeather> call, Response<ResponseWeather> response) {
                        if (response != null) {
                            setSuccess(true);
                      //      Log.d("DATA REQUIRED", new Gson().toJson(response));
                            weatherMutableLiveData.setValue(response.body());
                        }
                        setSuccess(false);
                    }

                    @Override
                    public void onFailure(Call<ResponseWeather> call, Throwable t) {
                        setSuccess(false);
                    }
                });
        return weatherMutableLiveData;
    }

}
