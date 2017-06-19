package com.backbase.weatherapp.util.provider;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;

import com.backbase.weatherapp.util.HttpUtil;
import com.backbase.weatherapp.util.provider.types.RemoteResource;

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
public final class ResourceProvider extends Fragment{

    private LruCache<String, RemoteResource> mResourceCache;
    private Map<String, List<IDownloadListener>> mCallbackMap;
    private Lock lock;

    public ResourceProvider() {

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

    public void load(@NonNull String url, @NonNull RemoteResource remoteResource,
                     @NonNull IDownloadListener callback, boolean force) {
        lock.lock();
        try {
            boolean started = addCallbackToMap(url, callback);
            RemoteResource cachedRes = getResourceFromCache(url);
            if ((cachedRes == null && !started) || force) {//force download
                AsyncProvider.getInstance().execute(new DownloaderTask(url, remoteResource));
            } else if (cachedRes != null) {// finished
                callback.completed(url, cachedRes.getResource());
            }

        } finally {
            lock.unlock();
        }
    }

    public void cancel(String url, IDownloadListener callback) {
            List<IDownloadListener> callbackList = getCallbacks(url);
            if (callbackList != null && callbackList.size() > 0) {
                callbackList.remove(callback);
            }
    }

    public void releaseCache() {
        mResourceCache.evictAll();
    }

    private boolean addCallbackToMap(String url, IDownloadListener callback) {
        boolean callbackExist = true;
        List<IDownloadListener> callbackList = mCallbackMap.get(url);
        if (callbackList == null) {
            callbackList = new ArrayList<>();
            mCallbackMap.put(url, callbackList);
            callbackExist = false;
        }
        callbackList.add(callback);
        return callbackExist;
    }

    private List<IDownloadListener> getCallbacks(String url) {
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

    /**
     * Should notify the result in the UI/Main thread.
     */
    private void notifyDownloaded(final String mUrl, final RemoteResource remoteResource) {
        lock.lock();
        try {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //from UI thread and activity is always there
                    List<IDownloadListener> callbackList = mCallbackMap.remove(mUrl);
                    addResourceToCache(mUrl, remoteResource);
                    if (callbackList != null && callbackList.size() > 0) {
                        for (IDownloadListener callback:callbackList) {
                            callback.completed(mUrl, remoteResource.getResource());
                        }
                    }
                }
            });

        } finally {
            lock.unlock();
        }
    }

    final class DownloaderTask implements Runnable {

        private String mUrl;
        private RemoteResource remoteResource;

        public DownloaderTask(String url, RemoteResource remoteResource) {
            this.mUrl = url;
            this.remoteResource = remoteResource;
        }

        @Override
        public void run() {
            try {
                remoteResource = HttpUtil.get(mUrl, remoteResource);
                notifyDownloaded(mUrl, remoteResource);
            } catch (IOException e) {
                notifyDownloaded(mUrl, null);
                e.printStackTrace();
            }
        }


    }

}