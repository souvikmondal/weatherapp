package com.backbase.weatherapp.home;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.main.IFavAddedListener;
import com.backbase.weatherapp.main.IMainActivityListener;
import com.backbase.weatherapp.main.MainActivity;
import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.model.weather.Climate;
import com.backbase.weatherapp.util.IDownloadListener;
import com.backbase.weatherapp.util.ImageProvider;
import com.backbase.weatherapp.util.WeatherProvider;
import com.backbase.weatherapp.util.db.dao.CityDao;
import com.backbase.weatherapp.widget.DownloadedDrawable;
import com.backbase.weatherapp.widget.ImageLoaderCallback;
import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends BottomSheetDialogFragment implements IFavAddedListener {

    private static final String URL = "http://openweathermap.org/img/w/";

    private static final int ID_CITY_LOADER = 1;

    private ListView recyclerView;
    private CityAdapter cityAdapter;
    private HomePresenter homePresenter;
    private FloatingActionButton favButton;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initControls(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homePresenter = new HomePresenter(getActivity());
        Cursor cursor = homePresenter.getFavCities();
        cityAdapter = new CityAdapter(getActivity(), cursor);
        recyclerView.setAdapter(cityAdapter);
    }

    private void initControls(View parent) {
        recyclerView = (ListView) parent.findViewById(R.id.rv_cities);
//        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ((IMainActivityListener)getActivity()).showDetails();
//            }
//        });
        favButton = (FloatingActionButton) parent.findViewById(R.id.fab);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IMainActivityListener)getActivity()).showMap();
            }
        });
    }

    @Override
    public void onAdded(City city) {
        Cursor cursor = homePresenter.getFavCities();
        cityAdapter.swapCursor(cursor);
        cityAdapter.notifyDataSetChanged();
    }

    private class CityAdapter extends CursorAdapter {

        public CityAdapter(Context context, Cursor cursor) {
            super(context, cursor, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_city_row, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            CityViewHolder viewHolder = (CityViewHolder) view.getTag();
            if (viewHolder == null) {
                viewHolder = new CityViewHolder();
                viewHolder.cityDescTv = (TextView) view.findViewById(R.id.city_name);
                viewHolder.tempTextView = (TextView) view.findViewById(R.id.temp);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.image);
                viewHolder.parentView = view;
                view.setTag(viewHolder);
            }
            final City city = CityDao.getCity(cursor);
            viewHolder.cityDescTv.setText(city.getDesc());
            DownloadListener listener = new DownloadListener(viewHolder);
            viewHolder.weakReference = new WeakReference<DownloadListener>(listener);
            WeatherProvider.retrieveWeather(city.getLatLng(), "metric", listener);

            Log.d("com.backbase.weatherapp", "bindView");
        }
    }

    class DownloadListener implements IDownloadListener<Climate> {

        CityViewHolder holder;

        DownloadListener(CityViewHolder holder) {
            this.holder = holder;
        }

        @Override
        public void completed(String url, final Climate data) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (data != null && holder.weakReference.get() == DownloadListener.this) {

                        holder.parentView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((IMainActivityListener)getActivity()).showDetails(data);
                            }
                        });

                        String u = URL + data.getWeather()[0].getIcon() + ".png";
                        holder.tempTextView.setText(((int)data.getMain().getTemp()) + "");
                        if (cancelPotentialDownload(u, holder.imageView)) {
                            ImageLoaderCallback imageLoaderCallbackProfile = new ImageLoaderCallback(holder.imageView, u, getActivity());
                            DownloadedDrawable downloadedDrawableImageProfile = new DownloadedDrawable(getResources(), imageLoaderCallbackProfile);
                            holder.imageView.setImageDrawable(downloadedDrawableImageProfile);
                            ImageProvider.getInstance().load(u, imageLoaderCallbackProfile, false);
                        }
                    }
                }
            });
        }
    }

    private class CityViewHolder {

        public TextView cityDescTv;
        public TextView tempTextView;
        public ImageView imageView;
        public View parentView;

        WeakReference<DownloadListener> weakReference;

    }

    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        ImageLoaderCallback imageLoaderCallback = ImageLoaderCallback.getImageLoaderCallback(imageView);

        if (imageLoaderCallback != null ) {
            if (!url.equals(imageLoaderCallback.url)) {
                ImageProvider.getInstance().cancel(url, imageLoaderCallback);
            } else {
                //already being downloaded
                return false;
            }
        }
        return true;
    }


}
