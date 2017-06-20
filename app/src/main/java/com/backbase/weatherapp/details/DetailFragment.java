package com.backbase.weatherapp.details;


import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.databinding.FragmentDetailBinding;
import com.backbase.weatherapp.databinding.WeatherTodayLayoutBinding;
import com.backbase.weatherapp.main.MainActivity;
import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.model.weather.Climate;
import com.backbase.weatherapp.model.weather.Forecast;
import com.backbase.weatherapp.util.DateUtil;
import com.backbase.weatherapp.util.provider.IDownloadListener;
import com.backbase.weatherapp.util.provider.WeatherProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements IDownloadListener{

    private DetailPresenter detailPresenter;
    private Map<String, List<Climate>> forecastMap;
    private Button first;
    private Button second;
    private Button third;
    private Button fourth;
    private Button fifth;
    private FragmentDetailBinding binding;

    public DetailFragment() {
    }

    private void controlTodayWeatherColorScheme(Climate todayClimate) {

        ((ImageView)binding.getRoot().findViewById(R.id.statusimage))
                .setImageDrawable(getResources().getDrawable(
                        detailPresenter.getWeatherIconResource(todayClimate)));



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        View view = binding.getRoot();

        init(view);
        refreshData();
        return view;
    }

    private RecyclerView recyclerView;
    private ForecastAdapter forecastAdapter;
    private ProgressBar progressBar;

    private void init(View view) {
        forecastMap = new HashMap<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false));
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        first = (Button) view.findViewById(R.id.first);
        second = (Button) view.findViewById(R.id.second);
        third = (Button) view.findViewById(R.id.third);
        fourth = (Button) view.findViewById(R.id.fourth);
        fifth = (Button) view.findViewById(R.id.fifth);

    }

    private void refreshData() {
        boolean force = ((MainActivity)getActivity()).isRefreshRequired();
        if (force)
            ((MainActivity)getActivity()).setLastRefreshTime();
        progressBar.setVisibility(View.VISIBLE);
        detailPresenter = new DetailPresenter((AppCompatActivity) getActivity());
        WeatherProvider.getInstance().retrieveWeather(
                ((MainActivity)getActivity()).getSelectedCity().getLatLng(), "metric", this, force,
                ((MainActivity)getActivity()).getResourceProvider());
        WeatherProvider.getInstance().retrieveForecast(
                ((MainActivity)getActivity()).getSelectedCity().getLatLng(), "metric", this, force,
                ((MainActivity)getActivity()).getResourceProvider());
    }

    class TabBtnOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            v.setSelected(true);
            showDayList((String) v.getTag());
        }
    }

    @Override
    public void completed(String key, final Object data) {
        MainActivity mainActivity = ((MainActivity)getActivity());
        if (mainActivity != null) {
            if (data instanceof Climate) {
                Climate c = (Climate) data;
                binding.setModel(detailPresenter.
                        getTodayWeatherBindingModel(c,mainActivity
                        .getSelectedCity()));
                binding.executePendingBindings();
                controlTodayWeatherColorScheme(c);
            } else if (data instanceof Forecast) {
                List<Climate> f = ((Forecast)data).getList();
                populateForcastMap(f);
                showDayList(DateUtil.getDayOfWeek(f.get(0).getDt()));
                progressBar.setVisibility(View.GONE);
            }
        }

    }

    private void showDayList(String day) {
        if (forecastAdapter == null) {
            forecastAdapter = new ForecastAdapter(forecastMap.get(day));
            recyclerView.setAdapter(forecastAdapter);

            initTabBtn(day);
        } else {
            forecastAdapter.setItems(forecastMap.get(day));
            forecastAdapter.notifyDataSetChanged();
        }
    }

    private void initTabBtn(String day) {
        TabBtnOnClickListener listener = new TabBtnOnClickListener();
        first.setText("Today");
        first.setTag(day);
        first.setOnClickListener(listener);
        String next = DateUtil.getNextDay(day);
        second.setTag(next);
        second.setText(next);
        second.setOnClickListener(listener);
        next = DateUtil.getNextDay(next);
        third.setTag(next);
        third.setText(next);
        third.setOnClickListener(listener);
        next = DateUtil.getNextDay(next);
        fourth.setTag(next);
        fourth.setText(next);
        fourth.setOnClickListener(listener);
        next = DateUtil.getNextDay(next);
        fifth.setTag(next);
        fifth.setText(next);
        fifth.setOnClickListener(listener);
    }

    private void populateForcastMap(List<Climate> forecast) {
        for (Climate climate:forecast) {
            String day = DateUtil.getDayOfWeek(climate.getDt());
            List<Climate> f = forecastMap.get(day);
            if (f == null) {
                f = new ArrayList<>();
                forecastMap.put(day, f);
            }
            if (DateUtil.isAfter(climate.getDt()))
                f.add(climate);
        }
    }

    @Override
    public void error(Exception ex) {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    class ForecastAdapter extends RecyclerView.Adapter {

        private List<Climate> climateList;

        public ForecastAdapter(List<Climate> climateList) {
            this.climateList = climateList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.forecast_layout, parent, false);
            return new ForecastViewHolder(view) ;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Climate climate = getItemAt(position);
            ForecastViewHolder forecastViewHolder = (ForecastViewHolder)holder;
            forecastViewHolder.tempTextView.setText(String.valueOf((int)climate.getMain().getTemp()));
            forecastViewHolder.timeTextView.setText(DateUtil.getTimeOfDay(climate.getDt()));
        }

        @Override
        public int getItemCount() {
            return climateList.size();
        }

        private Climate getItemAt(int position) {
            return climateList.get(position);
        }

        public void setItems(List<Climate> climateList) {
            this.climateList = climateList;
            notifyDataSetChanged();
        }
    }

    class ForecastViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView tempTextView;
        public TextView timeTextView;

        public ForecastViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.weather_image);
            tempTextView = (TextView) itemView.findViewById(R.id.temp);
            timeTextView = (TextView) itemView.findViewById(R.id.time);
        }
    }
}
