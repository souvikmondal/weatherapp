package com.backbase.weatherapp.home;

import android.content.Context;
import android.database.Cursor;

import com.backbase.weatherapp.util.AsyncProvider;
import com.backbase.weatherapp.util.db.DBHelper;
import com.backbase.weatherapp.util.db.dao.CityDao;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by Souvik on 17/06/17.
 */

public class HomePresenter {

    private Context context;

    public HomePresenter(Context context) {
        this.context = context;
    }

    public Cursor getFavCities() {

        Callable<Cursor> cursorCallable = new Callable<Cursor>() {
            @Override
            public Cursor call() throws Exception {
                CityDao cityDao = new CityDao(DBHelper.getInstance(context));
                return cityDao.getCitiesCursor();
            }
        };

        FutureTask<Cursor> task = new FutureTask<Cursor>(cursorCallable);
        AsyncProvider.getInstance().execute(task);

        try {
            return task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

}
