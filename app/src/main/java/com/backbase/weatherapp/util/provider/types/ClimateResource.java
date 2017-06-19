package com.backbase.weatherapp.util.provider.types;

import com.backbase.weatherapp.model.weather.Climate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Souvik on 19/06/17.
 */

public class ClimateResource implements RemoteResource<Climate> {

    private static Gson gson = new GsonBuilder().create();
    private Climate climate;
    private int size;

    @Override
    public void prepare(InputStream inputStream) throws IOException {
        size = inputStream.available();
        climate = gson.fromJson(new InputStreamReader(inputStream), Climate.class);
    }

    @Override
    public Climate getResource() {
        return climate;
    }

    @Override
    public int size() {
        return size;
    }
}
