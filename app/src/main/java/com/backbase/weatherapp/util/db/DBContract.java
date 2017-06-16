package com.backbase.weatherapp.util.db;

/**
 * Created by Souvik on 16/06/17.
 */

public class DBContract {

    public static final String TABLE_NAME = "cities";

    public static final String COL_CITY = "city";
    public static final String COL_LAT = "lat";
    public static final String COL_LON = "lon";

    public static final String CREATE_TABLE_CITIES = "create table " + TABLE_NAME
            + " (" + "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_CITY + " TEXT NOT NULL UNIQUE, "
            + COL_LAT + " INTEGER, "
            + COL_LON + " INTEGER);";
}
