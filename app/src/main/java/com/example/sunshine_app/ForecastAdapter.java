package com.example.sunshine_app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.sunshine.R;
import com.example.sunshine_app.utilities.SunshineDateUtils;
import com.example.sunshine_app.utilities.SunshineWeatherUtils;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;


    private final Context mcontext;
    private Cursor mCursor;
    private boolean mUseTodayLayout;

    //private String[] mWeatherData;
    private final ForecastAdapterOnClickHandler mClickHandler;

    public interface ForecastAdapterOnClickHandler{
        void onClick(long date);
    }

    public ForecastAdapter(Context context , ForecastAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
        mcontext=context;
        mUseTodayLayout = mcontext.getResources().getBoolean(R.bool.use_today_layout);
    }


//    public ForecastAdapter(){
//
//    }

     class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

         final TextView dateView;
         final TextView descriptionView;
         final TextView highTempView;
         final TextView lowTempView;

         final ImageView iconView;

         ForecastAdapterViewHolder(View view){
            super(view);

             iconView = (ImageView) view.findViewById(R.id.weather_icon);
             dateView = (TextView) view.findViewById(R.id.date);
             descriptionView = (TextView) view.findViewById(R.id.weather_description);
             highTempView = (TextView) view.findViewById(R.id.high_temperature);
             lowTempView = (TextView) view.findViewById(R.id.low_temperature);


             view.setOnClickListener(this);
        }

         @Override
         public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis= mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);
         }
     }


    @Override
    public  ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup,int viewType){
        int layoutId;

        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.forecast_list_item;
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        View view = LayoutInflater.from(mcontext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);

        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void  onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position){
        mCursor.moveToPosition(position);

        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherImageId;

        int viewType = getItemViewType(position);

        switch (viewType) {
            case VIEW_TYPE_TODAY:
                weatherImageId = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);
                break;
                case VIEW_TYPE_FUTURE_DAY:
                weatherImageId = SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);
                break;
                default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }

        forecastAdapterViewHolder.iconView.setImageResource(weatherImageId);


        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        String dateString = SunshineDateUtils.getFriendlyDateString(mcontext,dateInMillis,false);
        forecastAdapterViewHolder.dateView.setText(dateString);

        String description = SunshineWeatherUtils.getStringForWeatherCondition(mcontext, weatherId);
        String descriptionA11y = mcontext.getString(R.string.a11y_forecast, description);
        forecastAdapterViewHolder.descriptionView.setText(description);
        forecastAdapterViewHolder.descriptionView.setContentDescription(descriptionA11y);

        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        String highString = SunshineWeatherUtils.formatTemperature(mcontext, highInCelsius);
        String highA11y = mcontext.getString(R.string.a11y_high_temp, highString);
        forecastAdapterViewHolder.highTempView.setText(highString);
        forecastAdapterViewHolder.highTempView.setContentDescription(highA11y);

        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        String lowString = SunshineWeatherUtils.formatTemperature(mcontext, lowInCelsius);
        String lowA11y = mcontext.getString(R.string.a11y_low_temp, lowString);
        forecastAdapterViewHolder.lowTempView.setText(lowString);
        forecastAdapterViewHolder.lowTempView.setContentDescription(lowA11y);
    }

    @Override
    public int getItemCount(){
        if(null == mCursor)
            return 0;
        return  mCursor.getCount();
    }

    void swapCursor(Cursor newCursor){
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position){
        if (mUseTodayLayout && position == 0) {
            return VIEW_TYPE_TODAY;
        } else {
            return VIEW_TYPE_FUTURE_DAY;
        }
    }

}
