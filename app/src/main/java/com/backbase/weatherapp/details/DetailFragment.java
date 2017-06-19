package com.backbase.weatherapp.details;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.databinding.WeatherTodayLayoutBinding;
import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.model.weather.Climate;
import com.backbase.weatherapp.util.provider.IDownloadListener;
import com.backbase.weatherapp.util.provider.WeatherProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements IDownloadListener{

    private View customToolBar;
    private WeatherTodayLayoutBinding binding;
    private City city;
    private DetailPresenter detailPresenter;

    public DetailFragment() {
    }

    public void setCity(City city) {
        this.city = city;
    }

    private void controlTodayWeatherColorScheme(Climate todayClimate) {

        ((ImageView)customToolBar.findViewById(R.id.statusimage))
                .setImageDrawable(getResources().getDrawable(
                        detailPresenter.getWeatherIconResource(todayClimate)));



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        detailPresenter = new DetailPresenter((AppCompatActivity) context);
        setCustomToolBarForFragment();
        WeatherProvider.getInstance().retrieveWeather(city.getLatLng(), "metric", this, false);
    }

    private void setCustomToolBarForFragment() {
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appBar);

        binding = DataBindingUtil.inflate(getActivity().
                getLayoutInflater(), R.layout.weather_today_layout, null, false);
        binding.setModel(detailPresenter.getInitialWeatherBindingModel(city));

        // set CustomView
        customToolBar = binding.getRoot();

        appBarLayout.addView(customToolBar);
    }

    private void removeCustomToolbar() {
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appBar);

        appBarLayout.removeView(customToolBar);
    }

    @Override
    public void onStop() {
        super.onStop();
        removeCustomToolbar();
    }

    @Override
    public void completed(String key, Object data) {
        if (data instanceof Climate) {
            Climate c = (Climate) data;
            c.setName(city.getDesc());
            binding.setModel(detailPresenter.getTodayWeatherBindingModel(c));
            binding.executePendingBindings();
        }
    }
}
