package com.backbase.weatherapp.main.provider.types;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by souvik on 7/10/2016.
 */
public interface RemoteResource<ResourceType> {

    public void prepare(InputStream inputStream) throws IOException;
    public ResourceType getResource();
    public int size();

}