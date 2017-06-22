package com.backbase.weatherapp.main.provider;

import com.backbase.weatherapp.main.BackgroundTaskListener;
import com.backbase.weatherapp.model.weather.Climate;
import com.backbase.weatherapp.main.provider.types.ClimateResource;
import com.backbase.weatherapp.main.provider.types.ForecastResource;
import com.backbase.weatherapp.util.CONSTANT;
import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Souvik on 17/06/17.
 */

public final class WeatherProvider {

    private static Lock lock = new ReentrantLock();

    private static WeatherProvider instance;

    private WeatherProvider() {
    }

    public static final WeatherProvider getInstance() {
        lock.lock();
        try {
            if (instance == null) {
                instance = new WeatherProvider();
            }
            return instance;
        } finally {
            lock.unlock();
        }
    }

    public final void retrieveWeather(LatLng latLng, String unit,
                                             BackgroundTaskListener<Climate> listener,
                                             boolean force, AppDataProvider appDataProvider){

        String endpoint = CONSTANT.URL_WEATHER_CURRENT.replace("$lat", String.valueOf(latLng.latitude))
                .replace("$lon", String.valueOf(latLng.longitude))
                + unit;

        ClimateResource climateResource = new ClimateResource();

        appDataProvider.load(endpoint, climateResource, listener, force);

    }

    public final void retrieveForecast(LatLng latLng, String unit,
                                      BackgroundTaskListener<Climate> listener,
                                      boolean force, AppDataProvider appDataProvider){

        String endpoint = CONSTANT.URL_WEATHER_FORECAST.replace("$lat", String.valueOf(latLng.latitude))
                .replace("$lon", String.valueOf(latLng.longitude))
                + unit;

        ForecastResource forecastResource = new ForecastResource();

        appDataProvider.load(endpoint, forecastResource, listener, force);

    }


}
