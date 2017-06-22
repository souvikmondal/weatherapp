package com.backbase.weatherapp.details;

/**
 * Created by Souvik on 18/06/17.
 */

public class DetailBindingModel {

    private String city;
    private String temp;
    private String wind;
    private String humidity;
    private String temp_min;
    private String temp_max;
    private String currentDate;
    private String tempUnit;
    private String rainChance;
    private String status;

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setRainChance(String rainChance) {
        this.rainChance = rainChance;
    }

    public String getRainChance() {
        return rainChance;
    }

    public void setTempUnit(String tempUnit) {
        this.tempUnit = tempUnit;
    }

    public String getTempUnit() {
        return tempUnit;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public void setTemp_max(String temp_max) {
        this.temp_max = temp_max;
    }

    public void setTemp_min(String temp_min) {
        this.temp_min = temp_min;
    }

    public String getCity() {
        return city;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getTemp() {
        return temp;
    }

    public String getTemp_max() {
        return temp_max;
    }

    public String getTemp_min() {
        return temp_min;
    }

    public String getWind() {
        return wind;
    }
}
