package br.edu.ufabc.padm.jean.obompassageiro;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import java.text.DateFormat;
import java.util.Date;

import br.edu.ufabc.padm.jean.obompassageiro.model.Lines;
import br.edu.ufabc.padm.jean.obompassageiro.services.LocationUpdateService;

public class WaitFragment extends Fragment implements
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    protected static final String TAG = "WaitFragment";

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    protected Boolean mRequestingLocationUpdates = true;

    protected String mLastUpdateTime;

    PendingIntent mRequestLocationUpdatesPendingIntent;

    protected GoogleApiClient mGoogleApiClient;

    protected Location mLastLocation;
    protected MapView mMapView;
    private Fragment mFragment;

    private String mLongitudeText = "";
    private String mLatitudeText = "";

    public WaitFragment() {
        mFragment = this;
    }

    public static WaitFragment newInstance() {
        WaitFragment fragment = new WaitFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestingLocationUpdates = true;
        mLastUpdateTime = "";

        updateValuesFromBundle(savedInstanceState);

        buildGoogleApiClient();
    }

    public void onMapReady(GoogleMap googleMap) {
        Permission.checkAllUsePermission(this);
        googleMap.setMyLocationEnabled(true);

        Location fromServer = LocationUpdateService.callServer("", getContext(), new Intent());
        if (fromServer != null) {
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(fromServer.getLatitude(), fromServer.getLongitude()))
                    .title("Corre!"));
        }
    }

    private void updateUI() {
        if (mMapView != null) {
            try {
                mMapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 17.0f));
            } catch (Exception e) {
                Log.d(TAG, "FAIL MOVING CAMERA");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wait, container, false);

        mMapView = (MapView) view.findViewById(R.id.fragment_embedded_map_view_mapview);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                //Location fromServer = LocationUpdateService.callServer("", getContext(), new Intent());
                /*if(fromServer != null) {
                    googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(fromServer.getLongitude(), fromServer.getLatitude()))
                            .title("Corre!")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

                } else {*/
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(-23.644776, -46.528124))
                        .title("Corre!")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
                //}

                Permission.checkAllUsePermission(mFragment);
                googleMap.getUiSettings().setScrollGesturesEnabled(false);

                if (mCurrentLocation != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 17.0f));
                } else if (mLastLocation != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 17.0f));
                }

            }
        });

        Lines data = new Lines();
        Spinner spinner = (Spinner) view.findViewById(R.id.wait_spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_dropdown_item, data.getNamesList());
        ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {

                if (posicao == 0) {
                    // First element is default
                    return;
                }
                String nome = parent.getItemAtPosition(posicao).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = true;
            }

            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }

            updateUI();
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);

        super.onSaveInstanceState(savedInstanceState);

        if (mMapView != null) {
            mMapView.onSaveInstanceState(savedInstanceState);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle connectionHint) {

        if (mCurrentLocation == null) {
            Permission.checkAllUsePermission(this);
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        Intent mRequestLocationUpdatesIntent = new Intent(getContext(), LocationUpdateService.class);

        // create a PendingIntent
        mRequestLocationUpdatesPendingIntent = PendingIntent.getService(getContext(), 0,
                mRequestLocationUpdatesIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // request location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest,
                mRequestLocationUpdatesPendingIntent);

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    public void startUpdatesButtonHandler(View view) {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    public void stopUpdatesButtonHandler(View view) {
        if (mRequestingLocationUpdates) {
            stopLocationUpdates();
        }
    }

    protected void startLocationUpdates() {
        Permission.checkAllUsePermission(this);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMapView != null) {
            mMapView.onResume();
        }

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }

        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mRequestLocationUpdatesPendingIntent);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (NullPointerException e) {
            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

}
