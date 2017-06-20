package com.backbase.weatherapp.main;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.backbase.weatherapp.model.City;

/**
 * Created by Souvik on 20/06/17.
 */

public class MainDataFragment extends Fragment {

    private static final long REFRESH_FREQUENCY = 60000; //1 minute

    private City selectedCurrentCity;
    private long lastRefreshTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setSelectedCurrentCity(City selectedCurrentCity) {
        this.selectedCurrentCity = selectedCurrentCity;
    }

    public City getSelectedCurrentCity() {
        return selectedCurrentCity;
    }

    public void setLastRefreshTime() {
        this.lastRefreshTime = System.currentTimeMillis();
    }

    public boolean isTimeForRefresh() {
        return System.currentTimeMillis() - lastRefreshTime > REFRESH_FREQUENCY;
    }
}
