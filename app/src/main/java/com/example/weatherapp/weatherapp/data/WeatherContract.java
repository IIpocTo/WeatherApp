package com.example.weatherapp.weatherapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class WeatherContract {

    public static final String AUTHORITY = "com.example.weatherapp.weatherapp";
    public static final String PATH = "weather";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();
        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_WIND_DIR = "direction";

        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(date)).build();
        }

    }

}
