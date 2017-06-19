package com.backbase.weatherapp.home;

import android.content.Context;
import android.database.Cursor;
import android.widget.TextView;

import com.backbase.weatherapp.util.provider.AsyncProvider;
import com.backbase.weatherapp.util.db.DBHelper;
import com.backbase.weatherapp.util.db.dao.CityDao;
import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by Souvik on 17/06/17.
 */

public class HomePresenter {

    private Context context;

    private Map<TextView, WeakReference<FutureTask>> requestMap;

    public HomePresenter(Context context) {
        this.context = context;
        requestMap = new HashMap<>();
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

    public void cancelPotentialDownload(TextView textView, LatLng latLng) {
        WeakReference<FutureTask> taskWeakReference = requestMap.get(textView);
        if (taskWeakReference != null) {
            taskWeakReference.get().cancel(true);
        }
    }

}
