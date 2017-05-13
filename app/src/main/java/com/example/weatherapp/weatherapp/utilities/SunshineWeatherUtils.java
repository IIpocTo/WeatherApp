package com.example.weatherapp.weatherapp.utilities;

import android.content.Context;
import com.example.weatherapp.weatherapp.R;
import com.example.weatherapp.weatherapp.data.SunshinePreferences;

public final class SunshineWeatherUtils {

    private static double celsiusToFahrenheit(double temperatureInCelsius) {
        return temperatureInCelsius * 1.8 + 32;
    }

    public static String formatTemperature(Context context, double temperature) {
        int temperatureFormat = R.string.format_temperature_celsius;
        if (!SunshinePreferences.isMetric(context)) {
            temperature = celsiusToFahrenheit(temperature);
            temperatureFormat = R.string.format_temperature_fahrenheit;
        }
        return String.format(context.getString(temperatureFormat), temperature);
    }

    public static String formatHighLows(Context context, double high, double low) {
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);
        String formattedHigh = formatTemperature(context, roundedHigh);
        String formattedLow = formatTemperature(context, roundedLow);
        return formattedHigh + " / " + formattedLow;
    }

}
