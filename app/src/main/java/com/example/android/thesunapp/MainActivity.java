package com.example.android.thesunapp;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.SupportActivity;
import android.support.v4.content.*;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler , LoaderManager.LoaderCallbacks<String[]> {


    private TextView tvDisplay;
    private TextView hView ;
    private ProgressBar pBar;
    private ForecastAdapter fAdapter;
    private RecyclerView recyclerView;
    private static final int LoaderID = 372;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // tvDisplay = (TextView) findViewById(R.id.tv_weather);
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

        fAdapter = new ForecastAdapter(this);
        recyclerView.setAdapter(fAdapter);
        getSupportLoaderManager().initLoader(LoaderID,null,this);
        loadWeatherData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menufile,menu);
        return true;
    }
    @Override
    public void onClick(String weatherForDay) {
        Context context = this;
        Toast.makeText(context, weatherForDay, Toast.LENGTH_LONG)
                .show();
       Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("ExtraData", weatherForDay);
        startActivity(intent);
    }
    private void openLocationInMap() {
                String addressString = "1600 Ampitheatre Parkway, CA";
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
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {
            @Override
            protected void onStartLoading(){
                if(args==null){
                    return;
                }

                pBar.setVisibility(View.VISIBLE);
                forceLoad();
            }
            @Override
            public String[] loadInBackground() {

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
        };
    }


    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        pBar.setVisibility(View.INVISIBLE);
        if (data != null) {

            //   for (String weatherString : weatherData) {
            //             tvDisplay.append((weatherString) + "\n\n\n");
            //         }
            showWeatherDataView();
            fAdapter.setWeatherData(data);

        }
        else{
            //  tvDisplay.setText("");
            showErrorMessage();
            hView.setVisibility(View.VISIBLE);

        }

    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<String[]> loader) {

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

    
}
