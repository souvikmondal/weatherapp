package com.backbase.weatherapp.util;

import android.telecom.Call;
import android.util.Log;
import android.view.View;

import com.backbase.weatherapp.model.weather.Climate;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Souvik on 17/06/17.
 */

public final class WeatherProvider {

    private static Gson gson = new GsonBuilder().create();

    private static final String URL = "http://api.openweathermap.org/data/2.5/" +
                "weather?lat=$lat&lon=$lon&appid=9c1f915599217340bb8b97ad31019648&units=metric";

    public static final void retrieveWeather(LatLng latLng, String unit,
                                             IDownloadListener<Climate> listener){

        String endpoint = URL.replace("$lat", String.valueOf(latLng.latitude))
                .replace("$lon", String.valueOf(latLng.longitude));

        AsyncProvider.getInstance().execute(new WeatherCallable(endpoint, listener));
    }

    private static class WeatherCallable implements Runnable {

        String endpoint;
        IDownloadListener<Climate> listener;

        WeatherCallable(String endpoint, IDownloadListener<Climate> listener) {
            this.endpoint = endpoint;
            this.listener = listener;
        }

        @Override
        public void run() {
            String data = null;
            try {
                data = HttpUtil.get(endpoint);
                Climate obj = gson.fromJson(data, Climate.class);
                listener.completed(endpoint, obj);
            } catch (IOException e) {
                e.printStackTrace();
                listener.completed(endpoint, null);
            }

        }
    }



}
