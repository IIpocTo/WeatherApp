package com.example.weatherapp.weatherapp.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.weatherapp.weatherapp.R;

public class AppPreferences {

    public static boolean isMetric(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String chosenUnit = sharedPreferences.getString(context.getString(R.string.unit_key),
                context.getString(R.string.default_unit));
        return chosenUnit.equals(context.getString(R.string.units_metric));
    }

    public static String getPreferredForecastLocation(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(context.getString(R.string.forecast_location_key),
                context.getString(R.string.default_forecast_location));
    }

}
