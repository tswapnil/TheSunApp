package com.example.android.thesunapp;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Swapnil on 26-12-2016.
 */

public class SunshineSyncIntentService extends IntentService{
    public SunshineSyncIntentService () {
        super("SunshineSyncIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        SunshineSyncTask.syncWeather(this);
       }
}
