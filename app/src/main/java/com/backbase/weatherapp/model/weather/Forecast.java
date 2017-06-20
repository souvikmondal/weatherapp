package com.backbase.weatherapp.model.weather;

import java.util.List;

/**
 * Created by Souvik on 19/06/17.
 */

public class Forecast {

    private List<Climate> list;

    public List<Climate> getList() {
        return list;
    }

    public void setList(List<Climate> list) {
        this.list = list;
    }
}
