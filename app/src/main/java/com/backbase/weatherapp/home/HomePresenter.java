package com.backbase.weatherapp.home;

import android.content.Context;
import android.widget.TextView;

import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.util.ThreadPool;
import com.backbase.weatherapp.db.DBHelper;
import com.backbase.weatherapp.db.dao.CityDao;
import com.backbase.weatherapp.main.BackgroundTaskListener;
import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.FutureTask;

/**
 * Created by Souvik on 17/06/17.
 */

public class HomePresenter {

    private Context context;

    private Map<TextView, WeakReference<FutureTask>> requestMap;

    public HomePresenter(Context context) {
        this.context = context;
        requestMap = new HashMap<>();
    }

    public void cancelPotentialDownload(TextView textView, LatLng latLng) {
        WeakReference<FutureTask> taskWeakReference = requestMap.get(textView);
        if (taskWeakReference != null) {
            taskWeakReference.get().cancel(true);
        }
    }

    void saveCityAsFav(String cityDesc, LatLng latLng, BackgroundTaskListener listener) {
        City city = new City();
        city.setDesc(cityDesc);
        city.setLat(latLng.latitude);
        city.setLon(latLng.longitude);
        ThreadPool.getInstance().execute(new SaveCityToDBTask(city, listener));
    }

    class SaveCityToDBTask implements Runnable {

        City city;
        BackgroundTaskListener listener;

        SaveCityToDBTask(City city, BackgroundTaskListener listener) {
            this.city = city;
            this.listener = listener;
        }

        @Override
        public void run() {
            new CityDao(DBHelper.getInstance(context)).saveFavCity(city);
            listener.completed(null, null);
        }
    }

}
