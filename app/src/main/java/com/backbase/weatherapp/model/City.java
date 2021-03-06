package com.backbase.weatherapp.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Souvik on 16/06/17.
 */

public class City {

    private String desc;
    private double lat;
    private double lon;
    private LatLng latLng;

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        latLng = new LatLng(lat, lon);
        return latLng;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
