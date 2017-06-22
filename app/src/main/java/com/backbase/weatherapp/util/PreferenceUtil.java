package com.backbase.weatherapp.util;

import android.app.Activity;
import android.content.Context;

/**
 * Created by Souvik on 21/06/17.
 */

public class PreferenceUtil {

    public static final String getUnitSystem(Activity context) {
        return context.getPreferences(Context.MODE_PRIVATE)
                .getString("units", "metric");
    }

    public static final void saveUnitSystem(Activity context, String value) {
        context.getPreferences(Context.MODE_PRIVATE)
                .edit().putString("units", value)
                .commit();
    }

    public static final String getTemparatureUnit(Activity context) {
        String units = getUnitSystem(context);
        if (units.equalsIgnoreCase("metric")) {
            return "C";
        } else {
            return "F";
        }
    }

    public static final String getSpeedUnit(Activity context) {
        String units = getUnitSystem(context);
        if (units.equalsIgnoreCase("metric")) {
            return "KMPH";
        } else {
            return "MPH";
        }
    }


}
