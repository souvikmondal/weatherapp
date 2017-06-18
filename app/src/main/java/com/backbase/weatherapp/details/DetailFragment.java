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
import com.backbase.weatherapp.model.weather.Climate;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    View customToolBar;
    private Climate todayClimate;
    private DetailPresenter detailPresenter;

    public DetailFragment() {
    }

    public void setTodayClimate(Climate climate) {
        todayClimate = climate;
    }

    private void controlTodayWeatherColorScheme() {

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
        controlTodayWeatherColorScheme();
    }

    private void setCustomToolBarForFragment() {
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appBar);

        Toolbar toolbar = (Toolbar) appBarLayout.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorWeatherBackgroundClearDay));
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWeatherTextClearDay));
        toolbar.getOverflowIcon().setColorFilter(
                getResources().getColor(R.color.colorWeatherTextClearDay),
                PorterDuff.Mode.SRC_ATOP);
        toolbar.getNavigationIcon().setColorFilter(
                getResources().getColor(R.color.colorWeatherTextClearDay),
                PorterDuff.Mode.SRC_ATOP);

        WeatherTodayLayoutBinding binding = DataBindingUtil.inflate(getActivity().
                getLayoutInflater(), R.layout.weather_today_layout, null, false);
        binding.setModel(detailPresenter.getTodayWeatherBindingModel(todayClimate));

        // set CustomView
        customToolBar = binding.getRoot();

        appBarLayout.addView(customToolBar);
    }

    private void removeCustomToolbar() {
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appBar);

        Toolbar toolbar = (Toolbar) appBarLayout.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryText));

        toolbar.getOverflowIcon().setColorFilter(
                getResources().getColor(R.color.colorPrimaryText),
                PorterDuff.Mode.SRC_ATOP);
        toolbar.getNavigationIcon().setColorFilter(
                getResources().getColor(R.color.colorPrimaryText),
                PorterDuff.Mode.SRC_ATOP);

        appBarLayout.removeView(customToolBar);
    }

    @Override
    public void onStop() {
        super.onStop();
        removeCustomToolbar();
    }
}
