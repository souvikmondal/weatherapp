package com.backbase.weatherapp.main;

import com.backbase.weatherapp.model.City;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Souvik on 20/06/17.
 */

public class ClimateDataController  {

    public static final long REFRESH_FREQUENCY = 60000; //1 minute

    private static ClimateDataController instance;
    private static Lock lock = new ReentrantLock();

    private City selectedCurrentCity;
    private long lastRefreshTime;
    private boolean oneTimeForceRefresh;

    public static final ClimateDataController getInstance() {
        lock.lock();
        try {
            if (instance == null)
                instance = new ClimateDataController();
            return instance;
        } finally {
            lock.unlock();
        }
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

    public long getLastRefreshTime() {
        return lastRefreshTime;
    }

    public void setForceRefresh() {
        this.oneTimeForceRefresh = true;
    }

    public boolean isTimeForRefresh() {
        boolean ret = oneTimeForceRefresh || System.currentTimeMillis() -
                lastRefreshTime > REFRESH_FREQUENCY;
        oneTimeForceRefresh = false;
        return ret;
    }
}
