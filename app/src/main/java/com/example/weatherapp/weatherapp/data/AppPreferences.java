package com.example.weatherapp.weatherapp.data;

import android.content.Context;

public class AppPreferences {

    private static final String DEFAULT_WEATHER_LOCATION = "Moscow";

    public static boolean isMetric(Context context) {
        return true;
    }

    public static String getPreferredWeatherLocation(Context context) {
        return DEFAULT_WEATHER_LOCATION;
    }

}
