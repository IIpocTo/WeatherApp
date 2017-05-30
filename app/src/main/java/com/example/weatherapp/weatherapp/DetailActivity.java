package com.example.weatherapp.weatherapp;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_HASHTAG = "#WeatherApp";

    private TextView mDetailForecastTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDetailForecastTextView = (TextView) findViewById(R.id.tv_detail_forecast);

        Intent intentThatStartedActivity = getIntent();
        if (intentThatStartedActivity != null) {
            if (intentThatStartedActivity.hasExtra(Intent.EXTRA_TEXT)) {
                String forecastData = intentThatStartedActivity.getStringExtra(Intent.EXTRA_TEXT);
                mDetailForecastTextView.setText(forecastData);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedMenuItemId = item.getItemId();
        if (selectedMenuItemId == R.id.action_share) {
            ShareCompat.IntentBuilder.from(this)
                    .setType("plain/text")
                    .setText(mDetailForecastTextView.getText() + FORECAST_HASHTAG)
                    .startChooser();
            return true;
        } else if (selectedMenuItemId == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
