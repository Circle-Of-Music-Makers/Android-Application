package com.sidzi.circleofmusic;

import android.app.Application;

import com.android.volley.VolleyLog;

public class CircleOfMusic extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG)
            VolleyLog.DEBUG = true;
    }
}
