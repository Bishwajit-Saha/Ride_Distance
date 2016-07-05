package com.example.bishwajit.distance;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by bishwajit on 7/5/2016.
 */
// asynckTask to get total distance, Reads the database and gets the distance
public class CustomAsyncTask extends AsyncTask<DBHelper, Void, Double> {

    DistanceDbCallback c;
    int grouping_meter = 10;
    public CustomAsyncTask(DistanceDbCallback c) {
        this.c = c;
    }

    @Override
    protected Double doInBackground(DBHelper... params) {
        SQLiteDatabase db_read = params[0].getReadableDatabase();

        String[] columns = {TableData.TableInfo.LAT, TableData.TableInfo.LNG};
        Cursor data = db_read.query(TableData.TableInfo.TABLE_NAME,
                columns, null, null, null, null, null);

        int length = data.getCount();

        // currentLocations stores the list of locations that come within the grouping meter
        ArrayList<Location> currentLocations;
        double local_distance = 0, avg_distance = 0, d;
        int count;
        double avg_lat = 0, avg_lng = 0;
        Location past;

        // if there are no locations or only one location then set avg_distance to 0 and return
        if (length == 0 || length == 1) {
            avg_distance = 0;
        }

        // there are 2 or more than 2 locations in the DB
        // the first row is start point. Last row is end point. Rest all are considered for averaging
        else {
            // setting up the parameters
            //avgLocations = new ArrayList<>();
            currentLocations = new ArrayList<>();
            local_distance = 0;
            avg_distance = 0;
            count = 0;
            data.moveToFirst();

            // adding the first location to the list as the start point. keep record of the time
            Location first = new Location("");
            first.setLatitude(data.getDouble(0));
            first.setLongitude(data.getDouble(1));

            currentLocations.add(first);
            past = first;
            count++;
            data.moveToNext();

            Location c;

            // reading each row after first till 2nd last
            // the last location is kept for endpoint and is not averaged
            while (data.isLast() == false) {
                c = new Location("");
                c.setLatitude(data.getDouble(0));
                c.setLongitude(data.getDouble(1));

                // get the distance between to last read location and current location
                d = currentLocations.get(count - 1).distanceTo(c);

                // if adding the new location doesn't exceed the grouping_meter distance than add to list
                if (local_distance + d <= grouping_meter) {
                    currentLocations.add(c);
                    count++;
                    local_distance += d;
                    Log.i("App", "to group");
                }

                // if the new location exceeds the grouping_meter than make the avg of all the locations in the set
                // and add it to the avg_locations
                else {
                    // do the avg of the locations and add it to list
                    avg_lat = 0;
                    avg_lng = 0;
                    for (int i = 0; i < count; i++) {
                        avg_lat += currentLocations.get(i).getLatitude();
                        avg_lng += currentLocations.get(i).getLongitude();
                    }
                    avg_lat /= count;
                    avg_lng /= count;

                    // create a new location based on the avg_lat an avg_lng and add it to the avg_location list
                    Location new_location = new Location("");
                    new_location.setLatitude(avg_lat);
                    new_location.setLongitude(avg_lng);

                    Log.i("App", "added 1 avg. location");

                    // add the distance between the last average_location and current averaged location to avg_distance
                    avg_distance += past.distanceTo(new_location);
                    past = new_location;

                    // clear the currentLocation and add the last location for next round
                    currentLocations.clear();
                    currentLocations.add(c);
                    count = 1;
                    local_distance = 0;

                }

                data.moveToNext();
            }

            // this will come to effect for the last <10m set of distance whose average is not done
            //avg the last set of not averaged locations and add the distance to avg_distance
            avg_lat = 0;
            avg_lng = 0;
            for (int i = 0; i < count; i++) {
                avg_lat += currentLocations.get(i).getLatitude();
                avg_lng += currentLocations.get(i).getLongitude();
            }
            avg_lat /= count;
            avg_lng /= count;

            Location unaveraged = new Location("");
            unaveraged.setLatitude(avg_lat);
            unaveraged.setLongitude(avg_lng);
            avg_distance += past.distanceTo(unaveraged);
            Log.i("App", "added unaveraged location average");
            past = unaveraged;

//              add the last location received as parameter as the end point
            Location last = new Location("");
            last.setLatitude(data.getDouble(0));
            last.setLongitude(data.getDouble(1));
            Log.i("App", "added End location");

            // add the distance to the last location
            avg_distance += past.distanceTo(last);
        }

        //clean the content of the table
        SQLiteDatabase db_clear = params[0].getWritableDatabase();
        db_clear.execSQL(TableData.TableInfo.DELETE_CONTENT_FROM_TABLE);
        Log.i("App", "clear the content of the table");

        // return the avg_distance computed from the locations
        return avg_distance;
    }

    @Override
    protected void onPostExecute(Double aDouble) {
        c.callBack(aDouble);
    }
}
