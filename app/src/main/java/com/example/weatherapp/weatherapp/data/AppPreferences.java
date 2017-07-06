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

    public static boolean isNotificationsEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean defaultShowNotifications = context.getResources().getBoolean(R.bool.show_notifications);
        return sharedPreferences.getBoolean(displayNotificationsKey, defaultShowNotifications);
    }

    public static void saveLastNotificationTime(Context context, long timeOfNotification) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        editor.putLong(lastNotificationKey, timeOfNotification);
        editor.apply();
    }

    public static long getElapsedTimeSinceLastNotification(Context context) {
        long lastModificationTime = getLastModificationTime(context);
        return System.currentTimeMillis() - lastModificationTime;
    }

    private static long getLastModificationTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String lastNotificationKey = context.getString(R.string.pref_last_notification);
        return sharedPreferences.getLong(lastNotificationKey, 0);
    }

}
