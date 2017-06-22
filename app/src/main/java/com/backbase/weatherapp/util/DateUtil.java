package com.backbase.weatherapp.util;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Souvik on 19/06/17.
 */

public class DateUtil {

    public static final String[] DAYS = {"SUN", "MON", "TUE", "WED", "THU", "FRI",
            "SAT"};

    public static final String getDayOfWeek(long timeInSec) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInSec * 1000);

        return DAYS[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    public static boolean isAfter(long timeInSec, long currentDate) {
        return (timeInSec * 1000) > (currentDate * 1000);
    }

    public static final String getNextDay(String day) {
        String next = null;
        if (day.equalsIgnoreCase("SUN")) {
            next = "MON";
        } else if (day.equalsIgnoreCase("MON")) {
            next = "TUE";
        } else if (day.equalsIgnoreCase("TUES")) {
            next = "WED";
        } else if (day.equalsIgnoreCase("WED")) {
            next = "THU";
        } else if (day.equalsIgnoreCase("THU")) {
            next = "FRI";
        } else if (day.equalsIgnoreCase("FRI")) {
            next = "SAT";
        } else if (day.equalsIgnoreCase("SAT")) {
            next = "SUN";
        }
        return next;
    }

    public static final String formatTime(long timeInMillis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(new Date(timeInMillis));
    }

    public static final String getTimeOfDay(long timeInSec) {
        Calendar calendar = Calendar.getInstance();
        long t = ((long)timeInSec) * 1000l;
        calendar.setTimeInMillis(t);

        return calendar.get(Calendar.HOUR) +
                ((calendar.get(Calendar.AM_PM) == Calendar.AM) ? " AM" : " PM");
    }

}
