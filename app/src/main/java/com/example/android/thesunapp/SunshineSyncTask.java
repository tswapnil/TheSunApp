package com.example.android.thesunapp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateUtils;

import java.net.URL;

/**
 * Created by Swapnil on 26-12-2016.
 */

public class SunshineSyncTask {

    public static void syncWeather(Context context){
       // String location = args.getString("Location");
        URL weatherURL = NetworkUtils.getUrl(context);
        try {
            String jsonWeatherResponse = NetworkUtils
                    .getResponseFromHttpUrl(weatherURL);
            ContentValues[] weatherValues = OpenWeatherJsonUtils
                                       .getWeatherContentValuesFromJson(context, jsonWeatherResponse);

                                /*
             * In cases where our JSON contained an error code, getWeatherContentValuesFromJson
             * would have returned null. We need to check for those cases here to prevent any
             * NullPointerExceptions being thrown. We also have no reason to insert fresh data if
             * there isn't any to insert.
             */
            if (weatherValues != null && weatherValues.length != 0) {
            /* Get a handle on the ContentResolver to delete and insert data */
                 ContentResolver sunshineContentResolver = context.getContentResolver();
                //              COMPLETED (4) If we have valid results, delete the old data and insert the new
              /* Delete old weather data because we don't need to keep multiple days' data */
                    sunshineContentResolver.delete(
                            WeatherContract.WeatherEntry.CONTENT_URI,
                                                               null,
                                                               null);

                                        /* Insert our new weather data into Sunshine's ContentProvider */
                            sunshineContentResolver.bulkInsert(
                                            WeatherContract.WeatherEntry.CONTENT_URI,
                                            weatherValues);
            }

            boolean notificationsEnabled = SunshinePreferences.areNotificationsEnabled(context);
                 /*
                  * If the last notification was shown was more than 1 day ago, we want to send
                 * another notification to the user that the weather has been updated. Remember,
                 * it's important that you shouldn't spam your users with notifications.
                 */
                long timeSinceLastNotification = SunshinePreferences
                       .getEllapsedTimeSinceLastNotification(context);
                boolean oneDayPassedSinceLastNotification = false;
    //              COMPLETED (14) Check if a day has passed since the last notification
                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                      oneDayPassedSinceLastNotification = true;
                }
        /*
         * We only want to show the notification if the user wants them shown and we
         * haven't shown a notification in the past day.
         */
            //              COMPLETED (15) If more than a day have passed and notifications are enabled, notify the user
                           if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                                NotificationUtils.notifyUserOfNewWeather(context);
                                }


                                /* If the code reaches this point, we have successfully performed our sync */

                       } catch (Exception e) {
                        /* Server probably invalid */
                                e.printStackTrace();
                    }

    }
}
