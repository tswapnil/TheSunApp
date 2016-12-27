package com.example.android.thesunapp;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.SupportActivity;
import android.support.v4.content.*;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.support.v7.widget.LinearLayoutManager;
import java.net.*;
import android.content.*;
import android.support.v4.app.ShareCompat;
import android.util.*;
import android.net.*;
public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler , LoaderManager.LoaderCallbacks<Cursor>,SharedPreferences.OnSharedPreferenceChangeListener {


    private TextView tvDisplay;
    private TextView hView ;
    private ProgressBar pBar;
    private ForecastAdapter fAdapter;
    private RecyclerView recyclerView;
    private int mPosition = RecyclerView.NO_POSITION;
    private static final int LoaderID = 372;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED = false;
    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    //  COMPLETED (17) Create constant int values representing each column name's position above
    /*
     * We store the indices of the values in the array of Strings above to more quickly be able to
     * access the data from our query. If the order of the Strings above changes, these indices
     * must be adjusted to match the order of the Strings.
     */
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // tvDisplay = (TextView) findViewById(R.id.tv_weather);
       // getSupportActionBar().setElevation(0f);

        hView = (TextView) findViewById(R.id.hidden_view);
        hView.setVisibility(View.INVISIBLE);
        hView.setText(R.string.error_string);
        pBar = (ProgressBar) findViewById(R.id.pBar);
        pBar.setVisibility(View.INVISIBLE);
        recyclerView = (RecyclerView) findViewById(R.id.tv_recycler);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        fAdapter = new ForecastAdapter(this,this);
        recyclerView.setAdapter(fAdapter);
        getSupportLoaderManager().initLoader(LoaderID,null,this);
        loadWeatherData();
        PreferenceManager.getDefaultSharedPreferences(this)
                               .registerOnSharedPreferenceChangeListener(this);
        SunshineSyncUtils.intialise(this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menufile,menu);
        return true;
    }
    @Override
    public void onClick(long date) {
        Context context = this;
     //    Toast.makeText(context, weatherForDay, Toast.LENGTH_LONG)
     //           .show();
     //   Intent intent = new Intent(context, DetailActivity.class);
     //   intent.putExtra("ExtraData", weatherForDay);
     //   startActivity(intent);
        Intent weatherDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
        Uri uriForDateClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
        weatherDetailIntent.setData(uriForDateClicked);
        startActivity(weatherDetailIntent);
    }
    private void openLocationInMap() {
        //  String addressString = "1600 Ampitheatre Parkway, CA";
        String addressString = SunshinePreferences.getPreferredWeatherLocation(this);
                Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(geoLocation);

                if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                 } else {
                        Log.d("Main Activity", "Couldn't call " + geoLocation.toString()
                                       + ", no receiving apps installed!");
                    }
            }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCES_HAVE_BEEN_UPDATED) {
            Log.d("Main Activity", "onStart: preferences were updated");
         //   getSupportLoaderManager().restartLoader(LoaderID, null, this);
           loadWeatherData();
            PREFERENCES_HAVE_BEEN_UPDATED = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                         .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int itemThatWasClicked = menuItem.getItemId();
        if(itemThatWasClicked == R.id.refresh_action){
            Context context = MainActivity.this;
            String textToShow = "Refresh Clicked";
            Toast.makeText(context, textToShow, Toast.LENGTH_LONG).show();
          //  tvDisplay.setText(" ");
            loadWeatherData();
            return true;
        }
        if(itemThatWasClicked==R.id.share_action){
            String mimeType = "text/plain";
            String textToShare = "Hello There";
            String title = "Share Your Feedback";
            ShareCompat.IntentBuilder i = ShareCompat.IntentBuilder.from(this)
                    .setChooserTitle(title)
                    .setType(mimeType)
                    .setText(textToShare);
            startActivity(i.getIntent());
            return true;
        }
        if(itemThatWasClicked == R.id.action_map){
            openLocationInMap();
            return true;
        }
        if(itemThatWasClicked == R.id.setting_action){
            Context context = this;
            Intent intent = new Intent(context, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void loadWeatherData() {
                 showWeatherDataView();
               hView.setVisibility(View.INVISIBLE);
               pBar.setVisibility(View.INVISIBLE);
               String location = SunshinePreferences.getPreferredWeatherLocation(this);
        Bundle bundle = new Bundle();
        bundle.putString("Location", location);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> gitHubLoader = loaderManager.getLoader(LoaderID);
        if (gitHubLoader == null){
            loaderManager.initLoader(LoaderID,bundle,this);
        }
        else{
            loaderManager.restartLoader(LoaderID, bundle, this);
        }

            //   new FetchWeatherTask().execute(location);
    }
    private void showWeatherDataView() {
        /* First, make sure the error is invisible */
        hView.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        recyclerView.setVisibility(View.VISIBLE);
    }
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        recyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        hView.setVisibility(View.VISIBLE);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
        switch (id) {

//          COMPLETED (22) If the loader requested is our forecast loader, return the appropriate CursorLoader
            case LoaderID:
                /* URI for all rows of weather data in our weather table */
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                /* Sort order: Ascending by date */
                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                /*
                 * A SELECTION in SQL declares which rows you'd like to return. In our case, we
                 * want all weather data from today onwards that is stored in our weather table.
                 * We created a handy method to do that in our WeatherEntry class.
                 */
                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(this,
                        forecastQueryUri,
                        MAIN_FORECAST_PROJECTION,
                        selection,
                        null,
                        sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }





        /**return new AsyncTaskLoader<Cursor>(this) {
            @Override
            protected void onStartLoading(){
                if(args==null){
                    return;
                }

                pBar.setVisibility(View.VISIBLE);
                forceLoad();
            }
            @Override
            public Cursor loadInBackground() {

                String location = args.getString("Location");
                URL weatherURL = NetworkUtils.buildUrl(location);
                try {
                    String jsonWeatherResponse = NetworkUtils
                            .getResponseFromHttpUrl(weatherURL);
                    String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                            .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                    return simpleJsonWeatherData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

            }
        };**/
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        pBar.setVisibility(View.INVISIBLE);
//        if (data != null) {
//
//            //   for (String weatherString : weatherData) {
//            //             tvDisplay.append((weatherString) + "\n\n\n");
//            //         }
//            showWeatherDataView();
//            fAdapter.swapCursor(data);
//
//        }
//        else{
//            //  tvDisplay.setText("");
//            showErrorMessage();
//            hView.setVisibility(View.VISIBLE);
//
//        }

        fAdapter.swapCursor(data);
//      COMPLETED (29) If mPosition equals RecyclerView.NO_POSITION, set it to 0
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
//      COMPLETED (30) Smooth scroll the RecyclerView to mPosition
        recyclerView.smoothScrollToPosition(mPosition);

//      COMPLETED (31) If the Cursor's size is not equal to 0, call showWeatherDataView
        if (data.getCount() != 0) showWeatherDataView();
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        PREFERENCES_HAVE_BEEN_UPDATED = true;
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String[] doInBackground(String... strings) {
            if(strings.length==0){
                return null;
            }

            String location = strings[0];
            URL weatherURL = NetworkUtils.buildUrl(location);
            try {
                               String jsonWeatherResponse = NetworkUtils
                                               .getResponseFromHttpUrl(weatherURL);
                String[] simpleJsonWeatherData = OpenWeatherJsonUtils
                                               .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);

                                       return simpleJsonWeatherData;

                                } catch (Exception e) {
                            e.printStackTrace();
                               return null;
                           }

        }
        @Override
        protected void onPostExecute(String[] weatherData) {
              pBar.setVisibility(View.INVISIBLE);
                   if (weatherData != null) {

                 //   for (String weatherString : weatherData) {
                 //             tvDisplay.append((weatherString) + "\n\n\n");
                 //         }
                       showWeatherDataView();
                       fAdapter.setWeatherData(weatherData);

                       }
                    else{
                           //  tvDisplay.setText("");
                             showErrorMessage();
                             hView.setVisibility(View.VISIBLE);

                     }
               }
        }
    private void openPreferredLocationInMap() {
        double[] coords = SunshinePreferences.getLocationCoordinates(this);
        String posLat = Double.toString(coords[0]);
        String posLong = Double.toString(coords[1]);
        Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d("MainActivity", "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
        }
    }

    
}
