package com.codejustice.eyeconizefamily;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.FrameLayout;

import com.codejustice.dialogs.AskAvailableDialog;
import com.codejustice.enums.MessageTypes;
import com.codejustice.global.Global;
import com.codejustice.utils.db.MessagesDBHelper;

import NetService.ConnectionUtils.ChatMessage;
import NetService.ConnectionUtils.ConnectionManager;
import NetService.ConnectionUtils.PageObserver;
import NetService.MessageProtocol.TextMessage;

public class MainActivity extends AppCompatActivity implements PageObserver, ReplierActivity{

    private DualFragment dualFragment;
    Handler handler;
    ConnectionManager connectionManager;
    MessagesDBHelper messagesDBHelper;

    private MessagesFragment messagesFragment;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("received..");
            if (intent.getAction().equals(MessageTypes.ACTION_GO_TO_SEND_MESSAGES)) {
                // 处理接收到的广播消息
                Intent sendMessagesIntent = new Intent(MainActivity.this, SendMessageActivity.class);
                startActivity(sendMessagesIntent);
                finish();
            } else if (intent.getAction().equals(MessageTypes.ACTION_GO_TO_PICK_PATIENTS)) {
                System.out.println(intent.getAction());
                Intent sendMessagesIntent = new Intent(MainActivity.this, PickPatientDetailedActivity.class);
                startActivity(sendMessagesIntent);
            } else if (intent.getAction().equals(MessageTypes.ACTION_ALTER_CHAT_CONTENTS)) {
                System.out.println("received broadcast: alter chat content. former id:"+ Global.receiverID);
                long newID = intent.getLongExtra(MessageTypes.INTENT_EXTRA_NEW_USER_ID, Global.receiverID);
                System.out.println("new ID: "+newID);
                Global.receiverID = newID;
                messagesFragment.refreshDatabase(newID);

            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // 注册广播接收器
        IntentFilter intentFilter = new IntentFilter(MessageTypes.ACTION_GO_TO_SEND_MESSAGES);
        registerReceiver(broadcastReceiver, intentFilter);
        intentFilter = new IntentFilter(MessageTypes.ACTION_GO_TO_PICK_PATIENTS);
        registerReceiver(broadcastReceiver, intentFilter);
        intentFilter = new IntentFilter(MessageTypes.ACTION_ALTER_CHAT_CONTENTS);
        registerReceiver(broadcastReceiver, intentFilter);
        connectionManager.registerPageObserver(this);
        if (Global.SEND_NEW_MESSAGE) {
            while (!Global.messagesToSend.isEmpty()){
                TextMessage tm = Global.messagesToSend.get(0);
                sendMessage(tm.getMessage(), Global.receiverID);
                Global.messagesToSend.remove(0);
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        // 取消注册广播接收器
        unregisterReceiver(broadcastReceiver);
        connectionManager.unregisterPageObserver(this);

    }
    private void renewMessageSerial(){
        Global.messageSerial = messagesDBHelper.getLastSerial(Global.receiverID);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messagesDBHelper = MessagesDBHelper.getInstance(this);

        connectionManager = ConnectionManager.getInstance();
        connectionManager.registerPageObserver(this);
        // 创建 DualFragment 实例并添加到布局中
        dualFragment = new DualFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, dualFragment)
                .commit();
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message message) {
                System.out.println("Message received.");
                switch (message.what) {
                    case MessageTypes.GO_TO_SEND_MESSAGES:
                        Intent intent = new Intent(MainActivity.this, SendMessageActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case MessageTypes.HANDLER_NEW_MESSAGE:


                        messagesFragment.getMessage((ChatMessage) message.obj);
                        AskAvailableDialog askAvailableDialog = new AskAvailableDialog(MainActivity.this, R.style.AskAvailableStyle, (ChatMessage) message.obj, connectionManager, MainActivity.this);
                        askAvailableDialog.show();
                    default:
                        break;

                }
            }
        };


    }
    @Override
    protected void onStart(){
        super.onStart();        // 替换 FamilyPickerFragment
        PickPatientsFragment familyPickerFragment = new PickPatientsFragment(PickPatientsFragment.DUAL_MODE);
        dualFragment.replaceFamilyPickerFragment(familyPickerFragment);
        // 替换 ChatFragment

         messagesFragment = new MessagesFragment(MessagesFragment.DUAL_MODE);

        dualFragment.replaceChatFragment(messagesFragment);
        connectionManager.registerPageObserver(this);


    }

    @Override
    public void newMessageAlert(ChatMessage chatMessage) {
        Message message = handler.obtainMessage(MessageTypes.HANDLER_NEW_MESSAGE, chatMessage);
        handler.sendMessage(message);
    }

    @Override
    public void sendMessage(String content, long receiverID) {
        if (!content.trim().equals("")) {
            System.out.println("connection manager id:");
            System.out.println(connectionManager.getSelfID());
            renewMessageSerial();
            Global.messageSerial++;
            Global.messageSerial = (short)(Global.messageSerial%10000);
            long sendTime = System.currentTimeMillis();
            messagesFragment.addMessage(new ChatMessage(content, Global.selfID, sendTime, Global.messageSerial, ChatMessage.SENDING));

            new Thread(()->{
                connectionManager.sendTextMessage(content, Global.receiverID, Global.messageSerial, sendTime);
            }).start();
        }
    }
}
