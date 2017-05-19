package com.example.weatherapp.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.weatherapp.weatherapp.adapters.ForecastAdapter;
import com.example.weatherapp.weatherapp.data.AppPreferences;
import com.example.weatherapp.weatherapp.utilities.NetworkUtils;
import com.example.weatherapp.weatherapp.utilities.OpenWeatherJsonUtils;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ForecastAdapter mForecastAdapter;
    private TextView mRefreshErrorTextView;
    private ProgressBar mRefreshProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_forecast);
        mForecastAdapter = new ForecastAdapter();
        mRefreshErrorTextView = (TextView) findViewById(R.id.tv_error_refresh_message);
        mRefreshProgressBar = (ProgressBar) findViewById(R.id.pb_refresh_indicator);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mForecastAdapter);

        loadWeatherData();
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
            Toast.makeText(this, R.string.toast_refresh_message, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadWeatherData() {
        String location = AppPreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    private void showForecast() {
        mRefreshErrorTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mRefreshErrorTextView.setVisibility(View.VISIBLE);
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            mRefreshProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String location = params[0];
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
        protected void onPostExecute(String[] weatherData) {
            mRefreshProgressBar.setVisibility(View.INVISIBLE);
            if (weatherData != null) {
                mForecastAdapter.setWeatherData(weatherData);
            } else {
                showErrorMessage();
            }
        }
    }

}
