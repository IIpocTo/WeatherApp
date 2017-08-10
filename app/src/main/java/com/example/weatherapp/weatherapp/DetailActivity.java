package com.example.weatherapp.weatherapp;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.weatherapp.weatherapp.databinding.ActivityDetailBinding;
import com.example.weatherapp.weatherapp.utilities.DateTimeUtils;
import com.example.weatherapp.weatherapp.utilities.WeatherUtils;

import static com.example.weatherapp.weatherapp.data.WeatherContract.WeatherEntry;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String FORECAST_HASHTAG = "#WeatherApp";
    private static final int DETAIL_LOADER_ID = 44;

    private static final String[] DETAIL_FORECAST_PROJECTION = {
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_WIND_DIR
    };

    private String mForecastSummary;
    private Uri mUri;
    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent intentThatStartedActivity = getIntent();
        if (intentThatStartedActivity != null) {
            mUri = intentThatStartedActivity.getData();
            if (mUri == null) {
                throw new NullPointerException("Provided URI is null.");
            }
            getSupportLoaderManager().initLoader(DETAIL_LOADER_ID, new Bundle(), this);
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
            int weatherIconId = WeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);
            String weatherDescription = WeatherUtils.getStringForWeatherCondition(this, weatherId);
            String temperature = WeatherUtils.formatTemperature(this, data.getDouble(WeatherEntry.INDEX_COLUMN_TEMP));
            float humidity = data.getFloat(WeatherEntry.INDEX_COLUMN_HUMIDITY);
            String humidityString = getString(R.string.format_humidity, humidity);
            float pressure = data.getFloat(WeatherEntry.INDEX_COLUMN_PRESSURE);
            String pressureString = getString(R.string.format_pressure, pressure);
            float windDirection = data.getFloat(WeatherEntry.INDEX_COLUMN_WIND_DIR);
            float windSpeed = data.getFloat(WeatherEntry.INDEX_COLUMN_WIND_SPEED);
            String formattedWindInfo = WeatherUtils.getFormattedWind(this, windSpeed, windDirection);

            mDetailBinding.primaryWeather.date.setText(dateString);
            mDetailBinding.primaryWeather.weatherDescription.setText(weatherDescription);
            mDetailBinding.primaryWeather.temp.setText(temperature);
            mDetailBinding.primaryWeather.weatherIcon.setImageResource(weatherIconId);
            mDetailBinding.extraDetails.pressure.setText(pressureString);
            mDetailBinding.extraDetails.humidity.setText(humidityString);
            mDetailBinding.extraDetails.wind.setText(formattedWindInfo);

            mForecastSummary = String.format("%s - %s - %s - %s - %s - %s", dateString, weatherDescription,
                    temperature, humidityString, pressureString, formattedWindInfo);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}

}
