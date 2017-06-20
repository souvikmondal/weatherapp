package com.backbase.weatherapp.util.provider.types;

import com.backbase.weatherapp.model.weather.Climate;
import com.backbase.weatherapp.model.weather.Forecast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Souvik on 19/06/17.
 */

public class ForecastResource implements RemoteResource<Forecast> {

    private static Gson gson = new GsonBuilder().create();
    private Forecast forecast;
    private int size;

    @Override
    public void prepare(InputStream inputStream) throws IOException {
        size = inputStream.available();
        forecast = gson.fromJson(new InputStreamReader(inputStream), Forecast.class);
    }

    @Override
    public Forecast getResource() {
        return forecast;
    }

    @Override
    public int size() {
        return size;
    }
}
