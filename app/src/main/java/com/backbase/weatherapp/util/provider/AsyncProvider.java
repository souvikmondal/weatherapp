package com.backbase.weatherapp.util.provider;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Souvik on 16/06/17.
 */

public final class AsyncProvider {

    private static AsyncProvider instance;
    private static Lock lock = new ReentrantLock();

    private ThreadPoolExecutor threadPoolExecutor;

    private AsyncProvider() {
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
    }

    public static final AsyncProvider getInstance() {
        lock.lock();
        if (instance == null)
            instance = new AsyncProvider();
        lock.unlock();
        return instance;
    }

    public void execute(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }



}
