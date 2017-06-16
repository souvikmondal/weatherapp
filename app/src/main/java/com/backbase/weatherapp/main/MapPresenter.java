package com.backbase.weatherapp.main;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.util.AsyncProvider;
import com.backbase.weatherapp.util.db.DBHelper;
import com.backbase.weatherapp.util.db.dao.CityDao;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by Souvik on 16/06/17.
 */

class MapPresenter {

    private Context context;
    private Map<String, FutureTask<String>> cityMap;

    MapPresenter(Context context) {
        this.context = context;
        cityMap = new HashMap<>();
    }

    void saveCityAsFav(String cityDesc, LatLng latLng) {
        City city = new City();
        city.setDesc(cityDesc);
        city.setLat(latLng.latitude);
        city.setLon(latLng.longitude);
        AsyncProvider.getInstance().execute(new SaveCityToDBTask(city));
    }

    String getCity(LatLng latLng) {
        cancelOtherPotentialTask();
        String key = latLng.latitude + "," + latLng.longitude;
        FutureTask<String> futureTask = new FutureTask<String>(new ReverseGeoCoderTask(latLng));
        AsyncProvider.getInstance().execute(futureTask);
        try {
            return futureTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void cancelOtherPotentialTask() {
        Iterator<FutureTask<String>> iterator = cityMap.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().cancel(true);
        }
    }

    class SaveCityToDBTask implements Runnable {

        City city;

        SaveCityToDBTask(City city) {
            this.city = city;
        }

        @Override
        public void run() {
            new CityDao(DBHelper.getInstance(context)).saveFavCity(city);
        }
    }

    class ReverseGeoCoderTask implements Callable<String> {

        LatLng coordinates;

        ReverseGeoCoderTask(LatLng coordinates) {
            this.coordinates = coordinates;
        }


        @Override
        public String call() throws Exception {
            Geocoder geocoder = new Geocoder(context);
            List<Address> addresses = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                return address.getLocality();
            }
            return null;
        }
    }

}
