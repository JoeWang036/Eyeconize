package com.whu.eyerecongize;

import android.app.Application;

import com.whu.eyerecongize.connect.NetThread;

public class EyeconizeApplication extends Application {
    private NetThread netThread;

    @Override
    public void onCreate() {
        super.onCreate();
        startNetThread();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        terminateBackgroundThread();
    }

    private void startNetThread() {
        netThread = NetThread.getInstance();
        netThread.startThread();

    }
    private void terminateBackgroundThread(){
        if (netThread != null) {
            netThread.quit();
            netThread = null;
        }
    }
    public NetThread getNetThread(){
        return netThread;
    }
}
