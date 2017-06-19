package com.backbase.weatherapp.util.provider;

import com.backbase.weatherapp.model.weather.Climate;
import com.backbase.weatherapp.util.provider.IDownloadListener;
import com.backbase.weatherapp.util.provider.ResourceProvider;
import com.backbase.weatherapp.util.provider.types.ClimateResource;
import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Souvik on 17/06/17.
 */

public final class WeatherProvider {

    private static final String URL = "http://api.openweathermap.org/data/2.5/" +
                "weather?lat=$lat&lon=$lon&appid=9c1f915599217340bb8b97ad31019648&units=metric";

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
                                             IDownloadListener<Climate> listener,
                                             boolean force){

        String endpoint = URL.replace("$lat", String.valueOf(latLng.latitude))
                .replace("$lon", String.valueOf(latLng.longitude));

        ClimateResource climateResource = new ClimateResource();

        ResourceProvider.getInstance().load(endpoint, climateResource, listener, force);

    }


}
