package com.backbase.weatherapp.util;

/**
 * Created by Souvik on 17/06/17.
 */

public interface IDownloadListener<T> {

    public void completed(String key, T data);

}
