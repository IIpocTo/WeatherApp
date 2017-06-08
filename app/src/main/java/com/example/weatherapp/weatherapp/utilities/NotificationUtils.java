package com.example.weatherapp.weatherapp.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.example.weatherapp.weatherapp.DetailActivity;
import com.example.weatherapp.weatherapp.R;
import com.example.weatherapp.weatherapp.data.AppPreferences;

import static com.example.weatherapp.weatherapp.data.WeatherContract.WeatherEntry;

public class NotificationUtils {

    private static final int WEATHER_NOTIFICATION_ID = 3004;
    private static final String[] WEATHER_NOTIFICATION_PROJECTION = {
            WeatherEntry.COLUMN_WEATHER_ID,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP
    };

    private static final int INDEX_COLUMN_WEATHER_ID = 0;
    private static final int INDEX_COLUMN_MIN = 1;
    private static final int INDEX_COLUMN_MAX = 2;

    private static String getNotificationText(Context context, int weatherId, double high, double low) {
        String description = WeatherUtils.getStringForWeatherCondition(context, weatherId);
        String notificationFormat = context.getString(R.string.format_notification);
        return String.format(notificationFormat,
                description,
                WeatherUtils.formatTemperature(context, high),
                WeatherUtils.formatTemperature(context, low));
    }

    public static void notifyUserOfNewWeather(Context context) {

        Uri weatherUri = WeatherEntry.buildWeatherUriWithDate(System.currentTimeMillis());
        Cursor todayWeatherCursor = context.getContentResolver().query(
                weatherUri,
                WEATHER_NOTIFICATION_PROJECTION,
                null,
                null,
                null);

        if (todayWeatherCursor.moveToFirst()) {

            int weatherId = todayWeatherCursor.getInt(INDEX_COLUMN_WEATHER_ID);
            double maxTemp = todayWeatherCursor.getDouble(INDEX_COLUMN_MAX);
            double minTemp = todayWeatherCursor.getDouble(INDEX_COLUMN_MIN);

            Resources resources = context.getResources();
            int largeArtResourceId = WeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);
            Bitmap largeIcon = BitmapFactory.decodeResource(resources, largeArtResourceId);
            String notificationTitle = context.getString(R.string.app_name);
            String notificationText = getNotificationText(context, weatherId, maxTemp, minTemp);
            int smallArtResourceId = WeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(smallArtResourceId)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setAutoCancel(false);

            Intent detailIntent = new Intent(context, DetailActivity.class);
            detailIntent.setData(weatherUri);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(detailIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder.setContentIntent(resultPendingIntent);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(WEATHER_NOTIFICATION_ID, notificationBuilder.build());
            AppPreferences.saveLastNotificationTime(context, System.currentTimeMillis());

            todayWeatherCursor.close();
        }

    }

}
