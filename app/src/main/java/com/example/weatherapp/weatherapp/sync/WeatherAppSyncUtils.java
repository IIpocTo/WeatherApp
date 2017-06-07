package com.example.weatherapp.weatherapp.sync;

import android.content.Context;
import android.content.Intent;

public class WeatherAppSyncUtils {

    public static void startImmediateSync(Context context) {
        Intent intentToSyncImmediately = new Intent(context, WeatherAppSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }

}
