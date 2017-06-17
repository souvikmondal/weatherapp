package com.backbase.weatherapp.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

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
public final class ImageProvider {

    private static ImageProvider instance = new ImageProvider();

    public static ImageProvider getInstance() {
        return instance;
    }

    private LruCache<String, Bitmap> mResourceCache;
    private Map<String, List<IDownloadListener>> mCallbackMap;
    private Lock lock;

    private ImageProvider() {
        initResourceCache();
        mCallbackMap = new WeakHashMap<>();
        lock = new ReentrantLock();
    }


    public void load(String url, IDownloadListener callback, boolean force) {
        lock.lock();
        try {
            boolean started = addCallbackToMap(url, callback);
            Bitmap cachedRes = getResourceFromCache(url);
            if ((cachedRes == null && !started) || force) {//force download
                AsyncProvider.getInstance().execute(new DownloaderTask(url));
            } else if (cachedRes != null) {// finished
                callback.completed(url, cachedRes);
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
        mResourceCache = new LruCache<String, Bitmap>(computeCacheSize()) {
            @Override
            protected int sizeOf(String key, Bitmap remoteResource) {
                return (remoteResource.getByteCount() / 1024) / 1024;
            }
        };
    }

    private int computeCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 16;
        return cacheSize;
    }

    private void addResourceToCache(String url, Bitmap remoteResource) {
        if (remoteResource != null) {
            mResourceCache.put(url, remoteResource);
        }
    }

    private Bitmap getResourceFromCache(String url) {
        final Bitmap remoteResource = mResourceCache.get(url);
        return remoteResource;
    }

    private void notifyDownloaded(String mUrl, Bitmap bitmap) {
        lock.lock();
        try {
            List<IDownloadListener> callbackList = mCallbackMap.remove(mUrl);
            addResourceToCache(mUrl, bitmap);
            if (callbackList != null && callbackList.size() > 0) {
                for (IDownloadListener callback:callbackList) {
                    callback.completed(mUrl, bitmap);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    final class DownloaderTask implements Runnable {

        private String mUrl;

        public DownloaderTask(String url) {
            this.mUrl = url;
        }

        @Override
        public void run() {
            try {
                Bitmap bitmap = HttpUtil.getBitmap(mUrl);
                notifyDownloaded(mUrl, bitmap);
            } catch (IOException e) {
                notifyDownloaded(mUrl, null);
                e.printStackTrace();
            }
        }


    }

}