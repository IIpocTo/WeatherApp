package com.example.weatherapp.weatherapp.data;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Map;
import java.util.Set;

import static com.example.weatherapp.weatherapp.data.WeatherContract.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

class TestUtilities {

    static ContentValues createTestWeatherContentValues() {
        ContentValues testContentValues = new ContentValues();
        testContentValues.put(WeatherEntry.COLUMN_WEATHER_ID, 3212);
        testContentValues.put(WeatherEntry.COLUMN_DATE, 1475280000000L);
        testContentValues.put(WeatherEntry.COLUMN_MIN_TEMP, 30);
        testContentValues.put(WeatherEntry.COLUMN_MAX_TEMP, 50);
        testContentValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        testContentValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        testContentValues.put(WeatherEntry.COLUMN_WIND_DIR, 1.1);
        testContentValues.put(WeatherEntry.COLUMN_WIND_SPEED, 1.5);
        return testContentValues;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {

            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);

            String columnNotFoundError = "Column '" + columnName + "' not found. " + error;
            assertFalse(columnNotFoundError, index == -1);

            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(index);

            String valuesNotMatchError = "Actual value '" + actualValue +
                    "' did not match the expected value '" + expectedValue + "'. " + error;
            assertEquals(valuesNotMatchError, expectedValue, actualValue);

        }
    }

}
