package com.codejustice.eyeconizefamily;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
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
        MessagesDBHelper.instantiate(this);// 创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "eyeconize_notifications";
            String channelName = "识目";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("用于接收病人消息");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

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
