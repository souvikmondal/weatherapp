package com.backbase.weatherapp.home;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.main.MainActivity;
import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.model.weather.Climate;
import com.backbase.weatherapp.model.weather.Main;
import com.backbase.weatherapp.util.provider.IDownloadListener;
import com.backbase.weatherapp.util.provider.ResourceProvider;
import com.backbase.weatherapp.util.provider.WeatherProvider;
import com.backbase.weatherapp.util.provider.types.ImageResource;
import com.backbase.weatherapp.widget.DownloadedDrawable;
import com.backbase.weatherapp.widget.ImageLoaderCallback;

/**
 * Created by Souvik on 18/06/17.
 */

public class FavListRowController implements View.OnClickListener, IDownloadListener<Climate>{

    private static final String URL = "http://openweathermap.org/img/w/";

    private TextView cityDescTv;
    private TextView tempTextView;
    private ImageView imageView;
    private View parentView;
    private City city;
    private Fragment parent;


    public FavListRowController(View parent, Fragment parentFragment) {
        this.parentView = parent;
        this.cityDescTv = (TextView) parentView.findViewById(R.id.city_name);
        this.tempTextView = (TextView) parentView.findViewById(R.id.temp);
        this.imageView = (ImageView) parentView.findViewById(R.id.image);
        this.parent = parentFragment;
    }

    public void update(final City city) {
        this.city = city;
        this.cityDescTv.setText(city.getDesc());
        this.tempTextView.setTag(city);
        WeatherProvider.getInstance().retrieveWeather(
                city.getLatLng(), "metric", this, true,
                ((MainActivity)parent.getActivity()).getResourceProvider());
        parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = parent.getActivity();
                if (activity != null) {
                    ((MainActivity)activity).showDetails(city);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    private boolean cancelPotentialDownload(String url, ImageView imageView) {
        ImageLoaderCallback imageLoaderCallback = ImageLoaderCallback.getImageLoaderCallback(imageView);

        if (imageLoaderCallback != null ) {
            if (!url.equals(imageLoaderCallback.url)) {
                ((MainActivity)parent.getActivity()).getResourceProvider().cancel(url, imageLoaderCallback);
            } else {
                //already being downloaded
                return false;
            }
        }
        return true;
    }

    @Override
    public void completed(final String key, final Climate data) {

        if (data != null && (tempTextView.getTag() != null
                && tempTextView.getTag().equals(city))) {

            String u = URL + data.getWeather()[0].getIcon() + ".png";
            tempTextView.setText(((int)data.getMain().getTemp()) + "");
            if (cancelPotentialDownload(u, imageView)) {
                ImageLoaderCallback imageLoaderCallbackProfile =
                        new ImageLoaderCallback(imageView, u, parent.getActivity());
                DownloadedDrawable downloadedDrawableImageProfile =
                        new DownloadedDrawable(parent.getResources(), imageLoaderCallbackProfile);
                imageView.setImageDrawable(downloadedDrawableImageProfile);
                ImageResource imageResource = new ImageResource();
                ((MainActivity)parent.getActivity()).getResourceProvider()
                        .load(u, imageResource, imageLoaderCallbackProfile, false);
            }
        }

    }
}
