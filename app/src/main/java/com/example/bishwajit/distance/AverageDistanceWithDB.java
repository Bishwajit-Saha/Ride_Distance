package com.example.bishwajit.distance;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.concurrent.ExecutionException;

/**
 * Created by bishwajit on 29/06/16.
 */
public class AverageDistanceWithDB implements DistanceDbCallback {


    DBHelper dbHelper;
    double lat, lng;
    // initialize the DB
    AverageDistanceWithDB(Context context)
    {
        dbHelper = new DBHelper(context);
    }


    // call this to add locations to the DB
    // @param Location: location update. add the lat, lng, time to the the table
    public void addToDB(LatLng location)
    {
        lat = location.latitude;
        lng = location.longitude;

        // insert the values to the DB
        dbHelper.insertIntoDB(dbHelper, lat, lng);

    }

    // receives the last location, takes all locations from DB computes the distance and returns it
    // the last received location is used as the end point as that is kept fixed and not averaged
    public void reset()
    {
         Log.i("App", "before call");
        // call the asynkTask to get the distance
        try {
            //dbHelper.getDistance.execute(dbHelper);
            CustomAsyncTask task = new CustomAsyncTask(this);
            task.execute(dbHelper);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void callBack(double d) {
        MapsActivity.setDistance(d);
    }
}
