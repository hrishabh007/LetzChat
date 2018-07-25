package com.app.letzchat.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Ahmed AbuQamar on 12/17/17.
 *
 *
 * Add next line Application class in your App
 * registerActivityLifecycleCallbacks(new AppLifecycleTracker());
 *
 * As next:
 *
 * @Override
 * public void onCreate() {
 * super.onCreate();
 * registerActivityLifecycleCallbacks(new AppLifecycleTracker());
 * }
 *
 */

public class AppLifecycleTracker implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "AppLifecycleTracker";
    private int numberActivitiesStart = 0;


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {


        if (numberActivitiesStart == 0) {
            // The application come from background to foreground
            Log.d(TAG, "AppController status > onActivityStarted:  app went to foreground");

        }
        numberActivitiesStart++;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        numberActivitiesStart--;
        if (numberActivitiesStart == 0) {
            // The application go from foreground to background
            Log.d(TAG, "AppController status > onActivityStopped: app went to background");
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}