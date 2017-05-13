package com.example.weatherapp.weatherapp.data;

import android.content.Context;

public class SunshinePreferences {

    private static final String DEFAULT_WEATHER_LOCATION = "94043,USA";

    public static boolean isMetric(Context context) {
        return true;
    }

    public static String getPrefferedWeatherLocation(Context context) {
        return DEFAULT_WEATHER_LOCATION;
    }

}
