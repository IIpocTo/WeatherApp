package com.example.weatherapp.weatherapp.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

import static com.example.weatherapp.weatherapp.data.WeatherContract.WeatherEntry;

public class WeatherAppSyncUtils {

    private static boolean sInitialized;

    synchronized public static void initialize(final Context context) {
        if (!sInitialized) {
            sInitialized = true;
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    String[] projectionColumns = {WeatherEntry._ID};
                    Cursor cursor = context.getContentResolver().query(
                            WeatherEntry.CONTENT_URI,
                            projectionColumns,
                            WeatherEntry.COLUMN_DATE + " >= " + 0,
                            null,
                            null
                    );
                    if (cursor == null || cursor.getCount() == 0) {
                        startImmediateSync(context);
                    }
                    cursor.close();
                    return null;
                }

            }.execute();
        }
    }

    public static void startImmediateSync(Context context) {
        Intent intentToSyncImmediately = new Intent(context, WeatherAppSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }

}
