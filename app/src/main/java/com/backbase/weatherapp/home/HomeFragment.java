package com.backbase.weatherapp.home;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.main.BaseFragment;
import com.backbase.weatherapp.main.ClimateDataController;
import com.backbase.weatherapp.main.MainActivity;
import com.backbase.weatherapp.main.OnActivityAttachListener;
import com.backbase.weatherapp.main.provider.ProviderException;
import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.util.CommonLocation;
import com.backbase.weatherapp.db.dao.CityDao;
import com.backbase.weatherapp.main.BackgroundTaskListener;
import com.backbase.weatherapp.main.provider.AppDataProvider;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeFragment extends BaseFragment {



    private static final int ID_CITY_LOADER = 1;

    private ListView recyclerView;
    private CityAdapter cityAdapter;
    private HomePresenter homePresenter;
    private FloatingActionButton favButton;
    private AutoCompleteTextView autoCompleteTextView;
    private Handler refreshHandler;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initControls(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        homePresenter = new HomePresenter(getActivity());

        AppDataProvider appDataProvider = ((MainActivity)getActivity()).getDataController();
        appDataProvider.loadBookmarkedCities(new BackgroundTaskListener<Cursor>() {
            @Override
            public void completed(String key, Cursor data) {
                cityAdapter = new CityAdapter(getActivity(), data);
                recyclerView.setAdapter(cityAdapter);
            }

            @Override
            public void error(ProviderException ex) {

            }
        }, appDataProvider.getActivity());

        ((MainActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.title_home));

        startRefreshHandler();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.fav_list_main, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_delete:
                MainActivity mainActivity = (MainActivity) getActivity();
                AppDataProvider appDataProvider = mainActivity.getDataController();
                appDataProvider.deleteCity(new BackgroundTaskListener<Cursor>() {
                    @Override
                    public void completed(String key, Cursor data) {
                        notifyDataSetChange();
                    }

                    @Override
                    public void error(ProviderException ex) {

                    }
                }, mainActivity, (City) cityAdapter.getItem(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            ((MainActivity)getActivity()).showSettings();
            return true;
        }

        else if (id == R.id.action_help) {
            ((MainActivity)getActivity()).showHelp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startRefreshHandler() {
        if (refreshHandler == null) {
            refreshHandler = new Handler(Looper.getMainLooper());
        }
        refreshHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (cityAdapter != null)
                    cityAdapter.notifyDataSetChanged();
            }
        }, ClimateDataController.REFRESH_FREQUENCY);
    }

    private void initControls(View parent) {
        recyclerView = (ListView) parent.findViewById(R.id.rv_cities);
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City city = (City) cityAdapter.getItem(position);
                ((MainActivity)getActivity()).showDetails(city);
            }
        });
        registerForContextMenu(recyclerView);

        favButton = (FloatingActionButton) parent.findViewById(R.id.fab);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showMap();
            }
        });

        final String[] values = CommonLocation.getAllCities();
        autoCompleteTextView = (AutoCompleteTextView) parent.findViewById(R.id.autocomplete);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, values);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                homePresenter.saveCityAsFav(((TextView)view).getText().toString(),
                        CommonLocation.getLocation(((TextView)view).getText().toString()), new BackgroundTaskListener() {
                            @Override
                            public void completed(String key, Object data) {
                                Activity activity = getActivity();
                                if (activity != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            autoCompleteTextView.setText("");
                                            notifyDataSetChange();
                                            Toast.makeText(getActivity(), "City bookmarked", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void error(ProviderException ex) {

                            }
                        });

            }
        });
    }

    public void notifyDataSetChange() {
        AppDataProvider appDataProvider = ((MainActivity)getActivity()).getDataController();
        appDataProvider.loadBookmarkedCities(new BackgroundTaskListener<Cursor>() {
            @Override
            public void completed(String key, Cursor data) {
                cityAdapter.swapCursor(data);
                cityAdapter.notifyDataSetChanged();
            }

            @Override
            public void error(ProviderException ex) {

            }
        }, appDataProvider.getActivity());
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

            BookmarkViewHolder viewHolder = (BookmarkViewHolder) view.getTag();
            if (viewHolder == null) {
                viewHolder = new BookmarkViewHolder(view, HomeFragment.this);
                view.setTag(viewHolder);
            }

            final City city = CityDao.getCity(cursor);
            viewHolder.update(city);

            Log.d("com.backbase.weatherapp", "bindView");
        }

        public void refresh() {
            notifyDataSetChange();
        }

        @Override
        public Object getItem(int position) {
            Cursor cursor = getCursor();
            cursor.moveToPosition(position);
            return CityDao.getCity(cursor);
        }
    }

}
