package com.example.weatherapp.weatherapp.utilities;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

import static com.example.weatherapp.weatherapp.data.WeatherContract.*;

public final class OpenWeatherJsonUtils {

    private static final String OWM_LIST = "list";
    private static final String OWM_MAX = "temp_max";
    private static final String OWM_MIN = "temp_min";
    private static final String OWM_WEATHER = "weather";
    private static final String OWM_WEATHER_ID = "id";
    private static final String OWM_MESSAGE_CODE = "cod";
    private static final String OWM_DATETIME = "dt";
    private static final String OWM_MAIN_OBJECT = "main";
    private static final String OWM_HUMIDITY = "humidity";
    private static final String OWM_PRESSURE = "pressure";
    private static final String OWM_WIND_OBJECT = "wind";
    private static final String OWM_WIND_SPEED = "speed";
    private static final String OWM_WIND_DIRECTION = "deg";

    public static ContentValues[] getWeatherContentValuesFromJson(String forecastJsonString) throws JSONException {

        JSONObject forecastJson = new JSONObject(forecastJsonString);

        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);
            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    return null;
                default:
                    return null;
            }
        }

        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
        ContentValues[] parsedContentValuesData = new ContentValues[weatherArray.length()];

        for (int i = 0; i < weatherArray.length(); i++) {

            JSONObject forecast = weatherArray.getJSONObject(i);
            long dateTimeMillis = forecast.getLong(OWM_DATETIME);

            JSONObject weatherObject = forecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            String weatherId = weatherObject.getString(OWM_WEATHER_ID);

            JSONObject mainWeatherObject = forecast.getJSONObject(OWM_MAIN_OBJECT);
            double maxTempInKelvins = mainWeatherObject.getDouble(OWM_MAX);
            double minTempInKelvins = mainWeatherObject.getDouble(OWM_MIN);
            int humidity = mainWeatherObject.getInt(OWM_HUMIDITY);
            double pressure = mainWeatherObject.getDouble(OWM_PRESSURE);

            JSONObject windObject = forecast.getJSONObject(OWM_WIND_OBJECT);
            double windSpeed = windObject.getDouble(OWM_WIND_SPEED);
            double windDirection = windObject.getDouble(OWM_WIND_DIRECTION);

            ContentValues weatherContentValues = new ContentValues();
            weatherContentValues.put(WeatherEntry.COLUMN_DATE, dateTimeMillis);
            weatherContentValues.put(WeatherEntry.COLUMN_WEATHER_ID, weatherId);
            weatherContentValues.put(WeatherEntry.COLUMN_MIN_TEMP, WeatherUtils.kelvinToCelsius(minTempInKelvins));
            weatherContentValues.put(WeatherEntry.COLUMN_MAX_TEMP, WeatherUtils.kelvinToCelsius(maxTempInKelvins));
            weatherContentValues.put(WeatherEntry.COLUMN_HUMIDITY, humidity);
            weatherContentValues.put(WeatherEntry.COLUMN_PRESSURE, pressure);
            weatherContentValues.put(WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
            weatherContentValues.put(WeatherEntry.COLUMN_WIND_DIR, windDirection);

            parsedContentValuesData[i] = weatherContentValues;

        }

        return parsedContentValuesData;

    }

}
