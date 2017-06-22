package com.backbase.weatherapp.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.backbase.weatherapp.R;
import com.backbase.weatherapp.details.DetailFragment;
import com.backbase.weatherapp.help.HelpFragment;
import com.backbase.weatherapp.home.HomeFragment;
import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.model.weather.Climate;
import com.backbase.weatherapp.settings.SettingsFragment;
import com.backbase.weatherapp.main.provider.AppDataProvider;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, FragmentManager.OnBackStackChangedListener {

    private static final String TAG_RES_FRAGMENT = "res_fragment";
    private static final String TAG_DATA_FRAGMENT = "data_fragment";

    private GoogleMap googleMap;
    private MapPresenter mapPresenter;
    private FusedLocationProviderClient locationProviderClient;
    private Location currentLocation;
    private AppDataProvider appDataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        init();

        initToolbar();

        initDataController();

        showHomeFragment(savedInstanceState);

    }

    private void init() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapPresenter = new MapPresenter(this);
    }

    private void showHomeFragment(Bundle savedInstanceState) {
        if(savedInstanceState == null) {
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_fragment, homeFragment)
                    .commit();
        }
    }

    private void initDataController() {
        appDataProvider = (AppDataProvider) getSupportFragmentManager()
                .findFragmentByTag(TAG_RES_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (appDataProvider == null) {
            appDataProvider = new AppDataProvider();
            getSupportFragmentManager().beginTransaction()
                    .add(appDataProvider, TAG_RES_FRAGMENT).commit();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    public AppDataProvider getDataController() {
        return appDataProvider;
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(
                            INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showDetails(City data) {
        hideSoftKeyboard();
        ClimateDataController.getInstance().setSelectedCurrentCity(data);
        DetailFragment detailFragment = new DetailFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragment, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    public void showMap() {
        hideSoftKeyboard();
        getSupportActionBar().setTitle(getString(R.string.title_map));
        SupportMapFragment mapFragment = new SupportMapFragment();
        mapFragment.setHasOptionsMenu(false);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragment, mapFragment)
                .addToBackStack(null)
                .commit();
        mapFragment.getMapAsync(this);
    }

    public void showHelp() {
        hideSoftKeyboard();
        HelpFragment helpFragment = new HelpFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragment, helpFragment)
                .addToBackStack(null)
                .commit();
    }

    public void showSettings() {
        hideSoftKeyboard();
        SettingsFragment settingsFragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragment, settingsFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            handleLocationPermission();
        } else {
            requestLocation();
        }
    }

    @TargetApi(11)
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        currentLocation = location;
                        moveMapTo(location);
                    }
                });
    }

    private void moveMapTo(Location location) {
        if (googleMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
            googleMap.animateCamera(cameraUpdate);
        }
    }



    @TargetApi(23)
    private void handleLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
                Toast.makeText(this, "Permission reading contacts denied", Toast.LENGTH_LONG);
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
            }
        } else {
            locationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            currentLocation = location;
                            moveMapTo(location);
                        }
                    });
        }
    }


    private void addMarkerOnMap(LatLng latLng) {
        googleMap.clear();
        String city = mapPresenter.getCity(latLng);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng).title(city)
                .snippet("Add as Fav"));
        marker.setTag(city);
        marker.showInfoWindow();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setInfoWindowAdapter(new MapInfoWindowAdapter());
        this.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarkerOnMap(latLng);
            }
        });

        this.googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String city = (String) marker.getTag();
                mapPresenter.saveCityAsFav(city, marker.getPosition());
            }
        });

        if (currentLocation != null) {
            moveMapTo(currentLocation);
        }
    }

    @Override
    public void onBackStackChanged() {
        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return true;
    }

    class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        View view = null;

        MapInfoWindowAdapter() {
            view = LayoutInflater.from(MainActivity.this).inflate(R.layout.map_info, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }


}
