package com.codejustice.eyeconizefamily;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;

import com.codejustice.global.Global;
import com.codejustice.services.NetThread;
import com.codejustice.services.NotificationService;
import com.codejustice.utils.db.FriendsDBHelper;
import com.codejustice.utils.db.MessagesDBHelper;

public class EyeconizeFamilyApplication extends Application {

    private NetThread netThread;
    private boolean inForeground = true;

    @Override
    public void onCreate() {
        super.onCreate();
        startNetThread();
        FriendsDBHelper.instantiate(this);
        MessagesDBHelper.instantiate(this);// 创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Global.userMessageChannelID, Global.userMessageChannelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(Global.userMessageChannelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        terminateBackgroundThread();
    }

    public boolean isInForeground(){
        return inForeground;
    }

    public void setInForeground(boolean inForeground) {
        this.inForeground = inForeground;
    }
    private void startNetThread() {

        FriendsDBHelper.instantiate(this);
        MessagesDBHelper.instantiate(this);
        netThread = NetThread.getInstance(this);
        netThread.setMessagesDBHelper(MessagesDBHelper.getInstance(this));
        netThread.setFriendsDBHelper(FriendsDBHelper.getInstance(this));
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
