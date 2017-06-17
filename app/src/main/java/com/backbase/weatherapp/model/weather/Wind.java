package com.backbase.weatherapp.model.weather;

/**
 * Created by Souvik on 17/06/17.
 */

public class Wind {

    private float speed;
    private float deg;

    public void setDeg(float deg) {
        this.deg = deg;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDeg() {
        return deg;
    }

    public float getSpeed() {
        return speed;
    }
}
