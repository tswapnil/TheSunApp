package com.example.android.thesunapp;

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
    private final ForecastAdapterOnClickHandler mClickHandler;


    public interface ForecastAdapterOnClickHandler {
        void onClick(String weatherForDay);
    }
  public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
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
        String weatherForDay = weatherData[adapterPosition];
        mClickHandler.onClick(weatherForDay);
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
        String weatherForThisDay = weatherData[position];
        fViewHolder.mWeatherTV.setText(weatherForThisDay);
    }
    @Override
    public int getItemCount(){
        if(null==weatherData){
            return 0;
        }else{
            return weatherData.length;
        }
    }
    public void setWeatherData(String[] weatherData) {
        this.weatherData = weatherData;
        notifyDataSetChanged();
    }
}
