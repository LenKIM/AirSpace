package com.yyy.xxx.airspace;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by len on 2017. 4. 23..
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
