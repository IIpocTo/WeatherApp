package com.example.weatherapp.weatherapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.example.weatherapp.weatherapp.data.WeatherContract.WeatherEntry;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;

@RunWith(AndroidJUnit4.class)
public class TestDatabase {

    private final Context context = InstrumentationRegistry.getTargetContext();

    private SQLiteDatabase database;

    @Before
    public void setUp() throws Exception {
        WeatherDbHelper dbHelper = new WeatherDbHelper(context);
        context.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        database = dbHelper.getWritableDatabase();
    }

    @Test
    public void testCreateDb() {

        final Set<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(WeatherEntry.TABLE_NAME);

        String databaseIsNotOpen = "The database should be open but it isn't";
        assertTrue(databaseIsNotOpen, database.isOpen());

        Cursor tableNameCursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'", null);

        String errorInCreatingDatabase = "Error: This means that the database has not been created correctly";
        assertTrue(errorInCreatingDatabase, tableNameCursor.moveToFirst());

        do {
            tableNameHashSet.remove(tableNameCursor.getString(0));
        } while (tableNameCursor.moveToNext());

        String databaseIsNotEmpty = "Error: Your database was created without the expected tables.";
        assertTrue(databaseIsNotEmpty, tableNameHashSet.isEmpty());

        tableNameCursor.close();

    }

    @Test
    public void testInsertSingleRecordIntoWeatherTable() {

        ContentValues testWeatherValues = TestUtilities.createTestWeatherContentValues();
        long weatherRowId = database.insert(WeatherEntry.TABLE_NAME, null, testWeatherValues);

        final String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, weatherRowId != -1);

        Cursor weatherCursor = database.query(WeatherEntry.TABLE_NAME, null, null, null, null, null, null);

        final String emptyQueryError = "Error: No Records returned from weather query";
        assertTrue(emptyQueryError, weatherCursor.moveToFirst());

        final String expectedWeatherNotMatchActual = "Expected weather values didn't match actual values.";
        TestUtilities.validateCurrentRecord(expectedWeatherNotMatchActual, weatherCursor, testWeatherValues);

        final String moreThenOneRecord = "Error: More than one record returned from weather query";
        assertFalse(moreThenOneRecord, weatherCursor.moveToNext());

        weatherCursor.close();

    }

    @Test
    public void testIdAutoincrement() {

        ContentValues testWeatherValues = TestUtilities.createTestWeatherContentValues();
        long originalDate = testWeatherValues.getAsLong(WeatherEntry.COLUMN_DATE);
        long firstRowId = database.insert(WeatherEntry.TABLE_NAME, null, testWeatherValues);
        database.delete(WeatherEntry.TABLE_NAME, "_ID == " + firstRowId, null);
        long dayAfterOriginalDate = originalDate + TimeUnit.DAYS.toMillis(1);
        testWeatherValues.put(WeatherEntry.COLUMN_DATE, dayAfterOriginalDate);
        long secondRowId = database.insert(WeatherEntry.TABLE_NAME, null, testWeatherValues);

        String sequentialInsertsDoNotAutoIncrementId =
                "IDs were reused and shouldn't be if autoincrement is setup properly.";
        assertNotSame(sequentialInsertsDoNotAutoIncrementId, firstRowId, secondRowId);

    }

}
