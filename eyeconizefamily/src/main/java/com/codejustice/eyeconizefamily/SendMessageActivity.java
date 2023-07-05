package com.codejustice.eyeconizefamily;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import NetService.ConnectionUtils.ChatMessage;

import com.codejustice.dialogs.AskAvailableDialog;
import com.codejustice.enums.MessageTypes;
import com.codejustice.global.Global;
import com.codejustice.netservice.NetThread;
import com.codejustice.utils.db.MessagesDBHelper;

import NetService.ConnectionUtils.ConnectionManager;
import NetService.ConnectionUtils.PageObserver;

public class SendMessageActivity extends AppCompatActivity implements ReplierActivity, PageObserver {


    private final long MAX_CLICK_DURATION = 1000;
    private final float MAX_CLICK_DISTANCE = 40;
    private final float MIN_SWIPE_DISTANCE = 50;
    private final float HORIZONTAL_SWIPE_THRESH = 50;

    private NetThread netThread;

    private MessagesFragment messagesFragment;
    private ConnectionManager connectionManager;
    private ImageButton sendButton;
    private EditText sendContent;
    public FrameLayout FamilyStatusHead;
    private ImageButton goToMainButton;
    private TextView batteryView;

    private MessagesDBHelper  messagesDBHelper;
    private TextView deviceStatusView;
    private TextView patientCurrentStatusView;
    private TextView patientLastStatusView;
    Handler handler;
    private boolean gonnaSendQuestion = false;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        messagesDBHelper = MessagesDBHelper.getInstance(this);
        netThread = ((EyeconizeFamilyApplication)getApplication()).getNetThread();
        setContentView(R.layout.activity_send_messages_detailed);
        connectionManager = ConnectionManager.getInstance();
        messagesFragment = new MessagesFragment(MessagesFragment.DETAILED_MODE, this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.send_message_detailed_frame_layout, messagesFragment)
                .commit();
        sendButton = findViewById(R.id.sendMessageButton);
        sendContent = findViewById(R.id.sendMessageEditText);
        goToMainButton = findViewById(R.id.GoToMainButton);
        batteryView = findViewById(R.id.ChatBattery);
        deviceStatusView = findViewById(R.id.ChatDeviceStatus);
        patientCurrentStatusView = findViewById(R.id.ChatPatientCurrentStatus);
        patientLastStatusView = findViewById(R.id.ChatPatientLastStatus);
        goToMainButton.setOnClickListener(v->{
            Intent intent = new Intent(SendMessageActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        Global.messageSerial = messagesDBHelper.getLastSerial(Global.receiverID);
        connectionManager.registerPageObserver(this);


        alterButtonStatus(false);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                alterButtonStatus(!s.toString().trim().equals(""));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                alterButtonStatus(!s.toString().trim().equals(""));
            }

            @Override
            public void afterTextChanged(Editable s) {
                alterButtonStatus(!s.toString().trim().equals(""));
            }
        };
        sendContent.addTextChangedListener(textWatcher);

        View.OnTouchListener swipeListener = new View.OnTouchListener() {
            private long pressStartTime;
            private float pressedX;
            private float pressedY;

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        pressStartTime = System.currentTimeMillis();
                        pressedX = event.getX();
                        pressedY = event.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        long pressDuration = System.currentTimeMillis() - pressStartTime;
                        float distance = getDistance(pressedX, pressedY, event.getX(), event.getY());
                        if (pressDuration < MAX_CLICK_DURATION && distance < MAX_CLICK_DISTANCE) {
                          //正常点击
                            String message = sendContent.getText().toString();
                            if (gonnaSendQuestion) {
                                sendQuestion(message, Global.receiverID);
                            }else{
                                sendMessage(message, Global.receiverID);
                            }


                        } else if (event.getX() < pressedX && Math.abs(event.getY()-pressedY)<MIN_SWIPE_DISTANCE*2 && distance > MIN_SWIPE_DISTANCE) {
                            //左滑
                            gonnaSendQuestion = false;
                            alterButtonStatus();
                        } else if (event.getX() > pressedX && Math.abs(event.getY()-pressedY)<MIN_SWIPE_DISTANCE*2 && distance > MIN_SWIPE_DISTANCE) {
                            //左滑
                            gonnaSendQuestion = true;
                            alterButtonStatus();
                        }
                        break;
                }
                return true;
            }
            private float getDistance(float x1, float y1, float x2, float y2) {
                float dx = x1 - x2;
                float dy = y1 - y2;
                return (float) Math.sqrt(dx * dx + dy * dy);
            }
        };
        sendButton.setOnTouchListener(swipeListener);

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message message) {
                System.out.println("Message received.");
                switch (message.what) {
                    case MessageTypes.GO_TO_SEND_MESSAGES:

                        break;
                    case MessageTypes.HANDLER_NEW_MESSAGE:
                        if (((ChatMessage) message.obj).senderID != Global.receiverID) {
                            Global.receiverID = ((ChatMessage) message.obj).senderID;
                            messagesFragment.refreshDatabase(Global.receiverID);
                        }
                        messagesFragment.getMessage((ChatMessage) message.obj);
                        if (((ChatMessage) message.obj).needToReply) {
                            AskAvailableDialog askAvailableDialog = new AskAvailableDialog(SendMessageActivity.this, R.style.AskAvailableStyle, (ChatMessage) message.obj, connectionManager, SendMessageActivity.this);
                            askAvailableDialog.show();
                        }
                    default:
                        break;

                }
            }
        };


    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        connectionManager.unregisterPageObserver(this);

        System.out.println("main view destroyed.");
    }
    private void renewMessageSerial(){
        Global.messageSerial = messagesDBHelper.getLastSerial(Global.receiverID);
    }

    @Override
    protected void onStart(){
        super.onStart();
        connectionManager.registerPageObserver(this);


    }

    @Override
    public void sendMessage(String content, long receiverID) {
        if (!content.trim().equals("")) {
            Global.receiverID = receiverID;
            //TODO 完善页面跳转逻辑，当消息接收者不是当前接收者时改变显示内容
            renewMessageSerial();

            System.out.println("connection manager id:");
            System.out.println(connectionManager.getSelfID());
            Global.messageSerial++;
            Global.messageSerial = (short)(Global.messageSerial%10000);
            long sendTime = System.currentTimeMillis();

            messagesFragment.addMessage(new ChatMessage(content, Global.selfID, sendTime, Global.messageSerial, ChatMessage.SENDING));

            new Thread(()->{
                connectionManager.sendTextMessage(content, receiverID, Global.messageSerial, sendTime);
            }).start();
            sendContent.setText("");
        }
    }
    public void sendQuestion(String content, long receiverID) {
        if (!content.trim().equals("")) {
            Global.receiverID = receiverID;
            //TODO 完善页面跳转逻辑，当消息接收者不是当前接收者时改变显示内容
            renewMessageSerial();

            System.out.println("connection manager id:");
            System.out.println(connectionManager.getSelfID());
            Global.messageSerial++;
            Global.messageSerial = (short)(Global.messageSerial%10000);
            long sendTime = System.currentTimeMillis();

            messagesFragment.addMessage(new ChatMessage(content, Global.selfID, sendTime, Global.messageSerial, ChatMessage.SENDING));

            new Thread(()->{
                connectionManager.sendQuestionMessage(content, receiverID, Global.messageSerial);
            }).start();
            sendContent.setText("");
        }
    }

    @Override
    public void newMessageAlert(ChatMessage chatMessage) {
        Message message = handler.obtainMessage(MessageTypes.HANDLER_NEW_MESSAGE, chatMessage);
        handler.sendMessage(message);
    }

    private void alterButtonStatus() {
        alterButtonStatus(!sendContent.getText().toString().trim().equals(""));
    }

    private void alterButtonStatus(boolean activated) {
        sendButton.setEnabled(activated);
        if (gonnaSendQuestion) {
            if (activated) {

                sendButton.setBackgroundResource(R.drawable.send_button_question);
            }else{
                sendButton.setBackgroundResource(R.drawable.send_button_question_deactivated);
            }
        }
        else{
            if(activated){
                sendButton.setBackgroundResource(R.drawable.send_button_normal);
            }else{
                sendButton.setBackgroundResource(R.drawable.send_button_normal_deactivated);
            }
        }
    }
}
