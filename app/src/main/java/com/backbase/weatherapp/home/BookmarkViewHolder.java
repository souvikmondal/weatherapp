package com.backbase.weatherapp.home;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.main.BackgroundTaskListener;
import com.backbase.weatherapp.main.BaseFragment;
import com.backbase.weatherapp.main.ClimateDataController;
import com.backbase.weatherapp.main.MainActivity;
import com.backbase.weatherapp.main.OnActivityAttachListener;
import com.backbase.weatherapp.main.provider.AppDataProvider;
import com.backbase.weatherapp.main.provider.ProviderException;
import com.backbase.weatherapp.main.provider.WeatherProvider;
import com.backbase.weatherapp.main.provider.types.ImageResource;
import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.model.weather.Climate;
import com.backbase.weatherapp.util.CONSTANT;
import com.backbase.weatherapp.util.PreferenceUtil;
import com.backbase.weatherapp.widget.DownloadedDrawable;
import com.backbase.weatherapp.widget.ImageLoaderCallback;

/**
 * Created by Souvik on 18/06/17.
 */

public class BookmarkViewHolder implements View.OnClickListener, BackgroundTaskListener<Climate> {

    private TextView cityDescTv;
    private TextView tempTextView;
    private TextView unitTextView;
    private ImageView imageView;
    private View parentView;
    private City city;
    private BaseFragment parent;


    public BookmarkViewHolder(View parent, BaseFragment parentFragment) {
        this.parentView = parent;
        this.cityDescTv = (TextView) parentView.findViewById(R.id.city_name);
        this.tempTextView = (TextView) parentView.findViewById(R.id.temp);
        this.unitTextView = (TextView) parentView.findViewById(R.id.unit);
        this.imageView = (ImageView) parentView.findViewById(R.id.image);
        this.parent = parentFragment;
    }

    public void update(final City city) {
        this.city = city;
        this.cityDescTv.setText(city.getDesc());
        this.tempTextView.setTag(city);
        AppDataProvider appDataProvider = ((MainActivity)parent
                .getActivity()).getDataController();
        WeatherProvider.getInstance().retrieveWeather(
                city.getLatLng(), PreferenceUtil
                .getUnitSystem(appDataProvider.getActivity()),
                this, ClimateDataController.getInstance().isTimeForRefresh(),
                appDataProvider);
    }

    @Override
    public void onClick(View v) {

    }

    private boolean cancelPotentialDownload(String url, ImageView imageView) {
        ImageLoaderCallback imageLoaderCallback = ImageLoaderCallback.getImageLoaderCallback(imageView);

        if (imageLoaderCallback != null ) {
            if (!url.equals(imageLoaderCallback.url)) {
                ((MainActivity)parent.getActivity()).getDataController().cancel(url, imageLoaderCallback);
            } else {
                //already being downloaded
                return false;
            }
        }
        return true;
    }

    @Override
    public void completed(final String key, final Climate data) {

        parent.getAttachedActivity(new OnActivityAttachListener() {
            @Override
            public void onAvailable(Activity activity) {
                if (data != null && (tempTextView.getTag() != null
                        && tempTextView.getTag().equals(city))) {

                    String u = CONSTANT.URL_WEATHER_ICON + data.getWeather()[0].getIcon() + ".png";
                    tempTextView.setText(((int)data.getMain().getTemp()) + "");
                    unitTextView.setText(PreferenceUtil.getTemparatureUnit(parent.getActivity()));
                    if (cancelPotentialDownload(u, imageView)) {
                        ImageLoaderCallback imageLoaderCallbackProfile =
                                new ImageLoaderCallback(imageView, u, parent.getActivity());
                        DownloadedDrawable downloadedDrawableImageProfile =
                                new DownloadedDrawable(parent.getResources(), imageLoaderCallbackProfile);
                        imageView.setImageDrawable(downloadedDrawableImageProfile);
                        ImageResource imageResource = new ImageResource();
                        ((MainActivity)parent.getActivity()).getDataController()
                                .load(u, imageResource, imageLoaderCallbackProfile, false);
                    }
                }
            }
        });
    }

    @Override
    public void error(ProviderException ex) {
        //ignore
    }
}
