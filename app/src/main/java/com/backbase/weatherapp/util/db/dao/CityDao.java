package com.backbase.weatherapp.util.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.util.db.DBContract;
import com.backbase.weatherapp.util.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

public class CityDao {

    private DBHelper dbHelper;

    public CityDao(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public List<City> getAllFavCities() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DBContract.TABLE_NAME, null, null, null, null, null, null);

        List<City> cities = new ArrayList<>();

        while (cursor.moveToNext()) {
            City city = new City();
            city.setDesc(cursor.getString(cursor.getColumnIndex(DBContract.COL_CITY)));
            city.setLat(cursor.getDouble(cursor.getColumnIndex(DBContract.COL_LAT)));
            city.setLon(cursor.getDouble(cursor.getColumnIndex(DBContract.COL_LON)));


            cities.add(city);
        }

        cursor.close();

        return cities;
    }

    public static String getCityName(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(DBContract.COL_CITY));
    }

    public Cursor getCitiesCursor() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.query(DBContract.TABLE_NAME, null, null, null, null, null, null);

    }

    public void saveFavCity(City city) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(DBContract.COL_CITY, city.getDesc());
        contentValues.put(DBContract.COL_LAT, city.getLat());
        contentValues.put(DBContract.COL_LON, city.getLon());

        db.insert(DBContract.TABLE_NAME, null, contentValues);

    }
}