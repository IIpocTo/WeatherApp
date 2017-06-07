package com.example.weatherapp.weatherapp.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

public class WeatherAppSyncIntentService extends IntentService {

    public WeatherAppSyncIntentService() {
        super("SunshineSyncIntentService");
    }

    public WeatherAppSyncIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        WeatherAppSyncTask.syncWeather(this);
    }

}
