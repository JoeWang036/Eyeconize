package com.codejustice.eyeconizefamily;

import android.app.Application;
import android.os.Handler;

import com.codejustice.netservice.NetThread;

public class EyeconizeFamilyApplication extends Application {

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
