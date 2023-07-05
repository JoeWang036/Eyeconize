package com.whu.eyerecongize.connect;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;


import NetService.ConnectionUtils.ChatMessage;
import NetService.ConnectionUtils.ConnectionManager;
import NetService.ConnectionUtils.ConnectionObserver;
import NetService.MessageProtocol.CommunicationMessage;
import NetService.MessageProtocol.ConfirmMessage;
import NetService.MessageProtocol.TextMessage;

public class NetThread extends HandlerThread implements ConnectionObserver {
    private Handler handler;
    private static NetThread instance;
    private ConnectionManager connectionManager;

    private NetThread(String name) {
        super(name);
    }

    public static NetThread getInstance(){
        synchronized (NetThread.class) {
            if (instance == null) {
                instance = new NetThread("BackgroundThread: Net");
            }
        }
        return instance;
    }
    @Override
    protected void onLooperPrepared(){
        ConnectionManager.instantiate(Setting.selfID, Setting.password);
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
                NetService.ConnectionUtils.ChatMessage chatMessage = new ChatMessage((TextMessage) message);
                connectionManager.notifyPageObservers(chatMessage);
            } else if (message instanceof ConfirmMessage) {
                connectionManager.notifyMessageObserverRefresh((ConfirmMessage) message);

            }
        }
    }
    public void startThread(){
        start();
        System.out.println(getLooper());


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
