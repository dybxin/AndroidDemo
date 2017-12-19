package com.example.mydemo;

import android.app.Application;
import android.content.Context;

import com.zchu.log.LogLevel;
import com.zchu.log.Logger;
import com.zchu.log.Settings;

public class AppApplication extends Application {

    private static AppApplication appApplication = null;
    private ActivityLifecycleCallbacks activityManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        appApplication = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        activityManager = new ActivityManager();
        registerActivityLifecycleCallbacks(activityManager);
        //init Log
        Settings settings= Logger.init();
        if(!BuildConfig.DEBUG){
            settings.setLogLevel(LogLevel.NONE);
        }
    }

    public static AppApplication context() {
        return appApplication;
    }

    @Override
    public void onTerminate() {
        unregisterActivityLifecycleCallbacks(activityManager);
        activityManager = null;
        super.onTerminate();
    }

}
