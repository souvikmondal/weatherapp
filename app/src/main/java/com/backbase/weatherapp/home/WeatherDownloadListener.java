package com.backbase.weatherapp.home;

import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.model.weather.Climate;
import com.backbase.weatherapp.util.provider.IDownloadListener;

import java.lang.ref.WeakReference;

/**
 * Created by Souvik on 19/06/17.
 */

public class WeatherDownloadListener implements IDownloadListener<Climate> {

    private WeakReference<City> reference;

    public WeatherDownloadListener(City city) {
        reference = new WeakReference<City>(city);
    }

    @Override
    public void completed(String key, Climate data) {

    }
}
