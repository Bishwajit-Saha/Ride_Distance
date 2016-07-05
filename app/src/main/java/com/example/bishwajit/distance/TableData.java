package com.example.bishwajit.distance;

import android.provider.BaseColumns;

/**
 * Created by bishwajit on 29/06/16.
 */
public class TableData {

    // constructor
    TableData()
    {

    }

    public static abstract class TableInfo implements BaseColumns {

        // table columns names
        public static final String LAT = " latitude ";
        public static final String LNG = " longitude ";

        // table data type
        public static final String TYPE = " double ";
        public static final String SEPERATOR = ",";

        // names
        public static final String TABLE_NAME = " location_table ";
        public static final String DATABASE_NAME = "locations_db";

        // sql queries

        // create table sql
        public static final String CREATE_TABLE_SQL_QUERY  =  "CREATE TABLE " + TABLE_NAME + " (" +
                LAT + TYPE + SEPERATOR +
                LNG + TYPE +  " ); ";

        // delete the content of the table
        public static final String DELETE_CONTENT_FROM_TABLE = "DELETE FROM " + TABLE_NAME;

        // remove the table
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;


    }


}
