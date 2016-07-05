package com.example.bishwajit.distance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;


/**
 * Created by bishwajit on 29/06/16.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    //constructor
    public DBHelper(Context context) {
        super(context, TableData.TableInfo.DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("App", "Database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating the table
        try {
            db.execSQL(TableData.TableInfo.CREATE_TABLE_SQL_QUERY);
            Log.i("App", "Table created");
        } catch (Exception e) {
            Log.i("App", "error in creating table");
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    // inserting locations to DB
    public void insertIntoDB(DBHelper dbHelper, double lat, double lng) {

        // insert the received location details (lat, long, time ) to the DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TableData.TableInfo.LAT, lat);
        values.put(TableData.TableInfo.LNG, lng);

        db.insert(TableData.TableInfo.TABLE_NAME, null, values);
        String log = "Inserted " + String.valueOf(lat) + " , " + String.valueOf(lng);
        Log.i("App", log);
    }

}


