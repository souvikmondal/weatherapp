package com.backbase.weatherapp.settings;


import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.util.db.dao.CityDao;
import com.backbase.weatherapp.util.provider.AsyncProvider;

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

    private void init(View parent) {

        resetBtn = (Button) parent.findViewById(R.id.reset);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Activity activity = getActivity();

                AsyncProvider.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (activity != null) {
                            CityDao.removeAllCities(activity);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "All boolmarked cities removed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

}
