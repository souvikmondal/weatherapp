package com.backbase.weatherapp.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Souvik on 16/06/17.
 */

public final class ThreadPool {

    private static ThreadPool instance;
    private static Lock lock = new ReentrantLock();

    private ThreadPoolExecutor threadPoolExecutor;

    private ThreadPool() {
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    public static final ThreadPool getInstance() {
        lock.lock();
        try {
            if (instance == null)
                instance = new ThreadPool();
            return instance;
        } finally {
            lock.unlock();
        }

    }

    public void execute(Runnable runnable) {
        threadPoolExecutor.execute(runnable);
    }



}
