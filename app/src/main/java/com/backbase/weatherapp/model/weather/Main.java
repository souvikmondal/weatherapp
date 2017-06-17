package com.backbase.weatherapp.model.weather;

/**
 * Created by Souvik on 17/06/17.
 */

public class Main {

    private float temp;
    private float pressure;
    private float humidity;
    private float temp_min;
    private float temp_max;
    private float sea_level;
    private float grnd_level;

    public void setGrnd_level(float grnd_level) {
        this.grnd_level = grnd_level;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public void setSea_level(float sea_level) {
        this.sea_level = sea_level;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public void setTemp_max(float temp_max) {
        this.temp_max = temp_max;
    }

    public void setTemp_min(float temp_min) {
        this.temp_min = temp_min;
    }

    public float getGrnd_level() {
        return grnd_level;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getPressure() {
        return pressure;
    }

    public float getSea_level() {
        return sea_level;
    }

    public float getTemp() {
        return temp;
    }

    public float getTemp_max() {
        return temp_max;
    }

    public float getTemp_min() {
        return temp_min;
    }
}
