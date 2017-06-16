package com.backbase.weatherapp.util.db;

/**
 * Created by Souvik on 16/06/17.
 */

public class DBContract {

    public static final String TABLE_NAME = "cities";

    public static final String COL_CITY = "city";
    public static final String COL_LAT = "city";
    public static final String COL_LON = "city";

    public static final String CREATE_TABLE_CITIES = "create table " + TABLE_NAME
            + " (" + "id" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_CITY + " TEXT NOT NULL, "
            + COL_LAT + " INTEGER, "
            + COL_LON + " INTEGER);";
}
