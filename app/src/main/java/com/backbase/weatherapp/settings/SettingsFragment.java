package com.backbase.weatherapp.settings;


import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.main.ClimateDataController;
import com.backbase.weatherapp.main.MainActivity;
import com.backbase.weatherapp.main.BackgroundTaskListener;
import com.backbase.weatherapp.main.provider.ProviderException;
import com.backbase.weatherapp.util.PreferenceUtil;
import com.backbase.weatherapp.main.provider.AppDataProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private Button resetBtn;


    public SettingsFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        init(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.title_settings));
    }

    private void init(View parent) {

        resetBtn = (Button) parent.findViewById(R.id.reset);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AppDataProvider appDataProvider = ((MainActivity) getActivity()).getDataController();

                appDataProvider.clearBookmarkedCities(new BackgroundTaskListener<Cursor>() {
                    @Override
                    public void completed(String key, Cursor data) {
                        Toast.makeText(appDataProvider.getActivity(),
                                "Bookmarks cleared.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void error(ProviderException ex) {

                    }
                }, getActivity());
            }
        });

        RadioButton metric = ((RadioButton)parent.findViewById(R.id.radio_metric));
        metric.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceUtil.saveUnitSystem(getActivity(), "metric");
                    ClimateDataController.getInstance().setForceRefresh();
                }
            }
        });


        RadioButton imperial = ((RadioButton)parent.findViewById(R.id.radio_imperial));
        imperial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    PreferenceUtil.saveUnitSystem(getActivity(), "imperial");
                    ClimateDataController.getInstance().setForceRefresh();
                }
            }
        });

        if (PreferenceUtil.getUnitSystem(getActivity()).equals("metric")) {
            metric.setChecked(true);
        } else {
            imperial.setChecked(true);
        }

    }

}
