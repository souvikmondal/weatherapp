package com.backbase.weatherapp.main;

import com.backbase.weatherapp.main.provider.ProviderException;

/**
 * Created by Souvik on 17/06/17.
 */

public interface BackgroundTaskListener<T> {

    public void completed(String key, T data);

    public void error(ProviderException ex);

}
