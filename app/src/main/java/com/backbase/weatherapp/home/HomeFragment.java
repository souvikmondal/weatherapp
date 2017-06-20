package com.backbase.weatherapp.home;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.main.MainActivity;
import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.util.db.dao.CityDao;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends BottomSheetDialogFragment {



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

        favButton = (FloatingActionButton) parent.findViewById(R.id.fab);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showMap();
            }
        });
    }

    public void notifyDataSetChange() {
        Cursor cursor = homePresenter.getFavCities();
        cityAdapter.swapCursor(cursor);
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
            FavListRowController viewHolder = (FavListRowController) view.getTag();
            if (viewHolder == null) {
                viewHolder = new FavListRowController(view, HomeFragment.this);
                view.setTag(viewHolder);
            }

            final City city = CityDao.getCity(cursor);
            viewHolder.update(city);

            Log.d("com.backbase.weatherapp", "bindView");
        }
    }

}
