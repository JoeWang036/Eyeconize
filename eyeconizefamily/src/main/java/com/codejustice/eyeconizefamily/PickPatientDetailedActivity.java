package com.codejustice.eyeconizefamily;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.codejustice.dialogs.AskAvailableDialog;
import com.codejustice.enums.MessageTypes;
import com.codejustice.global.Global;
import com.codejustice.utils.db.MessagesDBHelper;

import NetService.ConnectionUtils.ChatMessage;
import NetService.ConnectionUtils.ConnectionManager;
import NetService.ConnectionUtils.PageObserver;
import NetService.MessageProtocol.TextMessage;

public class PickPatientDetailedActivity extends AppCompatActivity implements PageObserver, ReplierActivity {
    private PickPatientsFragment pickPatientsFragment;
    private Handler handler;
    private ConnectionManager connectionManager;
    private MessagesDBHelper messagesDBHelper;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("received..");

            if (intent.getAction().equals(MessageTypes.ACTION_ALTER_CHAT_CONTENTS)) {
                System.out.println("received broadcast: alter chat content. former id:"+ Global.receiverID);
                long newID = intent.getLongExtra(MessageTypes.INTENT_EXTRA_NEW_USER_ID, Global.receiverID);
                System.out.println("new ID: "+newID);
                Global.receiverID = newID;
                onBackPressed();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_patient);
        connectionManager = ConnectionManager.getInstance();
        pickPatientsFragment = new PickPatientsFragment(PickPatientsFragment.DETAILED_MODE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.pick_family_frame_layout, pickPatientsFragment)
                .commit();
        Toolbar toolbar = findViewById(R.id.pickDetailToolBar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("选择患者");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message message){
                switch (message.what) {
                    case MessageTypes.GO_TO_SEND_MESSAGES:
                        break;
                    case MessageTypes.HANDLER_NEW_MESSAGE:
                        Global.receiverID = ((ChatMessage) message.obj).senderID;
                        messagesDBHelper.switchTable(Global.selfID, Global.receiverID);
//                        messagesDBHelper.insertData((ChatMessage) message.obj);
                        pickPatientsFragment.refreshContent(Global.receiverID);
                        if (((ChatMessage) message.obj).needToReply) {
                            AskAvailableDialog askAvailableDialog =
                                    new AskAvailableDialog(PickPatientDetailedActivity.this,
                                            R.style.AskAvailableStyle, (ChatMessage) message.obj,
                                            connectionManager, PickPatientDetailedActivity.this);
                            askAvailableDialog.show();
                        }
                        break;
                    case MessageTypes.HANDLER_PICK_PATIENTS_RETURN_AND_SEND:
                        MessageToSend messageToSend = (MessageToSend) message.obj;
                        Global.SEND_NEW_MESSAGE = true;
                        Global.receiverID = messageToSend.receiverID;
                        Global.messageSerial++;
                        Global.messageSerial = (short)(Global.messageSerial%10000);
                        TextMessage textMessage = new TextMessage(messageToSend.content, Global.messageSerial, Global.receiverID, System.currentTimeMillis());
                        Global.messagesToSend.add(textMessage);
                        onBackPressed();
                    default:
                        break;
                }
            }
        };
    }




    @Override
    protected void onStart(){
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(MessageTypes.ACTION_ALTER_CHAT_CONTENTS);
        registerReceiver(broadcastReceiver, intentFilter);
        connectionManager.registerPageObserver(this);
    }

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
        messagesDBHelper = MessagesDBHelper.getInstance(this);
        ((EyeconizeFamilyApplication)getApplication()).setInForeground(true);


    }

    @Override
    protected void onPause() {
        super.onPause();
        // 取消注册广播接收器
        unregisterReceiver(broadcastReceiver);
        connectionManager.unregisterPageObserver(this);
        System.out.println("pausing...");
        ((EyeconizeFamilyApplication)getApplication()).setInForeground(false);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // 在这里处理返回按钮的点击事件
            // 例如，返回上一个界面或执行其他操作
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public void sendMessage(String content, long receiverID) {
        if (content==null || content.trim().equals("")) {
            return;
        }
        MessageToSend messageToSend = new MessageToSend(content, receiverID);
        Message message = handler.obtainMessage(MessageTypes.HANDLER_PICK_PATIENTS_RETURN_AND_SEND, messageToSend);
        handler.sendMessage(message);

    }

    @Override
    public void newMessageAlert(ChatMessage chatMessage) {
        Message message = handler.obtainMessage(MessageTypes.HANDLER_NEW_MESSAGE, chatMessage);
        handler.sendMessage(message);
    }

    class MessageToSend{
        String content;
        long receiverID;

        public MessageToSend(String content, long receiverID) {
            this.content = content;
            this.receiverID = receiverID;
        }
    }
}
