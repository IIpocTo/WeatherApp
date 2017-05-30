package com.example.weatherapp.weatherapp.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String APP_ID = "04a47a051709b3ba7442b022655aa670";

    private static final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast";
    private static final String QUERY_PARAM = "q";
    private static final String APP_PARAM = "appid";
    private static final String GOOGLE_MAPS_SCHEME = "geo";

    public static URL buildUrl(String locationQuery) {
        URL url = null;
        Uri uri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, locationQuery)
                .appendQueryParameter(APP_PARAM, APP_ID)
                .build();
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static Uri buildMapUri(String location) {
        return new Uri.Builder().scheme(GOOGLE_MAPS_SCHEME)
                .appendPath("0,0")
                .appendQueryParameter(QUERY_PARAM, location)
                .build();
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
