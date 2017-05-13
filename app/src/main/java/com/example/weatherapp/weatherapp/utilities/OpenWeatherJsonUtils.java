package com.example.weatherapp.weatherapp.utilities;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public final class OpenWeatherJsonUtils {

    public static String[] getSimpleWeatherStringFromJson(Context context, String forecastJsonString)
            throws JSONException {

        final String OWM_LIST = "list";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_MESSAGE_CODE = "cod";

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

        long localDate = System.currentTimeMillis();
        long utcDate = DateUtils.getUTCDateFromLocal(localDate);
        long startDate = DateUtils.normalizeDate(utcDate);

        for (int i = 0; i < weatherArray.length(); i++) {

            JSONObject dayForecast = weatherArray.getJSONObject(i);
            long dateTimeMillis = startDate + DateUtils.DAY_IN_MILLIS * i;
            String date = DateUtils.getFriendlyDateString(context, dateTimeMillis, false);

            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            String description = weatherObject.getString(OWM_DESCRIPTION);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);
            String highAndLow = WeatherUtils.formatHighLows(context, high, low);

            parsedWeatherData[i] = date + " - " + description + " - " + highAndLow;

        }

        return parsedWeatherData;

    }

}
