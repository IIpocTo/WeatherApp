package com.example.weatherapp.weatherapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.weatherapp.weatherapp.adapters.ForecastAdapter;
import com.example.weatherapp.weatherapp.data.AppPreferences;
import com.example.weatherapp.weatherapp.utilities.NetworkUtils;
import com.example.weatherapp.weatherapp.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements ForecastAdapter.ForecastAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String[]> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int WEATHER_LOADER_ID = 42;
    private static final String WEATHER_SEARCH_LOCATION = "search_location";

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private TextView mRefreshErrorTextView;
    private ProgressBar mRefreshProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_forecast);
        mForecastAdapter = new ForecastAdapter(this);
        mRefreshErrorTextView = (TextView) findViewById(R.id.tv_error_refresh_message);
        mRefreshProgressBar = (ProgressBar) findViewById(R.id.pb_refresh_indicator);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mForecastAdapter);

        loadWeatherData();
    }

    private void loadWeatherData() {

        String location = AppPreferences.getPreferredWeatherLocation(this);
        Bundle weatherBundle = new Bundle();
        weatherBundle.putString(WEATHER_SEARCH_LOCATION, location);

        LoaderManager weatherLoaderManager = getSupportLoaderManager();
        Loader<Object> weatherLoader = weatherLoaderManager.getLoader(WEATHER_LOADER_ID);
        if (weatherLoader == null) {
            weatherLoaderManager.initLoader(WEATHER_LOADER_ID, weatherBundle, this);
        } else {
            weatherLoaderManager.restartLoader(WEATHER_LOADER_ID, weatherBundle, this);
        }

    }

    private void showForecast() {
        mRefreshErrorTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mRefreshErrorTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItemId = item.getItemId();
        if (selectedMenuItemId == R.id.action_refresh) {
            mForecastAdapter.setWeatherData(null);
            loadWeatherData();
            return true;
        } else if (selectedMenuItemId == R.id.action_show_map) {
            Uri forecastLocationUri = NetworkUtils.buildMapUri();
            Intent showMapIntent = new Intent(Intent.ACTION_VIEW, forecastLocationUri);
            if (showMapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(showMapIntent);
            } else {
                Log.d(TAG, "Couldn't open map, no needed app installed.");
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListWeatherItemClick(String clickedData) {
        Intent detailIntent = new Intent(this, DetailActivity.class);
        detailIntent.putExtra(Intent.EXTRA_TEXT, clickedData);
        startActivity(detailIntent);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String[]>(this) {

            private String[] mWeatherData;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if (args != null) {
                    mRefreshProgressBar.setVisibility(View.VISIBLE);
                    if (mWeatherData != null) {
                        deliverResult(mWeatherData);
                    } else {
                        forceLoad();
                    }
                }

            }

            @Override
            public String[] loadInBackground() {
                String location = args.getString(WEATHER_SEARCH_LOCATION);
                URL weatherRequestUrl = NetworkUtils.buildUrl(location);
                try {
                    String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                    return OpenWeatherJsonUtils
                            .getSimpleWeatherStringFromJson(MainActivity.this, jsonWeatherResponse);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(String[] data) {
                mWeatherData = data;
                super.deliverResult(data);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        mRefreshProgressBar.setVisibility(View.INVISIBLE);
        if (data != null) {
            showForecast();
            mForecastAdapter.setWeatherData(data);
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

}
