package com.example.weatherapp.weatherapp.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public final class OpenWeatherJsonUtils {

    private static final String OWM_LIST = "list";
    private static final String OWM_MAX = "temp_max";
    private static final String OWM_MIN = "temp_min";
    private static final String OWM_WEATHER = "weather";
    private static final String OWM_DESCRIPTION = "main";
    private static final String OWM_MESSAGE_CODE = "cod";
    private static final String OWM_DATETIME = "dt";

    public static String[] getSimpleWeatherStringFromJson(Context context, String forecastJsonString)
            throws JSONException {

        String[] parsedWeatherData;
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
        parsedWeatherData = new String[weatherArray.length()];

        for (int i = 0; i < weatherArray.length(); i++) {

            JSONObject forecast = weatherArray.getJSONObject(i);
            long dateTimeMillis = forecast.getLong(OWM_DATETIME);
            String date = DateTimeUtils.getReadableDateString(context, dateTimeMillis);

            JSONObject weatherObject = forecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            String description = weatherObject.getString(OWM_DESCRIPTION);

            JSONObject temperatureObject = forecast.getJSONObject(OWM_DESCRIPTION);
            double highTempInKelvins = temperatureObject.getDouble(OWM_MAX);
            double lowTempInKelvins = temperatureObject.getDouble(OWM_MIN);
            double high = WeatherUtils.kelvinToCelsius(highTempInKelvins);
            double low = WeatherUtils.kelvinToCelsius(lowTempInKelvins);
            String highAndLow = WeatherUtils.formatHighLows(context, high, low);

            parsedWeatherData[i] = date + " - " + description + " - " + highAndLow;

        }

        return parsedWeatherData;

    }

}
