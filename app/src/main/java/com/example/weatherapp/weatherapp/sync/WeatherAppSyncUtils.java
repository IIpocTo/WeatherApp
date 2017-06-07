package com.example.weatherapp.weatherapp.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

import static com.example.weatherapp.weatherapp.data.WeatherContract.WeatherEntry;

public class WeatherAppSyncUtils {

    private static final int SYNC_PERIOD_IN_HOURS = 3;
    private static final int SYNC_PERIOD_IN_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_PERIOD_IN_HOURS);
    private static final int SYNC_FLEXTIME_PERIOD = SYNC_PERIOD_IN_SECONDS / 6;
    private static final String SYNC_WEATHER_TAG = "sync_weather_tag";

    private static boolean sInitialized;

    private static void scheduleFirebaseJobDispatcher(Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncWeatherJob = dispatcher.newJobBuilder()
                .setService(WeatherAppFirebaseJobService.class)
                .setTag(SYNC_WEATHER_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_PERIOD_IN_SECONDS,
                        SYNC_PERIOD_IN_SECONDS + SYNC_FLEXTIME_PERIOD))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncWeatherJob);
    }

    synchronized public static void initialize(final Context context) {
        if (!sInitialized) {
            sInitialized = true;
            scheduleFirebaseJobDispatcher(context);
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
