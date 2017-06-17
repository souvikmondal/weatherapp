package com.backbase.weatherapp.widget;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

public class DownloadedDrawable extends BitmapDrawable {

    private WeakReference<ImageLoaderCallback> imageLoaderCallbackWeakReference;


    public DownloadedDrawable(Resources res, ImageLoaderCallback callback) {
        super(res, (Bitmap) null);
        imageLoaderCallbackWeakReference =
                new WeakReference<ImageLoaderCallback>(callback);
    }

    public ImageLoaderCallback getImageLoaderCallback() {
        return imageLoaderCallbackWeakReference.get();
    }




}