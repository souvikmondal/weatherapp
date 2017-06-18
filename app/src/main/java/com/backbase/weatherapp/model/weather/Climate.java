package com.backbase.weatherapp.model.weather;


import com.backbase.weatherapp.model.City;
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
    private String name;
    private long dt;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

    public long getDt() {
        return dt;
    }

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
