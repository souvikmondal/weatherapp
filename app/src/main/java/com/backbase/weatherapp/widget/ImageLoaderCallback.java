package com.backbase.weatherapp.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.backbase.weatherapp.util.provider.IDownloadListener;

import java.lang.ref.WeakReference;

public class ImageLoaderCallback implements IDownloadListener<Bitmap> {

    private WeakReference<ImageView> imageViewWeakReference;
    public String url;
    private Activity activity;

    public ImageLoaderCallback(ImageView imageView, String url, Activity activity) {
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
        this.url = url;
        this.activity = activity;
    }

    @Override
    public void completed(String url, final Bitmap bitmap) {
        if (imageViewWeakReference != null) {
            final ImageView imageView = imageViewWeakReference.get();
            ImageLoaderCallback imageLoaderCallback = getImageLoaderCallback(imageView);
            if ((this == imageLoaderCallback)) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                });

            }
        }
    }

    @Override
    public void error(Exception ex) {

    }

    public static ImageLoaderCallback getImageLoaderCallback(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getImageLoaderCallback();
            }
        }
        return null;
    }

}