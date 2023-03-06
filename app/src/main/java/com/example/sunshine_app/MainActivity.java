package com.example.sunshine_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.sunshine.R;
import com.example.sunshine_app.data.SunshinePreferences;
import com.example.sunshine_app.data.WeatherContract;
import com.example.sunshine_app.sync.SunshineSyncUtils;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private ForecastAdapter mforecastAdapter;
    private int mPosition= RecyclerView.NO_POSITION;

    private ProgressBar mLoadingIndicator;
    private static final int FORECAST_LOADER_ID=0;
    private static boolean PREFERENCES_HAVE_BEEN_UPDATED =false;

    public static final String[] MAIN_FORECAST_PROJECTION={
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    public static  final int INDEX_WEATHER_DATE=0;
    public static  final int INDEX_WEATHER_MAX_TEMP=1;
    public static  final int INDEX_WEATHER_MIN_TEMP=2;
    public static  final int INDEX_WEATHER_CONDITION_ID=3;

    private static final int ID_FORECAST_LOADER = 44;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0f);
       // mWeatherListTextView= findViewById(R.id.tv_weather_data);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.recyclerview_forecast);


        //FakeDataUtils.insertFakeData(this);


        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);



        mforecastAdapter = new ForecastAdapter(this,this);
        mRecyclerView.setAdapter(mforecastAdapter);

        showLoading();

        int loaderId = FORECAST_LOADER_ID;
//        LoaderManager.LoaderCallbacks<String[]> callback =MainActivity.this;
//        Bundle bundleForLoader = null;
        LoaderManager.getInstance(this).initLoader(ID_FORECAST_LOADER,null,this);

        SunshineSyncUtils.initialize(this);
      }




    @Override
    public void onClick( long date){
       // Context context = this;
        Intent WeatherDetailIntent = new Intent(this,DetailActivity.class);
        Uri uriForDateClicked = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
        WeatherDetailIntent.setData(uriForDateClicked);
        startActivity(WeatherDetailIntent);
    }



    private void showWeatherDataView(){
        mRecyclerView.setVisibility(View.VISIBLE);
    }





    private void openPreferredLocationInMap(){
        double[] coords = SunshinePreferences.getLocationCoordinates(this);
        String posLat= Double.toString(coords[0]);
        String posLong= Double.toString(coords[1]);
        Uri geoLocation= Uri.parse("geo:" + posLat + "," + posLong);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if(intent.resolveActivity(getPackageManager())!= null){
            startActivity(intent);
        }
        else{
            Log.d(TAG,"Couldn't call"+ geoLocation.toString()+",no receiving apps installed");
        }
    }


    @Override
    public Loader<Cursor>onCreateLoader(int loaderId, Bundle bundle){
        switch (loaderId){
            case  ID_FORECAST_LOADER:
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;
                String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
                String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();

                return new CursorLoader(this,forecastQueryUri,MAIN_FORECAST_PROJECTION,selection,null,sortOrder);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data){
        mforecastAdapter.swapCursor(data);
        if(mPosition == RecyclerView.NO_POSITION) mPosition=0;
        mRecyclerView.smoothScrollToPosition(mPosition);

        if(data.getCount()!=0)
            showWeatherDataView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mforecastAdapter.swapCursor(null);
    }


//    @Override
//    protected void onStart(){
//        super.onStart();
//
//        if(PREFERENCES_HAVE_BEEN_UPDATED){
//            Log.d(TAG,"onStart: preferences were updated");
//            LoaderManager.getInstance(this).restartLoader(FORECAST_LOADER_ID,null,this);
//            PREFERENCES_HAVE_BEEN_UPDATED=false;
//        }
//    }
//
//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//
//        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
//    }


    private void showLoading(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forecast,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

//        if(id == R.id.action_refresh){
//            invalidateDaa();
//            LoaderManager.getInstance(this).initLoader(FORECAST_LOADER_ID,null,this);
//            //getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID,null,this);
//            return true;
//        }

        if(id==R.id.action_map){
            openPreferredLocationInMap();
        }

        if(id==R.id.action_settings){
            Intent startSettingActivity = new Intent(this, SettingActivity.class);
            startActivity(startSettingActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}