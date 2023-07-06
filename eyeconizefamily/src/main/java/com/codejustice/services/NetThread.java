package com.codejustice.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.codejustice.enums.MessageTypes;
import com.codejustice.eyeconizefamily.EyeconizeFamilyApplication;
import com.codejustice.eyeconizefamily.R;
import com.codejustice.eyeconizefamily.SendMessageActivity;
import com.codejustice.global.Global;
import com.codejustice.global.tempProfile;
import com.codejustice.utils.db.FriendsDBHelper;
import com.codejustice.utils.db.MessagesDBHelper;

import NetService.ConnectionUtils.ChatMessage;
import NetService.ConnectionUtils.ConnectionManager;
import NetService.ConnectionUtils.ConnectionObserver;
import NetService.MessageProtocol.CommunicationMessage;
import NetService.MessageProtocol.ConfirmMessage;
import NetService.MessageProtocol.TextMessage;

public class NetThread extends HandlerThread implements ConnectionObserver {
    private Handler handler;
    private MessagesDBHelper messagesDBHelper;
    private FriendsDBHelper friendsDBHelper;
    private static NetThread instance;
    private ConnectionManager connectionManager;
    private Context context;
    private int notificationID = 0;

    public void setMessagesDBHelper(MessagesDBHelper messagesDBHelper) {
        this.messagesDBHelper = messagesDBHelper;
    }

    public void setFriendsDBHelper(FriendsDBHelper friendsDBHelper) {
        this.friendsDBHelper = friendsDBHelper;
    }

    private NetThread(String name, Context context) {
        super(name);
        this.context = context;
    }

