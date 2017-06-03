package com.example.weatherapp.weatherapp.data;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.example.weatherapp.weatherapp.data.WeatherContract.WeatherEntry;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class TestWeatherProvider {

    private final Context mContext = InstrumentationRegistry.getTargetContext();
    private final WeatherDbHelper helper = new WeatherDbHelper(mContext);

    @Before
    public void setUp() throws Exception {
        SQLiteDatabase database = helper.getWritableDatabase();
        database.delete(WeatherEntry.TABLE_NAME, null, null);
        database.close();
    }

    @Test
    public void testProviderRegistry() {

        String packageName = mContext.getPackageName();
        String weatherProviderClassName = WeatherProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, weatherProviderClassName);

        try {
            PackageManager packageManager = mContext.getPackageManager();
            ProviderInfo providerInfo = packageManager.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;
            String expectedAuthority = WeatherContract.AUTHORITY;
            String incorrectAuthority =
                    "Error: WeatherProvider registered with authority: " + actualAuthority +
                            " instead of expected authority: " + expectedAuthority;
            assertEquals(incorrectAuthority, actualAuthority, expectedAuthority);
        } catch (PackageManager.NameNotFoundException e) {
            String providerNotRegisteredAtAll = "Error: WeatherProvider not registered at " + mContext.getPackageName();
            fail(providerNotRegisteredAtAll);
        }

    }

    @Test
    public void testBasicWeatherQuery() {

        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues testContentValues = TestUtilities.createTestWeatherContentValues();

        long weatherRowId = database.insert(WeatherEntry.TABLE_NAME, null, testContentValues);
        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, weatherRowId != -1);

        database.close();

        Cursor weatherCursor = mContext.getContentResolver().query(WeatherEntry.CONTENT_URI, null, null, null, null);
        TestUtilities.validateThenCloseCursor("testBasicWeatherQuery", weatherCursor, testContentValues);

    }

    @Test
    public void testBulkInsert() {

        ContentValues[] bulkInsertTestWeatherValues = TestUtilities.createBulkInsertTestWeatherValues();
        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();
        ContentResolver contentResolver = mContext.getContentResolver();

        contentResolver.registerContentObserver(WeatherEntry.CONTENT_URI, true, observer);
        int insertCount = contentResolver.bulkInsert(WeatherEntry.CONTENT_URI, bulkInsertTestWeatherValues);
        observer.waitForNotificationOrFail();
        contentResolver.unregisterContentObserver(observer);

        String expectedAndActualInsertedRecordCountDoNotMatch =
                "Number of expected records inserted does not match actual inserted record count";
        assertEquals(expectedAndActualInsertedRecordCountDoNotMatch,
                insertCount, TestUtilities.BULK_RECORDS_TO_INSERT);

        Cursor cursor = mContext.getContentResolver().query(WeatherEntry.CONTENT_URI, null,
                null, null, WeatherEntry.COLUMN_DATE + " ASC");
        assertEquals(expectedAndActualInsertedRecordCountDoNotMatch,
                cursor.getCount(), TestUtilities.BULK_RECORDS_TO_INSERT);

        cursor.moveToFirst();
        for (int i = 0; i < TestUtilities.BULK_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            String bulkValidationError = "testBulkInsert. Error validating WeatherEntry " + i;
            TestUtilities.validateCurrentRecord(bulkValidationError, cursor, bulkInsertTestWeatherValues[i]);
        }

        cursor.close();

    }

    @Test
    public void testDeleteAllRecordsFromProvider() {

        testBulkInsert();

        ContentResolver contentResolver = mContext.getContentResolver();
        TestUtilities.TestContentObserver observer = TestUtilities.getTestContentObserver();

        contentResolver.registerContentObserver(WeatherEntry.CONTENT_URI, true, observer);
        contentResolver.delete(WeatherEntry.CONTENT_URI, null, null);
        Cursor shouldBeEmptyCursor = contentResolver.query(WeatherEntry.CONTENT_URI, null, null, null, null);
        observer.waitForNotificationOrFail();
        contentResolver.unregisterContentObserver(observer);

        String cursorWasNull = "Cursor was null.";
        assertNotNull(cursorWasNull, shouldBeEmptyCursor);

        String allRecordsWereNotDeleted = "Error: All records were not deleted from weather table during delete";
        assertEquals(allRecordsWereNotDeleted, 0, shouldBeEmptyCursor.getCount());

        shouldBeEmptyCursor.close();

    }

}
