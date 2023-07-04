package com.codejustice.eyeconizefamily;

import android.app.Application;
import android.os.Handler;

import com.codejustice.netservice.NetThread;
import com.codejustice.utils.db.FriendsDBHelper;
import com.codejustice.utils.db.MessagesDBHelper;

public class EyeconizeFamilyApplication extends Application {

    private NetThread netThread;

    @Override
    public void onCreate() {
        super.onCreate();
        startNetThread();
        FriendsDBHelper.instantiate(this);
        MessagesDBHelper.instantiate(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        terminateBackgroundThread();
    }

    private void startNetThread() {

        FriendsDBHelper.instantiate(this);
        MessagesDBHelper.instantiate(this);
        netThread = NetThread.getInstance();
        netThread.setMessagesDBHelper(MessagesDBHelper.getInstance(this));
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
