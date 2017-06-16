package com.backbase.weatherapp.util.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "weatherapp";
    public static final int DATABASE_VERSION = 1;

    private static DBHelper instance;
    private static Lock lock = new ReentrantLock();

    public static final DBHelper getInstance(Context context) {
        lock.lock();
        if (instance == null)
            instance = new DBHelper(context);
        lock.unlock();
        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.CREATE_TABLE_CITIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}