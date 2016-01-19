package com.cundong.memory.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 一个空Service，仅仅用于测试一个空进程在dalvik、art上占用多少Total Pss
 */
public class EmptyService extends Service {

    public EmptyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}