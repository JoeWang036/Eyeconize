package com.codejustice.netservice;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.codejustice.enums.MessageTypes;
import com.codejustice.global.Global;

import java.util.Spliterator;

import NetService.ConnectionUtils.ConnectionManager;
import NetService.ConnectionUtils.ConnectionObserver;
import NetService.MessageProtocol.CommunicationMessage;
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
                connectionManager.notifyMessageObserversGet((TextMessage) message);
                Message msg = handler.obtainMessage(MessageTypes.HANDLER_UPDATE_MESSAGE);
                msg.obj = message;
                handler.sendMessage(msg);
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
                        System.out.println("handling...");
                        String content = (String) msg.obj;
                        connectionManager.sendTextMessage(content, 5);
                        break;

                    case MessageTypes.HANDLER_UPDATE_MESSAGE:
                        break;
                    case MessageTypes.HANDLER_CHANGE_CONNECTION_STATUS:
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
