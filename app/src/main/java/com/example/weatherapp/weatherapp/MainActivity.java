package com.example.weatherapp.weatherapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.weatherapp.weatherapp.adapters.ForecastAdapter;
import com.example.weatherapp.weatherapp.data.AppPreferences;
import com.example.weatherapp.weatherapp.sync.WeatherAppSyncUtils;
import com.example.weatherapp.weatherapp.utilities.NetworkUtils;

import static com.example.weatherapp.weatherapp.data.WeatherContract.WeatherEntry;

public class MainActivity extends AppCompatActivity implements
        ForecastAdapter.ForecastAdapterOnClickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int WEATHER_LOADER_ID = 42;

    private static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_TEMP,
    };

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private ProgressBar mLoadingProgressBar;
    private int mPosition = RecyclerView.NO_POSITION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_forecast);
        mForecastAdapter = new ForecastAdapter(this, this);
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_refresh_indicator);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mForecastAdapter);

        showLoadingIndicator();
        getSupportLoaderManager().initLoader(WEATHER_LOADER_ID, null, this);

        WeatherAppSyncUtils.initialize(this);
    }

    private void showForecast() {
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoadingIndicator() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItemId = item.getItemId();
        if (selectedMenuItemId == R.id.action_show_map) {
            String forecastLocation = AppPreferences.getPreferredForecastLocation(this);
            Uri forecastLocationUri = NetworkUtils.buildMapUri(forecastLocation);
            Intent showMapIntent = new Intent(Intent.ACTION_VIEW, forecastLocationUri);
            if (showMapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(showMapIntent);
            } else {
                Log.d(TAG, "Couldn't open map, no needed app installed.");
            }
            return true;
        } else if (selectedMenuItemId == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListWeatherItemClick(long clickedItemDate) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.setData(WeatherEntry.buildWeatherUriWithDate(clickedItemDate));
        startActivity(detailIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle args) {

        switch (id) {
            case WEATHER_LOADER_ID:
                return new CursorLoader(this,
                        WeatherEntry.CONTENT_URI,
                        MAIN_FORECAST_PROJECTION,
                        WeatherEntry.COLUMN_DATE + " >= " + 0,
                        null,
                        WeatherEntry.COLUMN_DATE + " ASC");
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) {
            mPosition = 0;
        }
        mRecyclerView.scrollToPosition(mPosition);
        if (data.getCount() != 0) {
            showForecast();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

}
