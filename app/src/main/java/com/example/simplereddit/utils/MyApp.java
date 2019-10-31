package com.example.simplereddit.utils;

import android.app.Application;

import com.example.simplereddit.BuildConfig;

import timber.log.Timber;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // only plant the logging for debug version
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
