package com.codejustice.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.codejustice.enums.MessageTypes;
import com.codejustice.eyeconizefamily.SendMessageActivity;
import com.codejustice.eyeconizefamily.TransparentActivity;
import com.codejustice.global.Global;
import com.codejustice.utils.db.MessagesDBHelper;

import NetService.ConnectionUtils.ChatMessage;
import NetService.ConnectionUtils.ConnectionManager;

public class NotificationService extends Service {

    public static final String ACTION_AVAILABLE = "action_available";
    public static final String ACTION_UNAVAILABLE = "action_unavailable";
    public static final String ACTION_CLICK_MESSAGE = "action_click_message";



    private Context context;
    private NotificationManager notificationManager;
    private ConnectionManager connectionManager;
    private MessagesDBHelper  messagesDBHelper;

    @Override
    public void onCreate(){
        super.onCreate();
        context = this;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        connectionManager = ConnectionManager.getInstance();
        messagesDBHelper = MessagesDBHelper.getInstance(this);
        System.out.println("notification service: started.");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        if(intent == null || intent.getAction() == null){
            return START_NOT_STICKY;
        }
        switch (intent.getAction()) {
            case ACTION_CLICK_MESSAGE:
                Intent transparentIntent = new Intent(context, SendMessageActivity.class);
                transparentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(transparentIntent);
                break;
            case ACTION_AVAILABLE:
                long receiverID = intent.getLongExtra(MessageTypes.INTENT_EXTRA_SENDER_ID, -1);
                sendMessage("这就来！", receiverID);
                break;
            case ACTION_UNAVAILABLE:
                long unavailable_id = intent.getLongExtra(MessageTypes.INTENT_EXTRA_SENDER_ID, -1);
                sendMessage("抱歉，我现在没空", unavailable_id);
                break;
        }
        int id = intent.getIntExtra(MessageTypes.INTENT_EXTRA_notification_ID, -1);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.cancelAll();


        return START_STICKY;
    }


    public void sendMessage(String content, long receiverID) {

        Global.receiverID = receiverID;
        Global.messageSerial++;
        Global.messageSerial = (short)(Global.messageSerial%10000);
        long sendTime = System.currentTimeMillis();

        messagesDBHelper.insertData(new ChatMessage(content, Global.selfID, sendTime, Global.messageSerial, ChatMessage.SENDING));
        new Thread(()->{
            connectionManager.sendTextMessage(content, Global.receiverID, Global.messageSerial, sendTime);
        }).start();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
