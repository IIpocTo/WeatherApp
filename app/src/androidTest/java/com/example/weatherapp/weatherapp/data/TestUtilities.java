package com.example.weatherapp.weatherapp.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import com.example.weatherapp.weatherapp.utilities.DateTimeUtils;
import com.example.weatherapp.weatherapp.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

import static com.example.weatherapp.weatherapp.data.WeatherContract.WeatherEntry;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

class TestUtilities {

    static final long TEST_DATE = 1475280000000L;
    static final int BULK_RECORDS_TO_INSERT = 15;

    static ContentValues createTestWeatherContentValues() {
        ContentValues testContentValues = new ContentValues();
        testContentValues.put(WeatherEntry.COLUMN_WEATHER_ID, 3212);
        testContentValues.put(WeatherEntry.COLUMN_DATE, TEST_DATE);
        testContentValues.put(WeatherEntry.COLUMN_MIN_TEMP, 30);
        testContentValues.put(WeatherEntry.COLUMN_MAX_TEMP, 50);
        testContentValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        testContentValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        testContentValues.put(WeatherEntry.COLUMN_WIND_DIR, 1.1);
        testContentValues.put(WeatherEntry.COLUMN_WIND_SPEED, 1.5);
        return testContentValues;
    }

    static ContentValues[] createBulkInsertTestWeatherValues() {

        ContentValues[] bulkTestContentValues = new ContentValues[BULK_RECORDS_TO_INSERT];
        long testDate = TEST_DATE;

        for (int i = 0; i < BULK_RECORDS_TO_INSERT; i++) {
            testDate += DateTimeUtils.DAY_IN_MILLIS;
            ContentValues contentValues = new ContentValues();
            contentValues.put(WeatherEntry.COLUMN_DATE, testDate);
            contentValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);
            contentValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65 - i);
            contentValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75 + i);
            contentValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
            contentValues.put(WeatherEntry.COLUMN_PRESSURE, 1.9);
            contentValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
            contentValues.put(WeatherEntry.COLUMN_WIND_DIR, 1.5);
            bulkTestContentValues[i] = contentValues;
        }

        return bulkTestContentValues;

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

    static void validateThenCloseCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        String cursorIsNull = "This cursor is null.";
        assertNotNull(cursorIsNull, valueCursor);
        String emptyCursorError = "Empty cursor returned. " + error;
        assertTrue(emptyCursorError, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    static class TestContentObserver extends ContentObserver {

        final HandlerThread handlerThread;
        boolean mContentChanged;

        public TestContentObserver(HandlerThread handlerThread) {
            super(new Handler(handlerThread.getLooper()));
            this.handlerThread = handlerThread;
        }

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        void waitForNotificationOrFail() {
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            handlerThread.quit();
        }

    }

}
