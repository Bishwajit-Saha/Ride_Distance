package com.example.bishwajit.distance;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

public class LocationService extends Service implements com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Context context = this;
    boolean first;
    AverageDistanceWithDB averageDistanceWithDB;
    GoogleApiClient mGoogleApiClient = null;
    LocationRequest locationRequest;
    GoogleApiClient newmGoogleApiClient;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);
        Log.i("App", "Service Started...");
        return START_STICKY;
    }


    @Override
    public void onCreate() {

        super.onCreate();

        first = true;
        Log.i("App", "Service created...");

        // set up the LocationRequest
        locationRequest = new LocationRequest();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.i("App", "Created locationRequest");

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            stopSelf();
        }

        // get the GoogleApiClient and start receiving location updates
        mGoogleApiClient = MapsActivity.getGoogleApiClient();


        averageDistanceWithDB = new AverageDistanceWithDB(context);

        // check if mGoogleApiClient is null create a local instance of GoogleApiClient and connect it
        // receive location updates using the local GoogleApiClient
        if (mGoogleApiClient == null) {
            Log.i("App", "mGoogleApiClient is NULL, creating new instance");
            newmGoogleApiClient = new GoogleApiClient.Builder(LocationService.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(LocationService.this)
                    .addOnConnectionFailedListener(LocationService.this)
                    .build();


            newmGoogleApiClient.connect();
            Log.i("App", ".connect called");
        }
        // if mGoogleApiClient is not null
        else {
            Log.i("App", "mGoogleApiClient is not null");
            if (mGoogleApiClient.isConnected()) {
                Log.i("App", "mGoogleApiClient Connected in service");
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {


        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng latLng = new LatLng(lat, lng);
        //Log.i("App", String.valueOf(lat) + " , " + String.valueOf(lng));
        averageDistanceWithDB.addToDB(latLng);


    }

    @Override
    public void onDestroy() {

        Log.i("App", "onDestroy Service");
        // remove receiving location updates
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        averageDistanceWithDB.reset();
        averageDistanceWithDB = null;
        locationRequest = null;
        super.onDestroy();

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("App", "inside onConnected function");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(newmGoogleApiClient, locationRequest, this);
        mGoogleApiClient = newmGoogleApiClient;

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("App", ">"+connectionResult.toString());
    }

}
