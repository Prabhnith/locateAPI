package com.prabhnith.locateapi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private static final String TAG = "LocationServiceINFO";
    double mlongitude, mlatitude;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        createLocationRequest();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onPause() {
        mGoogleApiClient.disconnect();
        stopLocationUpdates();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mGoogleApiClient.connect();
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        stopLocationUpdates();
        super.onStop();
    }

    protected void createLocationRequest() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(2000).setFastestInterval(1000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setSmallestDisplacement(2);
        }
    }

    protected void startLocationUpdates() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng mylocation = new LatLng(mlatitude, mlongitude);
        Toast.makeText(MapsActivity.this, "location :" + mlatitude + " " + mlongitude, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "OnMapReady location" + mlatitude + " " + mlongitude);
        mMap.addMarker(new MarkerOptions().position(mylocation).title("I am here."));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 25));
    }

    @Override
    public void onLocationChanged(Location location) {

        if (location != null) {
            mlatitude = location.getLatitude();
            mlongitude = location.getLongitude();

            LatLng mylocation = new LatLng(mlatitude, mlongitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 25));
//            mMap.animateCamera(CameraUpdateFactory.zoomIn());
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            mapFragment.getMapAsync(this);
        }
        Toast.makeText(MapsActivity.this, "OnLocationChanged  :" + mlatitude + " " + mlongitude, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "OnLocationChanged" + mlatitude + " " + mlongitude);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {

            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLocation != null) {
                mlatitude = mLocation.getLatitude();
                mlongitude = mLocation.getLongitude();

                LatLng mylocation = new LatLng(mlatitude, mlongitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 15));
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                mapFragment.getMapAsync(this);
            }
        }
        Toast.makeText(MapsActivity.this, "onConnected location :" + mlatitude + " " + mlongitude, Toast.LENGTH_SHORT).show();
        Log.i(TAG, "OnConnected location" + mlatitude + " " + mlongitude);
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
