package com.backbase.weatherapp.model.weather;


import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Souvik on 17/06/17.
 */

public class Climate {

    private LatLng coord;
    private Weather[] weather;
    private String base;
    private Main main;
    private Wind wind;

    public void setBase(String base) {
        this.base = base;
    }

    public void setCoord(LatLng coord) {
        this.coord = coord;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public LatLng getCoord() {
        return coord;
    }

    public Main getMain() {
        return main;
    }

    public String getBase() {
        return base;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public Wind getWind() {
        return wind;
    }
}
