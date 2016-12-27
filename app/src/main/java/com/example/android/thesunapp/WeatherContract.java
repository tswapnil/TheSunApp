package com.example.android.thesunapp;

import android.provider.BaseColumns;
import android.net.Uri;

/**
 * Created by Swapnil on 21-12-2016.
 */

public class WeatherContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.thesunapp";

        /*
         * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
         * the content provider for Sunshine.
         */

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";

    public static final class WeatherEntry implements BaseColumns {



        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build();
//      COMPLETED (2) Create a public static final String call TABLE_NAME with the value "weather"

        /* Used internally as the name of our weather table. */
        public static final String TABLE_NAME = "weather";

        //      COMPLETED (3) Create a public static final String call COLUMN_DATE with the value "date"
        /*
         * The date column will store the UTC date that correlates to the local date for which
         * each particular weather row represents. For example, if you live in the Eastern
         * Standard Time (EST) time zone and you load weather data at 9:00 PM on September 23, 2016,
         * the UTC time stamp for that particular time would be 1474678800000 in milliseconds.
         * However, due to time zone offsets, it would already be September 24th, 2016 in the GMT
         * time zone when it is 9:00 PM on the 23rd in the EST time zone. In this example, the date
         * column would hold the date representing September 23rd at midnight in GMT time.
         * (1474588800000)
         *
         * The reason we store GMT time and not local time is because it is best practice to have a
         * "normalized", or standard when storing the date and adjust as necessary when
         * displaying the date. Normalizing the date also allows us an easy way to convert to
         * local time at midnight, as all we have to do is add a particular time zone's GMT
         * offset to this date to get local time at midnight on the appropriate date.
         */
        public static final String COLUMN_DATE = "date";

        //      COMPLETED (4) Create a public static final String call COLUMN_WEATHER_ID with the value "weather_id"
        /* Weather ID as returned by API, used to identify the icon to be used */
        public static final String COLUMN_WEATHER_ID = "weather_id";

        //      COMPLETED (5) Create a public static final String call COLUMN_MIN_TEMP with the value "min"
        /* Min and max temperatures in °C for the day (stored as floats in the database) */
        public static final String COLUMN_MIN_TEMP = "min";
        //      COMPLETED (6) Create a public static final String call COLUMN_MAX_TEMP with the value "max"
        public static final String COLUMN_MAX_TEMP = "max";

        //      COMPLETED (7) Create a public static final String call COLUMN_HUMIDITY with the value "humidity"
        /* Humidity is stored as a float representing percentage */
        public static final String COLUMN_HUMIDITY = "humidity";

        //      COMPLETED (8) Create a public static final String call COLUMN_PRESSURE with the value "pressure"
        /* Pressure is stored as a float representing percentage */
        public static final String COLUMN_PRESSURE = "pressure";

        //      COMPLETED (9) Create a public static final String call COLUMN_WIND_SPEED with the value "wind"
        /* Wind speed is stored as a float representing wind speed in mph */
        public static final String COLUMN_WIND_SPEED = "wind";

        //      COMPLETED (10) Create a public static final String call COLUMN_DEGREES with the value "degrees"
        /*
         * Degrees are meteorological degrees (e.g, 0 is north, 180 is south).
         * Stored as floats in the database.
         *
         * Note: These degrees are not to be confused with temperature degrees of the weather.
         */
        public static final String COLUMN_DEGREES = "degrees";

        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .build();
        }

        /**
         * Returns just the selection part of the weather query from a normalized today value.
         * This is used to get a weather forecast from today's date. To make this easy to use
         * in compound selection, we embed today's date as an argument in the query.
         *
         * @return The selection part of the weather query for today onwards
         */
        public static String getSqlSelectForTodayOnwards() {
            long normalizedUtcNow = SunshineDateUtils.normalizeDate(System.currentTimeMillis());
            return WeatherContract.WeatherEntry.COLUMN_DATE + " >= " + normalizedUtcNow;
        }
    }
}


