package com.example.menu.viewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.menu.BuildConfig;
import com.example.menu.services.model.ResponseWeather;
import com.example.menu.services.repository.OpenWeather;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherCoordViewModel extends ViewModel {
    private MutableLiveData<ResponseWeather> coordMutableLiveData;
    private MutableLiveData<Boolean> success;

    public void setSuccess(Boolean result) {
        success = new MutableLiveData<>();
        this.success.setValue(result);
    }

    public LiveData<Boolean> getSuccess() {
        return success;
    }

    public LiveData<ResponseWeather> getWeatherCoord(String lat, String lon, String metric, String keyApi, String lang) {
        coordMutableLiveData = new MutableLiveData<>();
        loadWeatherCoord(lat, lon, metric, keyApi, lang);

        return coordMutableLiveData;
    }

    private void loadWeatherCoord(String lat, String lon, String metric, String keyApi, String lang) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OpenWeather.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenWeather api = retrofit.create(OpenWeather.class);
        lang = "ru";
        metric = "metric";
        Call<ResponseWeather> call = api.loadWeather(lat, lon, metric, BuildConfig.WEATHER_API_KEY, lang);

        call.enqueue(new Callback<ResponseWeather>() {
            @Override
            public void onResponse(Call<ResponseWeather> call, Response<ResponseWeather> response) {
                if(response!= null ){
                    setSuccess(true);
                    Log.d("DATA REQUIRED", new Gson().toJson(response));
                    coordMutableLiveData.setValue(response.body());
                }
                setSuccess(false);
            }

            @Override
            public void onFailure(Call<ResponseWeather> call, Throwable t) {
                setSuccess(false);
            }
        });

    }




}
