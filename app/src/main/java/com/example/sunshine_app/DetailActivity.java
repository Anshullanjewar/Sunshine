package com.example.sunshine_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.databinding.DataBindingUtil;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.sunshine.R;
import com.example.sunshine_app.data.WeatherContract;
import com.example.sunshine.databinding.ActivityDetailBinding;
import com.example.sunshine_app.utilities.SunshineDateUtils;
import com.example.sunshine_app.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity implements  LoaderManager.LoaderCallbacks<Cursor> {


    private static final String FORECAST_SHARE_HASHTAG = "#SunshineApp";

    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_PRESSURE = 4;
    public static final int INDEX_WEATHER_WIND_SPEED = 5;
    public static final int INDEX_WEATHER_DEGREES = 6;
    public static final int INDEX_WEATHER_CONDITION_ID = 7;


    private static final int ID_DETAIL_LOADER = 353;


    private String mForecastSummary;


    private  String mForecast;
   // private TextView mWeatherDisplay;

//    private TextView mDateView;
//    private TextView mDescriptionView;
//    private TextView mHighTemperatureView;
//    private TextView mLowTemperatureView;
//    private TextView mHumidityView;
//    private TextView mWindView;
//    private TextView mPressureView;

private Uri mUri;
private ActivityDetailBinding mDetailBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);




        mUri = getIntent().getData();
        if(mUri==null )
            throw new NullPointerException("URI for DetailActivity cannot be null");

        LoaderManager.getInstance(this).initLoader(ID_DETAIL_LOADER,null ,this);

//        Intent intent = getIntent();
//        if(intent!=null){
//            if(intent.hasExtra(Intent.EXTRA_TEXT)){
//                mForecast= intent.getStringExtra(Intent.EXTRA_TEXT);
//               // mWeatherDisplay.setText(mForecast);
//            }
//        }
    }

    public Intent createShareForecastIntent(){
        Intent shareIntent= new ShareCompat.IntentBuilder(DetailActivity.this).setType("text/plain").setText(mForecast + FORECAST_SHARE_HASHTAG).getIntent();
        return shareIntent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.details,menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id== R.id.action_settings){
            Intent startSettingActivity = new Intent(this, SettingActivity.class);
            startActivity(startSettingActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId,  Bundle loaderArgs) {
        switch(loaderId){
            case ID_DETAIL_LOADER:
                return new CursorLoader(this, mUri,WEATHER_DETAIL_PROJECTION,null,null,null);

            default:
                throw new RuntimeException("Loader Not Implemented" + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        boolean cursorHaseValidData = false;
        if(data != null && data.moveToFirst()){
            cursorHaseValidData= true;
        }

        if(!cursorHaseValidData){
            return;
        }

        int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
        int weatherImageId = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);
        mDetailBinding.primaryInfo.weatherIcon.setImageResource(weatherImageId);


        long localDateMidnightGmt = data.getLong(INDEX_WEATHER_DATE);
        String dateText = SunshineDateUtils.getFriendlyDateString(this,localDateMidnightGmt,true);
        mDetailBinding.primaryInfo.date.setText(dateText);

//        int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(this,weatherId);
        String descriptionA11y = getString(R.string.a11y_forecast, description);
        mDetailBinding.primaryInfo.weatherDescription.setText(description);
        mDetailBinding.primaryInfo.weatherDescription.setContentDescription(descriptionA11y);
        mDetailBinding.primaryInfo.weatherIcon.setContentDescription(descriptionA11y);


        double highInCelcius = data.getDouble(INDEX_WEATHER_MAX_TEMP);
        String highString = SunshineWeatherUtils.formatTemperature(this,highInCelcius);
        String highA11y = getString(R.string.a11y_high_temp, highString);
        mDetailBinding.primaryInfo.highTemperature.setText(highString);
        mDetailBinding.primaryInfo.highTemperature.setContentDescription(highA11y);

        double lowInCelcius = data.getDouble(INDEX_WEATHER_MIN_TEMP);
        String lowString = SunshineWeatherUtils.formatTemperature(this,lowInCelcius);
        String lowA11y = getString(R.string.a11y_low_temp, lowString);
        mDetailBinding.primaryInfo.lowTemperature.setText(lowString);
        mDetailBinding.primaryInfo.lowTemperature.setContentDescription(lowA11y);

        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);
        String humidityA11y = getString(R.string.a11y_humidity, humidityString);
        mDetailBinding.extraDetails.humidity.setText(humidityString);
        mDetailBinding.extraDetails.humidity.setContentDescription(humidityA11y);

        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection);
        String windA11y = getString(R.string.a11y_wind, windString);
        mDetailBinding.extraDetails.windMeasurement.setText(windString);
        mDetailBinding.extraDetails.windMeasurement.setContentDescription(windA11y);
        mDetailBinding.extraDetails.windLabel.setContentDescription(windA11y);

        float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);
        String pressureString = getString(R.string.format_pressure, pressure);
        String pressureA11y = getString(R.string.a11y_pressure, pressureString);
        mDetailBinding.extraDetails.pressure.setText(pressureString);
        mDetailBinding.extraDetails.pressure.setContentDescription(pressureA11y);
        mDetailBinding.extraDetails.pressureLabel.setContentDescription(pressureA11y);


        mForecastSummary = String.format("%s - %s - %s/%s",
                dateText, description, highString, lowString);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}