    public static NetThread getInstance(Context context){
        synchronized (NetThread.class) {
            if (instance == null) {
                instance = new NetThread("BackgroundThread: Net", context);
            }
        }
        return instance;
    }
    @Override
    protected void onLooperPrepared(){
        ConnectionManager.instantiate(Global.selfID, Global.password);
        connectionManager = ConnectionManager.getInstance();
        System.out.println("connection manager id:");
        System.out.println(connectionManager.getSelfID());
        while (true) {

            synchronized (connectionManager) {
                while (!connectionManager.isConnected()) {
                    try {
                        System.out.println("isn't connected.");
                        connectionManager.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("NetThread: Connected.");
            CommunicationMessage message;
            message = connectionManager.readMessage();
            if (message == null) {
                connectionManager.setConnected(false);
            } else if (message instanceof TextMessage) {
                Message msg = handler.obtainMessage(MessageTypes.HANDLER_NEW_MESSAGE);
                ChatMessage chatMessage = new ChatMessage((TextMessage) message);
                messagesDBHelper.insertData(chatMessage);
                System.out.println("checking in foreground:" + ((EyeconizeFamilyApplication) context).isInForeground());
                if (!((EyeconizeFamilyApplication) context).isInForeground()) {
                    Global.receiverID = ((TextMessage) message).getSenderID();
                    Intent availableIntent = new Intent(context, NotificationService.class);
                    availableIntent.putExtra(MessageTypes.INTENT_EXTRA_SENDER_ID, ((TextMessage) message).getSenderID());
                    availableIntent.putExtra(MessageTypes.INTENT_EXTRA_notification_ID, notificationID+1);
                    availableIntent.setAction(NotificationService.ACTION_AVAILABLE);
                    PendingIntent availablePendingIntent = PendingIntent.getService(context, 0, availableIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification.Action yesAction = new Notification.Action.Builder(
                            Icon.createWithResource("", R.mipmap.confirm_available_icon),
                            "有空",
                            availablePendingIntent
                    ).build();
                    Intent unavailableIntent = new Intent(context, NotificationService.class);
                    unavailableIntent.setAction(NotificationService.ACTION_UNAVAILABLE);
                    unavailableIntent.putExtra(MessageTypes.INTENT_EXTRA_SENDER_ID, ((TextMessage) message).getSenderID());
                    unavailableIntent.putExtra(MessageTypes.INTENT_EXTRA_notification_ID, notificationID+1);
                    PendingIntent unavailablePendingIntent = PendingIntent.getService(context, 0, unavailableIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification.Action noAction = new Notification.Action.Builder(
                            Icon.createWithResource("", R.mipmap.confirm_unavailable_icon),
                            "没空",
                            unavailablePendingIntent
                    ).build();

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        System.out.println("checking version: old enough.");

                        Intent clickMessageIntent = new Intent(context, NotificationService.class);
                        clickMessageIntent.setAction(NotificationService.ACTION_CLICK_MESSAGE);
                        clickMessageIntent.putExtra(MessageTypes.INTENT_EXTRA_notification_ID, notificationID+1);
                        PendingIntent pendingIntent = PendingIntent.getService(context, 0, clickMessageIntent, PendingIntent.FLAG_IMMUTABLE);

                        Notification.Builder notificationBuilder;

                        notificationBuilder = new Notification.Builder(context, Global.userMessageChannelID)
                                .setContentTitle(friendsDBHelper.getFriendNicknameByID(((TextMessage) message).getSenderID(), Global.selfID))
                                .setContentText(((TextMessage) message).getMessage())
                                .setWhen(((TextMessage) message).getSendTime())
                                .setContentIntent(pendingIntent)
                                .setSmallIcon(R.mipmap.app_temp_icon);
                        if (tempProfile.profile.containsKey(((TextMessage) message).getSenderID())) {
                            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), tempProfile.profile.get(((TextMessage) message).getSenderID()));
                            notificationBuilder.setLargeIcon(bitmap);
                        }
                        if (((TextMessage) message).needToReply) {
                            notificationBuilder.setActions(yesAction, noAction);
                        }
                        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
                        nm.notify(notificationID++, notificationBuilder.build());

                    }
                    else{
                        System.out.println("checking version: not old enough.");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Global.userMessageChannelID)
                                .setContentTitle(friendsDBHelper.getFriendNicknameByID(((TextMessage) message).getSenderID(), Global.selfID))
                                .setContentText(((TextMessage) message).getMessage())
                                .setWhen(((TextMessage) message).getSendTime())
                                .setSmallIcon(R.mipmap.app_temp_icon);
                        if (((TextMessage) message).needToReply) {
                            builder.addAction(R.mipmap.confirm_available_icon, "有空", availablePendingIntent)
                                    .addAction(R.mipmap.confirm_unavailable_icon, "没空", unavailablePendingIntent);
                        }
                        if (tempProfile.profile.containsKey(((TextMessage) message).getSenderID())) {
                            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), tempProfile.profile.get(((TextMessage) message).getSenderID()));
                            builder.setLargeIcon(bitmap);
                        }

                        Notification notification = builder.build();
                        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
                        nm.notify(notificationID++, notification);
                    }
                    Vibrator vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                    long[] vibrationPattern = new long[]{0, 200, 100,200, 100, 500, 100};
                    // 第一个参数为开关开关的时间，第二个参数是重复次数，振动需要添加权限
                    vibrator.vibrate(vibrationPattern, -1);
                }

                connectionManager.notifyPageObservers(chatMessage);
                msg.obj = message;
                handler.sendMessage(msg);
            } else if (message instanceof ConfirmMessage) {
                messagesDBHelper.updateSentStatus((ConfirmMessage) message);
                connectionManager.notifyMessageObserverRefresh((ConfirmMessage) message);

            }
        }
    }
    public void startThread(){
        start();
        System.out.println(getLooper());
        handler = new Handler(getLooper()){
            @Override
            public void handleMessage(Message msg) {
                System.out.println("got message.");
                switch (msg.what) {
                    case MessageTypes.HANDLER_REQUEST_SEND_MESSAGE:
//                        System.out.println("handling...");
//                        String content = (String) msg.obj;
//                        connectionManager.sendTextMessage(content, Global.receiverID, (short) 1);
                        break;

                    case MessageTypes.HANDLER_NEW_MESSAGE:
                        break;
                    case MessageTypes.HANDLER_CHANGE_CONNECTION_STATUS:
                        NetThread.this.notify();
                        break;


                }
            }
        };

    }
    public Handler getHandler(){

        return handler;
    }


    @Override
    public void onValueChange(boolean newValue) {
        Message msg = handler.obtainMessage(MessageTypes.HANDLER_CHANGE_CONNECTION_STATUS, newValue);
        handler.sendMessage(msg);
    }
}
