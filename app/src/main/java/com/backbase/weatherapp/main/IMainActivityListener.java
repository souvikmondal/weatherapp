package com.backbase.weatherapp.main;

import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.model.weather.Climate;

/**
 * Created by Souvik on 17/06/17.
 */

public interface IMainActivityListener {

    public void showDetails(Climate data);

    public void showMap();

}
