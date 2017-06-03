package com.example.weatherapp.weatherapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.weatherapp.weatherapp.data.WeatherContract.WeatherEntry;

public class WeatherProvider extends ContentProvider {

    public static final int CODE_WEATHER = 100;
    public static final int CODE_WEATHER_WITH_DATE = 101;

    private static UriMatcher uriMatcher = buildUriMatcher();
    private WeatherDbHelper mDbHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(WeatherContract.AUTHORITY, WeatherContract.PATH, CODE_WEATHER);
        uriMatcher.addURI(WeatherContract.AUTHORITY, WeatherContract.PATH + "/#", CODE_WEATHER_WITH_DATE);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Cursor cursor;
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case CODE_WEATHER:
                cursor = database.query(WeatherEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_WEATHER_WITH_DATE:
                String date = uri.getPathSegments().get(1);
                String mSelection = "date = ?";
                String[] mSelectionArgs = {date};
                cursor = database.query(WeatherEntry.TABLE_NAME,
                        projection, mSelection, mSelectionArgs, null, null, WeatherEntry.COLUMN_DATE);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CODE_WEATHER:

                database.beginTransaction();
                int rowsInserted = 0;

                try {
                    for (ContentValues value : values) {
                        long insertedId = database.insert(WeatherEntry.TABLE_NAME, null, value);
                        if (insertedId != -1) {
                            rowsInserted++;
                        }
                    }
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int numRowsDeleted;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        switch (uriMatcher.match(uri)) {
            case CODE_WEATHER:
                numRowsDeleted = database.delete(WeatherEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

}

