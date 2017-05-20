package com.example.weatherapp.weatherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private TextView mDetailForecastTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDetailForecastTextView = (TextView) findViewById(R.id.tv_detail_forecast);

        Intent intentThatStartedActivity = getIntent();
        if (intentThatStartedActivity.hasExtra(Intent.EXTRA_TEXT)) {
            String forecastData = intentThatStartedActivity.getStringExtra(Intent.EXTRA_TEXT);
            mDetailForecastTextView.setText(forecastData);
        }

    }
}
