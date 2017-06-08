package com.example.weatherapp.weatherapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.weatherapp.weatherapp.utilities.DateTimeUtils;
import com.example.weatherapp.weatherapp.utilities.WeatherUtils;

import static com.example.weatherapp.weatherapp.data.WeatherContract.WeatherEntry;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String FORECAST_HASHTAG = "#WeatherApp";
    private static final int DETAIL_LOADER_ID = 44;

    private static final String[] DETAIL_FORECAST_PROJECTION = {
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_WIND_DIR
    };

    private String mForecastSummary;
    private TextView mWeatherDate;
    private TextView mWeatherDescription;
    private TextView mWeatherHighTemp;
    private TextView mWeatherLowTemp;
    private TextView mWeatherHumidity;
    private TextView mWeatherPressure;
    private TextView mWeatherWind;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mWeatherDate = (TextView) findViewById(R.id.tv_detail_date);
        mWeatherDescription = (TextView) findViewById(R.id.tv_detail_description);
        mWeatherHighTemp = (TextView) findViewById(R.id.tv_detail_high_temp);
        mWeatherLowTemp = (TextView) findViewById(R.id.tv_detail_low_temp);
        mWeatherHumidity = (TextView) findViewById(R.id.tv_detail_humidity);
        mWeatherPressure = (TextView) findViewById(R.id.tv_detail_pressure);
        mWeatherWind = (TextView) findViewById(R.id.tv_detail_wind);

        Intent intentThatStartedActivity = getIntent();
        if (intentThatStartedActivity != null) {
            mUri = intentThatStartedActivity.getData();
            if (mUri == null) {
                throw new NullPointerException("Provided URI is null.");
            }
            LoaderManager detailLoaderManager = getSupportLoaderManager();
            Loader<Object> detailForecastLoader = detailLoaderManager.getLoader(DETAIL_LOADER_ID);
            if (detailForecastLoader == null) {
                detailLoaderManager.initLoader(DETAIL_LOADER_ID, new Bundle(), this);
            } else {
                detailLoaderManager.restartLoader(DETAIL_LOADER_ID, new Bundle(), this);
            }
        }

    }

    private Intent createShareForecastIntent() {
        return ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_HASHTAG)
                .getIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        menuItem.setIntent(createShareForecastIntent());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItemId = item.getItemId();
        if (selectedMenuItemId == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {
            case DETAIL_LOADER_ID:
                return new CursorLoader(this,
                        mUri,
                        DETAIL_FORECAST_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {

            String dateString = DateTimeUtils.getReadableDateString(this, data.getLong(WeatherEntry.INDEX_COLUMN_DATE));
            int weatherId = data.getInt(WeatherEntry.INDEX_COLUMN_WEATHER_ID);
            String weatherDescription = WeatherUtils.getStringForWeatherCondition(this, weatherId);
            String highTemp = WeatherUtils.formatTemperature(this, data.getDouble(WeatherEntry.INDEX_COLUMN_MAX));
            String lowTemp = WeatherUtils.formatTemperature(this, data.getDouble(WeatherEntry.INDEX_COLUMN_MIN));
            float humidity = data.getFloat(WeatherEntry.INDEX_COLUMN_HUMIDITY);
            String humidityString = getString(R.string.format_humidity, humidity);
            float pressure = data.getFloat(WeatherEntry.INDEX_COLUMN_PRESSURE);
            String pressureString = getString(R.string.format_pressure, pressure);
            float windDirection = data.getFloat(WeatherEntry.INDEX_COLUMN_WIND_DIR);
            float windSpeed = data.getFloat(WeatherEntry.INDEX_COLUMN_WIND_SPEED);
            String formattedWindInfo = WeatherUtils.getFormattedWind(this, windSpeed, windDirection);

            mWeatherDate.setText(dateString);
            mWeatherDescription.setText(weatherDescription);
            mWeatherHighTemp.setText(highTemp);
            mWeatherLowTemp.setText(lowTemp);
            mWeatherPressure.setText(pressureString);
            mWeatherHumidity.setText(humidityString);
            mWeatherWind.setText(formattedWindInfo);

            mForecastSummary = String.format("%s - %s - %s/%s - %s - %s - %s", dateString, weatherDescription,
                    highTemp, lowTemp, humidityString, pressureString, formattedWindInfo);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
