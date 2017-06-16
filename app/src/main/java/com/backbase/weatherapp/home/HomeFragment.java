package com.backbase.weatherapp.home;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.util.db.DBHelper;
import com.backbase.weatherapp.util.db.dao.CityDao;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends BottomSheetDialogFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int ID_CITY_LOADER = 1;

    private ListView recyclerView;
    private CityAdapter cityAdapter;

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
        getLoaderManager().initLoader(ID_CITY_LOADER, null, this);
    }

    private void initControls(View parent) {
        recyclerView = (ListView) parent.findViewById(R.id.rv_cities);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return new FavCityLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        cityAdapter = new CityAdapter(getActivity(), data);
        recyclerView.setAdapter(cityAdapter);
    }

    @Override
    public void onLoaderReset(Loader loader) {

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
                view.setTag(viewHolder);
            }
            viewHolder.cityDescTv.setText(CityDao.getCityName(cursor));
        }
    }

    private class CityViewHolder {

        public TextView cityDescTv;

    }

    class FavCityLoader extends AsyncTaskLoader<Cursor> {

        public FavCityLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            CityDao cityDao = new CityDao(DBHelper.getInstance(getActivity()));
            return cityDao.getCitiesCursor();
        }
    }
}
