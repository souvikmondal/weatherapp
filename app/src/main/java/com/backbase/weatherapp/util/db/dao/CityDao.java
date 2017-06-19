package com.backbase.weatherapp.util.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.util.db.DBContract;
import com.backbase.weatherapp.util.db.DBHelper;
import com.google.android.gms.maps.model.LatLng;

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

    public static void removeAllCities(Context context) {
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        db.delete(DBContract.TABLE_NAME, null, null);
    }

    public static void remove(City city, Context context) {
        SQLiteDatabase db = DBHelper.getInstance(context).getReadableDatabase();
        String where = DBContract.COL_CITY + "=?";
        String[] vals = new String[1];
        vals[0] = city.getDesc();
        db.delete(DBContract.TABLE_NAME, where, vals);
    }

    public static String getCityName(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(DBContract.COL_CITY));
    }

    public static City getCity(Cursor cursor) {
        City city = new City();
        city.setDesc(cursor.getString(cursor.getColumnIndex(DBContract.COL_CITY)));
        city.setLat(cursor.getDouble(cursor.getColumnIndex(DBContract.COL_LAT)));
        city.setLon(cursor.getDouble(cursor.getColumnIndex(DBContract.COL_LON)));
        return city;
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