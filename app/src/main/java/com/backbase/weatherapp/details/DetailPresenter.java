package com.backbase.weatherapp.details;

import android.support.v7.app.AppCompatActivity;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.model.weather.Climate;

/**
 * Created by Souvik on 18/06/17.
 */

public class DetailPresenter {

    private AppCompatActivity activityContext;

    public DetailPresenter(AppCompatActivity activityContext) {
        this.activityContext = activityContext;
    }

    public DetailBindingModel getTodayWeatherBindingModel(Climate climate) {
        DetailBindingModel model = new DetailBindingModel();
        model.setCity(climate.getName());
        //model.setCurrentDate(climate.getDt() + ""); //TODO date format
        model.setCurrentDate("Today");
        model.setHumidity(((int)climate.getMain().getHumidity()) + "%");
        model.setTemp(String.valueOf((int)climate.getMain().getTemp()));
        model.setTemp_max(String.valueOf((int)climate.getMain().getTemp_max()));
        model.setTemp_min(String.valueOf((int)climate.getMain().getTemp_min()));
        model.setWind(String.valueOf((int)climate.getWind().getSpeed()) + " km/h");
        model.setTempUnit("C");
        return model;
    }

    public int getWeatherIconResource(Climate climate) {
        int resDrawableId = 0;
        String id = String.valueOf(climate.getWeather()[0].getId());

        if (id.startsWith("2")) {
            resDrawableId = R.drawable.storm;
        } else if (id.startsWith("3")) {
            resDrawableId = R.drawable.drizzle;
        } else if (id.startsWith("5")) {
            resDrawableId = R.drawable.rain;
        } else if (id.startsWith("6")) {
            resDrawableId = R.drawable.snow;
        } else if (id.equals("800")) {
            resDrawableId = R.drawable.sun;
        } else {
            resDrawableId = R.drawable.clouds;
        }

        return resDrawableId;
    }

    public int getWeatherTextColorResource(Climate climate) {
        int resDrawableId = 0;
        String id = String.valueOf(climate.getWeather()[0].getId());

        if (id.startsWith("2")) {
            resDrawableId = R.drawable.storm;
        } else if (id.startsWith("3")) {
            resDrawableId = R.drawable.drizzle;
        } else if (id.startsWith("5")) {
            resDrawableId = R.drawable.rain;
        } else if (id.startsWith("6")) {
            resDrawableId = R.drawable.snow;
        } else if (id.equals("800")) {
            resDrawableId = R.drawable.sun;
        } else {
            resDrawableId = R.drawable.clouds;
        }

        return resDrawableId;
    }

}
