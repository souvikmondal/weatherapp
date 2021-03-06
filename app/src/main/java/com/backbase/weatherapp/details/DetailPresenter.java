package com.backbase.weatherapp.details;

import android.support.v7.app.AppCompatActivity;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.model.weather.Climate;
import com.backbase.weatherapp.util.DateUtil;
import com.backbase.weatherapp.util.PreferenceUtil;

/**
 * Created by Souvik on 18/06/17.
 */

public class DetailPresenter {

    private AppCompatActivity activityContext;

    public DetailPresenter(AppCompatActivity activityContext) {
        this.activityContext = activityContext;
    }

    public DetailBindingModel getTodayWeatherBindingModel(Climate climate, City city) {
        DetailBindingModel model = new DetailBindingModel();
        model.setCity(city.getDesc());
        model.setStatus(climate.getWeather()[0].getDescription());
        model.setCurrentDate(DateUtil.getDayOfWeek(climate.getDt())); //TODO date format
        model.setHumidity(((int)climate.getMain().getHumidity()) + "%");
        model.setTemp(String.valueOf((int)climate.getMain().getTemp()));
        model.setTemp_max(String.valueOf((int)climate.getMain().getTemp_max()));
        model.setTemp_min(String.valueOf((int)climate.getMain().getTemp_min()));
        model.setWind(String.valueOf((int)climate.getWind().getSpeed()) +
                    PreferenceUtil.getSpeedUnit(activityContext));
        model.setTempUnit(PreferenceUtil.getTemparatureUnit(activityContext));
        return model;
    }

    public DetailBindingModel getInitialWeatherBindingModel(City city) {
        DetailBindingModel model = new DetailBindingModel();
        model.setCity(city.getDesc());
        //model.setCurrentDate(climate.getDt() + ""); //TODO date format
        model.setCurrentDate("Today");
        model.setHumidity("__");
        model.setTemp("__");
        model.setTemp_max("__");
        model.setTemp_min("__");
        model.setWind("__");
        model.setTempUnit(PreferenceUtil.getTemparatureUnit(activityContext));
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
        } else if (id.startsWith("7")) {
            resDrawableId = R.drawable.mist;
        } else if (id.equals("800")) {
            resDrawableId = R.drawable.sun;
        } else {
            resDrawableId = R.drawable.clouds;
        }

        return resDrawableId;
    }

}
