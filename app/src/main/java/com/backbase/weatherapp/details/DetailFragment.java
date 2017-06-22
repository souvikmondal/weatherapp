package com.backbase.weatherapp.details;


import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.databinding.FragmentDetailBinding;
import com.backbase.weatherapp.main.BaseFragment;
import com.backbase.weatherapp.main.ClimateDataController;
import com.backbase.weatherapp.main.MainActivity;
import com.backbase.weatherapp.main.OnActivityAttachListener;
import com.backbase.weatherapp.main.provider.ProviderException;
import com.backbase.weatherapp.model.weather.Climate;
import com.backbase.weatherapp.model.weather.Forecast;
import com.backbase.weatherapp.util.CONSTANT;
import com.backbase.weatherapp.util.DateUtil;
import com.backbase.weatherapp.main.BackgroundTaskListener;
import com.backbase.weatherapp.util.PreferenceUtil;
import com.backbase.weatherapp.main.provider.WeatherProvider;
import com.backbase.weatherapp.main.provider.types.ImageResource;
import com.backbase.weatherapp.widget.DownloadedDrawable;
import com.backbase.weatherapp.widget.ImageLoaderCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends BaseFragment implements BackgroundTaskListener {

    private DetailPresenter detailPresenter;
    private Map<String, List<Climate>> forecastMap;
    private FragmentDetailBinding binding;
    private TabLayout tabLayout;
    private Climate currentClimate;
    private TextView lastUpdatedTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler refreshHandler;



    public DetailFragment() {
    }

    /*
     * Set weather icon as per current weather status.
     */
    private void controlTodayWeatherColorScheme(Climate todayClimate) {

        ((ImageView)binding.getRoot().findViewById(R.id.statusimage))
                .setImageDrawable(getResources().getDrawable(
                        detailPresenter.getWeatherIconResource(todayClimate)));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //dont need action menu here
        setHasOptionsMenu(false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false);
        View view = binding.getRoot();

        init(view);

        //rely on the previous data
        //forecast will be updated always
        refreshCurrentClimate(false);

        return view;
    }

    private void startRefreshHandler() {
        if (refreshHandler == null) {
            refreshHandler = new Handler(Looper.getMainLooper());
        }
        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //make sure we have the activity context
                if (getActivity() != null) {
                    refreshCurrentClimate(true);
                }
            }
        }, ClimateDataController.REFRESH_FREQUENCY);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.title_detail));
    }

    private RecyclerView recyclerView;
    private ForecastAdapter forecastAdapter;

    private void init(View view) {
        forecastMap = new HashMap<>();
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        tabLayout = (TabLayout) view.findViewById(R.id.tab);
        detailPresenter = new DetailPresenter((AppCompatActivity) getActivity());

        lastUpdatedTextView = (TextView) view.findViewById(R.id.last_updated);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swip_refresh_layout);
    }

    private void refreshCurrentClimate(boolean force) {
        boolean forceRequired = ClimateDataController.getInstance().isTimeForRefresh() || force;
        if (forceRequired)
            ClimateDataController.getInstance().setLastRefreshTime();
        WeatherProvider.getInstance().retrieveWeather(
                ClimateDataController.getInstance().getSelectedCurrentCity().getLatLng(),
                PreferenceUtil.getUnitSystem(getActivity()), this, forceRequired,
                ((MainActivity)getActivity()).getDataController());

        //display rotating icon
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });


    }

    private void refreshForecast() {
        WeatherProvider.getInstance().retrieveForecast(
                ClimateDataController.getInstance().getSelectedCurrentCity().getLatLng(),
                PreferenceUtil.getUnitSystem(getActivity()), this, true,
                ((MainActivity)getActivity()).getDataController());

    }

    @Override
    public void completed(String key, final Object data) {
        getAttachedActivity(new OnActivityAttachListener() {
            @Override
            public void onAvailable(Activity activity) {
                if (data instanceof Climate) {
                    currentClimate = (Climate) data;
                    binding.setModel(detailPresenter.
                            getTodayWeatherBindingModel(currentClimate,
                                    ClimateDataController.getInstance().getSelectedCurrentCity()));
                    binding.executePendingBindings();
                    controlTodayWeatherColorScheme(currentClimate);

                    lastUpdatedTextView.setText(getResources().getText(R.string.lbl_last_update)
                            + DateUtil.formatTime(ClimateDataController.getInstance().getLastRefreshTime()));

                    refreshForecast();
                } else if (data instanceof Forecast) {
                    List<Climate> f = ((Forecast)data).getList();
                    populateForcastMap(f);
                    showDayList(DateUtil.getDayOfWeek(f.get(0).getDt()));
                    swipeRefreshLayout.setRefreshing(false);
                    tabLayout.getTabAt(0).select();
                }
            }
        });
    }

    @Override
    public void error(final ProviderException ex) {
        getAttachedActivity(new OnActivityAttachListener() {
            @Override
            public void onAvailable(Activity activity) {
                //start refreshing every 1 minute
                //even if the current result is error.
                startRefreshHandler();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getActivity(), ex.getUserMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (swipeRefreshLayout!=null) {
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.destroyDrawingCache();
            swipeRefreshLayout.clearAnimation();
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

        tabLayout.addTab(tabLayout.newTab().setText(day));
        String next = day;
        for (int i = 1; i < 5; i++) {
            next = DateUtil.getNextDay(next);
            tabLayout.addTab(tabLayout.newTab().setText(next));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showDayList(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.getTabAt(0).select();

    }

    private void populateForcastMap(List<Climate> forecast) {
        //start refreshing every 1 minute
        startRefreshHandler();
        forecastMap.clear();
        for (Climate climate:forecast) {
            String day = DateUtil.getDayOfWeek(climate.getDt());
            List<Climate> f = forecastMap.get(day);
            if (f == null) {
                f = new ArrayList<>();
                forecastMap.put(day, f);
            }
//            if (DateUtil.isAfter(climate.getDt(), currentClimate.getDt()))
            //api is not providing current timezone
                f.add(climate);
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
            forecastViewHolder.statusTextView.setText(climate.getWeather()[0].getDescription());
            forecastViewHolder.windTextView.setText(climate.getWind().getSpeed() +
                    PreferenceUtil.getSpeedUnit(getActivity()));
            forecastViewHolder.humidityTextView.setText(climate.getMain().getHumidity() + "%");
            forecastViewHolder.unitTextView.setText(PreferenceUtil.getTemparatureUnit(getActivity()));


            String u = CONSTANT.URL_WEATHER_ICON + climate.getWeather()[0].getIcon() + ".png";
            if (cancelPotentialDownload(u, forecastViewHolder.imageView)) {
                ImageLoaderCallback imageLoaderCallbackProfile =
                        new ImageLoaderCallback(forecastViewHolder.imageView, u, getActivity());
                DownloadedDrawable downloadedDrawableImageProfile =
                        new DownloadedDrawable(getResources(), imageLoaderCallbackProfile);
                forecastViewHolder.imageView.setImageDrawable(downloadedDrawableImageProfile);
                ImageResource imageResource = new ImageResource();
                ((MainActivity)getActivity()).getDataController()
                        .load(u, imageResource, imageLoaderCallbackProfile, false);
            }
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

    private boolean cancelPotentialDownload(String url, ImageView imageView) {
        ImageLoaderCallback imageLoaderCallback = ImageLoaderCallback.getImageLoaderCallback(imageView);

        if (imageLoaderCallback != null ) {
            if (!url.equals(imageLoaderCallback.url)) {
                ((MainActivity)getActivity()).getDataController().cancel(url, imageLoaderCallback);
            } else {
                //already being downloaded
                return false;
            }
        }
        return true;
    }

    class ForecastViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView tempTextView;
        public TextView timeTextView;
        public TextView statusTextView;
        public TextView windTextView;
        public TextView humidityTextView;
        public TextView unitTextView;

        public ForecastViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.weather_image);
            tempTextView = (TextView) itemView.findViewById(R.id.temp);
            timeTextView = (TextView) itemView.findViewById(R.id.time);
            statusTextView = (TextView) itemView.findViewById(R.id.desc);
            humidityTextView = (TextView) itemView.findViewById(R.id.humidity);
            windTextView = (TextView) itemView.findViewById(R.id.wind);
            unitTextView = (TextView) itemView.findViewById(R.id.unit);
        }
    }
}
