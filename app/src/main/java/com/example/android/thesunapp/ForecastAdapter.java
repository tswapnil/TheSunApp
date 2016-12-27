package com.example.android.thesunapp;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.widget.*;
import android.view.*;
import android.content.*;
import org.w3c.dom.Text;

/**
 * Created by Swapnil on 15-12-2016.
 */

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.FViewHolder> {
  private String[] weatherData;
    private Cursor mCursor;
    private final Context mContext;

    private final ForecastAdapterOnClickHandler mClickHandler;


    public interface ForecastAdapterOnClickHandler {
        void onClick(long weatherForDay);
    }
  public ForecastAdapter(@NonNull Context context, ForecastAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
      mContext = context;
    }

public class FViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public final TextView mWeatherTV;
    public FViewHolder(View view){
        super(view);
        mWeatherTV = (TextView) view.findViewById(R.id.tv_weather_data);
        view.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        int adapterPosition = getAdapterPosition();
        mCursor.moveToPosition(adapterPosition);
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        /* Get human readable string using our utility method */
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
        /* Use the weatherId to obtain the proper description */
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        /* Read high temperature from the cursor (in degrees celsius) */
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        /* Read low temperature from the cursor (in degrees celsius) */
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);

        String highAndLowTemperature =
                SunshineWeatherUtils.formatHighLows(mContext, highInCelsius, lowInCelsius);

        String weatherForDay = dateString + " - " + description + " - " + highAndLowTemperature;


      //  String weatherForDay = mCursor.;
        mClickHandler.onClick(dateInMillis);
    }

}
    @Override
    public FViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.forecast_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem,viewGroup,shouldAttachToParentImmediately);
        return new FViewHolder(view);
    }
    @Override
    public void onBindViewHolder(FViewHolder fViewHolder, int position) {
       // String weatherForThisDay = weatherData[position];
        String weatherForThisDay ;
        mCursor.moveToPosition(position);
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        /* Get human readable string using our utility method */
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
        /* Use the weatherId to obtain the proper description */
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        /* Read high temperature from the cursor (in degrees celsius) */
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        /* Read low temperature from the cursor (in degrees celsius) */
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);

        String highAndLowTemperature =
                SunshineWeatherUtils.formatHighLows(mContext, highInCelsius, lowInCelsius);

        String weatherSummary = dateString + " - " + description + " - " + highAndLowTemperature;




        fViewHolder.mWeatherTV.setText(weatherSummary);
    }
    @Override
    public int getItemCount(){
        /**if(null==weatherData){
            return 0;
        }else{
            return weatherData.length;
        }**/
        return mCursor.getCount();
    }
    public void setWeatherData(String[] weatherData) {
        this.weatherData = weatherData;
        notifyDataSetChanged();
    }
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        // After the new Cursor is set, call notifyDataSetChanged
        notifyDataSetChanged();
    }
}
