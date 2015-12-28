package com.cundong.memory;

import android.app.Application;
import android.content.Context;

/**
 * Created by liucundong on 2015/12/25.
 */
public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getAppContext() {
        return mContext;
    }
}