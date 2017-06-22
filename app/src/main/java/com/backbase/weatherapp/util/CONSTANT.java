package com.backbase.weatherapp.util;

/**
 * Created by Souvik on 22/06/17.
 */

public interface CONSTANT {


    String URL_WEATHER_ICON = "http://openweathermap.org/img/w/";
    String URL_WEATHER_CURRENT = "http://api.openweathermap.org/data/2.5/" +
                "weather?lat=$lat&lon=$lon&appid=9c1f915599217340bb8b97ad31019648&units=";
    String URL_WEATHER_FORECAST = "http://api.openweathermap.org/data/2.5/" +
            "forecast?lat=$lat&lon=$lon&appid=9c1f915599217340bb8b97ad31019648&units=";
}
