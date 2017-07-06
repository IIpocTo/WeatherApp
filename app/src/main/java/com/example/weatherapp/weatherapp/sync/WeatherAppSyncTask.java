package com.example.weatherapp.weatherapp.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.example.weatherapp.weatherapp.data.AppPreferences;
import com.example.weatherapp.weatherapp.utilities.NetworkUtils;
import com.example.weatherapp.weatherapp.utilities.NotificationUtils;
import com.example.weatherapp.weatherapp.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static com.example.weatherapp.weatherapp.data.WeatherContract.WeatherEntry;

public class WeatherAppSyncTask {

    synchronized static void syncWeather(Context context) {
        try {
            URL weatherRequestUrl = NetworkUtils.buildUrl(context);
            String response = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
            ContentValues[] weatherContentValues = OpenWeatherJsonUtils.getWeatherContentValuesFromJson(response);
            if (weatherContentValues != null && weatherContentValues.length != 0) {
                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.delete(WeatherEntry.CONTENT_URI, null, null);
                contentResolver.bulkInsert(WeatherEntry.CONTENT_URI, weatherContentValues);
                boolean notificationsEnabled = AppPreferences.isNotificationsEnabled(context);
                long timeSinceLastNotification = AppPreferences.getElapsedTimeSinceLastNotification(context);
                boolean threeHoursPassedSinceLastNotification = timeSinceLastNotification >= TimeUnit.HOURS.toMillis(3);
                if (notificationsEnabled && threeHoursPassedSinceLastNotification) {
                    NotificationUtils.notifyUserOfNewWeather(context);
                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

}
