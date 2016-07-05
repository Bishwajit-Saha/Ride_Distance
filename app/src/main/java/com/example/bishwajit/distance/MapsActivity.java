package com.example.bishwajit.distance;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    EditText meters;
    Button start, stop;
    static TextView total_distance;
    static GoogleApiClient mGoogleApiClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        meters = (EditText) findViewById(R.id.meters);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        total_distance = (TextView) findViewById(R.id.total_distance);

        // read the sharedPreference and set the previous state
        resetActivityFromSharedPreference();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.i("App", "mGoogleApiClient created in MainActivity");
        }

        // onClickListeners for the buttons
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set the sharedPreferance
                setSharedPreferance(false);
                startRide();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset the sharedPreference
                setSharedPreferance(true);
                stopRide();
            }
        });

    }

    private void setSharedPreferance(boolean i) {

        SharedPreferences preferences = getSharedPreferences("ButtonStates", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("StartButtonClickable", i);
        editor.commit();

    }

    private void resetActivityFromSharedPreference() {

        SharedPreferences preferences = getSharedPreferences("ButtonStates", 0);
        boolean clickable = preferences.getBoolean("StartButtonClickable", true);

        if(clickable)
        {
            start.setEnabled(true);
            stop.setEnabled(false);
        }
        else
        {
            start.setEnabled(false);
            stop.setEnabled(true);
            total_distance.setText("Recording Locations");
        }

    }


    private void startRide() {


        if (location_mode(getApplicationContext()) != 3) {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            Toast.makeText(getApplicationContext(), "Enable high accuracy mode", Toast.LENGTH_LONG).show();
            setSharedPreferance(true);
            return;
        }
        mMap.clear();
        total_distance.setText("recording locations");
        start.setEnabled(false);
        stop.setEnabled(true);
        meters.setEnabled(false);

        // create the service here and start receiving location updates
        startService(new Intent(MapsActivity.this, LocationService.class));
    }

    private void stopRide() {

        //stop the service
        stopService(new Intent(MapsActivity.this, LocationService.class));

        // reset the members
        stop.setEnabled(false);
        start.setEnabled(true);
    }


   //checks the location mode
    public int location_mode(Context context)
    {
        int locationMode = -1;
        try {
            locationMode =  Settings.Secure.getInt(context.getContentResolver(),Settings.Secure.LOCATION_MODE);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return locationMode;
    }


    public static void setDistance(double distance)
    {
        total_distance.setText(String.valueOf(distance));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }


    public static GoogleApiClient getGoogleApiClient()
    {
        return mGoogleApiClient;
    }

    @Override
    protected void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }
}
