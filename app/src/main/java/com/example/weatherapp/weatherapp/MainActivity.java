package com.example.weatherapp.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.example.weatherapp.weatherapp.data.AppPreferences;
import com.example.weatherapp.weatherapp.utilities.NetworkUtils;
import com.example.weatherapp.weatherapp.utilities.OpenWeatherJsonUtils;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);

        loadWeatherData();
    }

    private void loadWeatherData() {
        String location = AppPreferences.getPreferredWeatherLocation(this);
        new FetchOWMData().execute(location);
    }

    private class FetchOWMData extends AsyncTask<String, Void, String[]> {

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
            if (weatherData != null) {
                for (String weather : weatherData) {
                    mWeatherTextView.append(weather + "\n\n\n");
                }
            }
        }
    }

}
