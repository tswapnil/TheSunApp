package com.example.android.thesunapp;

/**
 * Created by Swapnil on 21-12-2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.thesunapp.WeatherContract.WeatherEntry;

/**
 * Manages a local database for weather data.
 */

// COMPLETED (11) Extend SQLiteOpenHelper from WeatherDbHelper
public class WeatherDbHelper extends SQLiteOpenHelper {

    //  COMPLETED (12) Create a public static final String called DATABASE_NAME with value "weather.db"
    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    public static final String DATABASE_NAME = "weather.db";

    //  COMPLETED (13) Create a private static final int called DATABASE_VERSION and set it to 1
    /*
     * If you change the database schema, you must increment the database version or the onUpgrade
     * method will not be called.
     *
     * The reason DATABASE_VERSION starts at 3 is because Sunshine has been used in conjunction
     * with the Android course for a while now. Believe it or not, older versions of Sunshine
     * still exist out in the wild. If we started this DATABASE_VERSION off at 1, upgrading older
     * versions of Sunshine could cause everything to break. Although that is certainly a rare
     * use-case, we wanted to watch out for it and warn you what could happen if you mistakenly
     * version your databases.
     */
    private static final int DATABASE_VERSION = 2;

    //  COMPLETED (14) Create a constructor that accepts a context and call through to the superclass constructor
    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//  COMPLETED (15) Override onCreate and create the weather table from within it
    /**
     * Called when the database is created for the first time. This is where the creation of
     * tables and the initial population of the tables should happen.
     *
     * @param sqLiteDatabase The database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

         final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +

                        WeatherEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        WeatherEntry.COLUMN_DATE       + " INTEGER NOT NULL, "                 +

                        WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, "                 +

                        WeatherEntry.COLUMN_MIN_TEMP   + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_MAX_TEMP   + " REAL NOT NULL, "                    +

                        WeatherEntry.COLUMN_HUMIDITY   + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_PRESSURE   + " REAL NOT NULL, "                    +

                        WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_DEGREES    + " REAL NOT NULL, " +
                        " UNIQUE (" + WeatherEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}