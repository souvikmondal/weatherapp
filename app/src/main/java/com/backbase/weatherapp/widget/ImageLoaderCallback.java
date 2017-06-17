package com.backbase.weatherapp.widget;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.backbase.weatherapp.util.IDownloadListener;

import java.lang.ref.WeakReference;

public class ImageLoaderCallback implements IDownloadListener<Bitmap> {

    private WeakReference<ImageView> imageViewWeakReference;
    public String url;

    public ImageLoaderCallback(ImageView imageView, String url) {
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
        this.url = url;
    }

    @Override
    public void completed(String url, Bitmap bitmap) {
        if (imageViewWeakReference != null) {
            ImageView imageView = imageViewWeakReference.get();
            ImageLoaderCallback imageLoaderCallback = getImageLoaderCallback(imageView);
            if ((this == imageLoaderCallback)) {
                imageView.setImageBitmap(bitmap);
            }
        }
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