package com.backbase.weatherapp.main.provider;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.main.BackgroundTaskListener;
import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.util.HttpUtil;
import com.backbase.weatherapp.util.ThreadPool;
import com.backbase.weatherapp.db.DBHelper;
import com.backbase.weatherapp.db.dao.CityDao;
import com.backbase.weatherapp.main.provider.types.RemoteResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by souvik on 7/10/2016.
 */
public final class AppDataProvider extends Fragment{

    private LruCache<String, RemoteResource> mResourceCache;
    private Map<String, List<BackgroundTaskListener>> mCallbackMap;
    private Lock lock;

    public AppDataProvider() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setRetainInstance(true);
    }

    private void init() {
        initResourceCache();
        mCallbackMap = new WeakHashMap<>();
        lock = new ReentrantLock();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    public void loadBookmarkedCities(@NonNull final BackgroundTaskListener<Cursor> callback,
                                     Context context) {

        final CityDao cityDao = new CityDao(DBHelper.getInstance(context));

        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                notifyDBLoad(cityDao.getCitiesCursor(), callback);
            }
        });
    }

    public void clearBookmarkedCities(@NonNull final BackgroundTaskListener<Cursor> callback,
                                      final Context context) {

        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                CityDao.removeAllCities(context);
                notifyDBLoad(null, callback);
            }
        });
    }

    public void deleteCity(@NonNull final BackgroundTaskListener<Cursor> callback,
                           @NonNull final Context context, @NonNull final City city) {

        final CityDao cityDao = new CityDao(DBHelper.getInstance(context));

        ThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                CityDao.remove(city, context);
                notifyDBLoad(null, callback);
            }
        });
    }



    public void load(@NonNull String url, @NonNull RemoteResource remoteResource,
                     @NonNull BackgroundTaskListener callback, boolean force) {
        lock.lock();
        try {
            boolean started = addCallbackToMap(url, callback);
            RemoteResource cachedRes = getResourceFromCache(url);
            if ((cachedRes == null && !started) || force) {//force download
                ThreadPool.getInstance().execute(new DownloaderTask(url, remoteResource));
            } else if (cachedRes != null) {// finished
                callback.completed(url, cachedRes.getResource());
            }

        } finally {
            lock.unlock();
        }
    }

    public void cancel(String url, BackgroundTaskListener callback) {
            List<BackgroundTaskListener> callbackList = getCallbacks(url);
            if (callbackList != null && callbackList.size() > 0) {
                callbackList.remove(callback);
            }
    }

    public void releaseCache() {
        mResourceCache.evictAll();
    }

    private boolean addCallbackToMap(String url, BackgroundTaskListener callback) {
        boolean callbackExist = true;
        List<BackgroundTaskListener> callbackList = mCallbackMap.get(url);
        if (callbackList == null) {
            callbackList = new ArrayList<>();
            mCallbackMap.put(url, callbackList);
            callbackExist = false;
        }
        callbackList.add(callback);
        return callbackExist;
    }

    private List<BackgroundTaskListener> getCallbacks(String url) {
        return mCallbackMap.get(url);
    }

    private void initResourceCache() {
        mResourceCache = new LruCache<String, RemoteResource>(computeCacheSize()) {
            @Override
            protected int sizeOf(String key, RemoteResource remoteResource) {
                return remoteResource.size() / 1024;
            }
        };
    }

    private int computeCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 16;
        return cacheSize;
    }

    private void addResourceToCache(String url, RemoteResource remoteResource) {
        if (remoteResource != null) {
            mResourceCache.put(url, remoteResource);
        }
    }

    private RemoteResource getResourceFromCache(String url) {
        final RemoteResource remoteResource = mResourceCache.get(url);
        return remoteResource;
    }

    private void notifyDBLoad(final Cursor cursor, final BackgroundTaskListener<Cursor> listener) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.completed(null, cursor);
            }
        });
    }

    /**
     * Should notify the result in the UI/Main thread.
     */
    private void notifyDownloaded(final String mUrl, final RemoteResource remoteResource) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //from UI thread and activity is always there
                List<BackgroundTaskListener> callbackList = mCallbackMap.remove(mUrl);
                addResourceToCache(mUrl, remoteResource);
                if (callbackList != null && callbackList.size() > 0) {
                    for (BackgroundTaskListener callback:callbackList) {
                        callback.completed(mUrl, remoteResource.getResource());
                    }
                }
            }
        });
    }

    /**
     * Should notify the result in the UI/Main thread.
     */
    private void notifyError(final String mUrl, final Exception e) {
        final ProviderException providerException =
                new ProviderException(e, getString(R.string.error_mesg_network));
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                //from UI thread and activity is always there
                List<BackgroundTaskListener> callbackList = mCallbackMap.remove(mUrl);
                if (callbackList != null && callbackList.size() > 0) {
                    for (BackgroundTaskListener callback:callbackList) {
                        callback.error(providerException);
                    }
                }
            }
        });
    }

    final class DownloaderTask implements Runnable {

        private String url;
        private RemoteResource remoteResource;

        public DownloaderTask(String url, RemoteResource remoteResource) {
            this.url = url;
            this.remoteResource = remoteResource;
        }

        @Override
        public void run() {
            try {
                remoteResource = HttpUtil.get(url, remoteResource);
                notifyDownloaded(url, remoteResource);
            } catch (IOException e) {
                notifyError(url, e);
                e.printStackTrace();
            }
        }


    }

}