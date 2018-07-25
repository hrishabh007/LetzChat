package com.app.letzchat.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;


import com.app.letzchat.xmpp.SmackHelper;


import java.io.File;
import java.util.List;

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onCreate() {
        super.onCreate();


        registerActivityLifecycleCallbacks(this);

        //   saveLog();
    /*    if (!isAppOnForeground(this)){
            SmackHelper smackHelper = SmackHelper.getInstance(this);
            smackHelper.unavailable();
        }*/
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
       // MultiDex.install(this);
    }



    private void saveLog() {
        try {
            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/PMDM");
            File logDirectory = new File(appDirectory + "/log");
            File logFile = new File(logDirectory, "logcat_" + System.currentTimeMillis() + ".txt");

            // create app folder
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
            }

            // create log folder
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }


            // clear the previous logcat and then write the new one to the file
            try {
//                Process process = Runtime.getRuntime().exec("logcat -c");
//                process = Runtime.getRuntime().exec("logcat -f " + logFile + " *  ReminderService:D"); // For Filtered Log
                Process process = Runtime.getRuntime().exec("logcat -f " + logFile); // For All Log
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isAppOnForeground(Context context) {
        boolean isForeGround = false;
        boolean isNotSleep = false;
        boolean isFound = false;
        try {
            try {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                    if (pm.isInteractive()) {
                        isNotSleep = true;
                    } else {
                        isNotSleep = false;
                    }
                } else {
                    if (pm.isScreenOn()) {
                        isNotSleep = true;
                    } else {
                        isNotSleep = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                    List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
                    if (appProcesses == null) {
                        isFound = false;
                    } else {
                        final String packageName = context.getPackageName();
                        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                                isFound = true;
                            }
                        }
                    }
                } else {
                    List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);
                    ComponentName componentInfo = taskInfo.get(0).topActivity;
                    if (componentInfo.getPackageName().equals(context.getPackageName())) {
                        isFound = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e("APP_STATUS", "isNotSleep > " + isNotSleep);
            Log.e("APP_STATUS", "isFound > " + isFound);

            if (isNotSleep && isFound) {
                isForeGround = true;
            } else {
                isForeGround = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("APP_STATUS", "isForeGround > " + isForeGround);

        return isForeGround;
    }



    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (isAppOnForeground(this)) {
            SmackHelper smackHelper = SmackHelper.getInstance(this);
            smackHelper.available();
        } else {
            SmackHelper smackHelper = SmackHelper.getInstance(this);
            smackHelper.unavailable();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {
 /*       if (isAppOnForeground(this)){
            SmackHelper smackHelper = SmackHelper.getInstance(this);
            smackHelper.unavailable();
        }else {
            SmackHelper smackHelper = SmackHelper.getInstance(this);
            smackHelper.available();
        }*/
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (isAppOnForeground(this)) {
            SmackHelper smackHelper = SmackHelper.getInstance(this);
            smackHelper.available();
        } else {
            SmackHelper smackHelper = SmackHelper.getInstance(this);
            smackHelper.unavailable();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
   /*     if (isAppOnForeground(this)){
            SmackHelper smackHelper = SmackHelper.getInstance(this);
            smackHelper.unavailable();
        }else {
            SmackHelper smackHelper = SmackHelper.getInstance(this);
            smackHelper.available();
        }*/
    }
}