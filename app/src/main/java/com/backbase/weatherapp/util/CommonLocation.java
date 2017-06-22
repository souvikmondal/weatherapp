package com.backbase.weatherapp.util;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Souvik on 20/06/17.
 */

public class CommonLocation {

    private static final Map<String, LatLng> LOCATION_MAP = new HashMap<>();

    static {
        LOCATION_MAP.put("New Delhi", new LatLng(28.613939, 77.209021));
        LOCATION_MAP.put("Amsterdam", new LatLng(52.370216, 4.895168));
        LOCATION_MAP.put("Stockholm", new LatLng(59.329323, 18.068581));
        LOCATION_MAP.put("London", new LatLng(51.507351, -0.127758));
        LOCATION_MAP.put("Paris", new LatLng(48.856614, 2.352222));
        LOCATION_MAP.put("Helsinki", new LatLng(48.856614, 2.352222));
        LOCATION_MAP.put("Copenhagen", new LatLng(55.676097, 12.568337));
        LOCATION_MAP.put("New York", new LatLng(40.712784, -74.005941));
        LOCATION_MAP.put("Los Angeles", new LatLng(34.052234, -118.243685));
        LOCATION_MAP.put("Baltimore", new LatLng(39.290385, -76.612189));
//        LOCATION_MAP.put("Copenhagen", new LatLng(55.676097, 12.568337));
//        LOCATION_MAP.put("Copenhagen", new LatLng(55.676097, 12.568337));
//        LOCATION_MAP.put("Copenhagen", new LatLng(55.676097, 12.568337));
//        LOCATION_MAP.put("Copenhagen", new LatLng(55.676097, 12.568337));
    }

    public static final String[] getAllCities() {
        Iterator<String> iterator = LOCATION_MAP.keySet().iterator();
        List<String> v = new ArrayList<>();
        while (iterator.hasNext()) {
            v.add(iterator.next());
        }
        String[] values = new String[v.size()];
        int index = 0;
        for (String s:v) {
            values[index++] = s;
        }
        return values;
    }

    public static final LatLng getLocation(String city) {
        return LOCATION_MAP.get(city);
    }

}
