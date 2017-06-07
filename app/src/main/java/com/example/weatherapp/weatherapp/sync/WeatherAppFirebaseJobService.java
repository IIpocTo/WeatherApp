package com.example.weatherapp.weatherapp.sync;

import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class WeatherAppFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchWeatherTask;

    @Override
    public boolean onStartJob(JobParameters job) {
        mFetchWeatherTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                WeatherAppSyncUtils.startImmediateSync(WeatherAppFirebaseJobService.this);
                return null;
            }

        };
        mFetchWeatherTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if (mFetchWeatherTask != null) {
            mFetchWeatherTask.cancel(true);
        }
        return true;
    }

}
