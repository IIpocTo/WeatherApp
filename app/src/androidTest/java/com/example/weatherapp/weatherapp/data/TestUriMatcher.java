package com.example.weatherapp.weatherapp.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TestUriMatcher {

    private static final Uri TEST_WEATHER_DIR = WeatherContract.WeatherEntry.CONTENT_URI;
    private static final Uri TEST_WEATHER_WITH_DATE_DIR =
            WeatherContract.WeatherEntry.buildWeatherUriWithDate(TestUtilities.TEST_DATE);

    private UriMatcher testMatcher;

    @Before
    public void setUp() throws Exception {
        testMatcher = WeatherProvider.buildUriMatcher();
    }

    @Test
    public void testUriMatcher() throws Exception {

        int actualWeatherCode = testMatcher.match(TEST_WEATHER_DIR);
        String weatherUriDoesNotMatch = "Error: The CODE_WEATHER URI was matched incorrectly.";
        assertEquals(weatherUriDoesNotMatch, WeatherProvider.CODE_WEATHER, actualWeatherCode);

        actualWeatherCode = testMatcher.match(TEST_WEATHER_WITH_DATE_DIR);
        String weatherWithDateUriCodeDoesNotMatch = "Error: The CODE_WEATHER WITH DATE URI was matched incorrectly.";
        assertEquals(weatherWithDateUriCodeDoesNotMatch, WeatherProvider.CODE_WEATHER_WITH_DATE, actualWeatherCode);

    }
}
