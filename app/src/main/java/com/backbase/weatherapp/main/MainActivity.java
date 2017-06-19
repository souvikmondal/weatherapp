package com.backbase.weatherapp.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.backbase.weatherapp.help.HelpFragment;
import com.backbase.weatherapp.R;
import com.backbase.weatherapp.details.DetailFragment;
import com.backbase.weatherapp.home.HomeFragment;
import com.backbase.weatherapp.model.City;
import com.backbase.weatherapp.settings.SettingsFragment;
import com.backbase.weatherapp.util.IFragmentInteraction;
import com.backbase.weatherapp.util.provider.ResourceProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements IFragmentInteraction,
        OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener,
        LoaderManager.LoaderCallbacks, LocationListener,
        FragmentManager.OnBackStackChangedListener {

    private static final String TAG_RES_FRAGMENT = "res_fragment";

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private MapPresenter mapPresenter;
    private FusedLocationProviderClient locationProviderClient;
    private Location currentLocation;
    private List<IFavAddedListener> favAddedListeners;
    private ResourceProvider resourceProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build();

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mapPresenter = new MapPresenter(this);

        favAddedListeners = new ArrayList<>();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);

        HomeFragment homeFragment = new HomeFragment();
        favAddedListeners.add(homeFragment);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_fragment, homeFragment)
                .commit();

        resourceProvider = (ResourceProvider) getSupportFragmentManager()
                .findFragmentByTag(TAG_RES_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (resourceProvider == null) {
            resourceProvider = new ResourceProvider();
            getSupportFragmentManager().beginTransaction()
                    .add(resourceProvider, TAG_RES_FRAGMENT).commit();
        }

    }

    public ResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    public void showDetails(City data) {
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setCity(data);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragment, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    public void showMap() {
        SupportMapFragment mapFragment = new SupportMapFragment();
        mapFragment.setHasOptionsMenu(false);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragment, mapFragment)
                .addToBackStack(null)
                .commit();
        mapFragment.getMapAsync(this);
    }

    public void showHelp() {
        HelpFragment helpFragment = new HelpFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragment, helpFragment)
                .addToBackStack(null)
                .commit();
    }

    public void showSettings() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showSettings();
            return true;
        }

        else if (id == R.id.action_help) {
            showHelp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void next(Fragment fragment) {

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
                for(IFavAddedListener listener:favAddedListeners) {
                    City c = new City();
                    c.setLon(marker.getPosition().longitude);
                    c.setLat(marker.getPosition().latitude);
                    c.setDesc(city);
                    listener.onAdded(c);
                }
            }
        });

        if (currentLocation != null) {
            moveMapTo(currentLocation);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    @Override
    public void onLocationChanged(Location location) {

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

    class AddressLoader extends AsyncTaskLoader<String>{

        public AddressLoader(Context context) {
            super(context);
        }

        @Override
        public String loadInBackground() {
            return null;
        }
    }
}
