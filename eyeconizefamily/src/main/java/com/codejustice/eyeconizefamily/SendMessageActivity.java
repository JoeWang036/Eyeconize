package com.codejustice.eyeconizefamily;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import NetService.ConnectionUtils.ChatMessage;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.listener.OnGuideChangedListener;
import com.app.hubert.guide.listener.OnLayoutInflatedListener;
import com.app.hubert.guide.listener.OnPageChangedListener;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.HighLight;
import com.app.hubert.guide.model.RelativeGuide;
import com.codejustice.dialogs.AskAvailableDialog;
import com.codejustice.enums.MessageTypes;
import com.codejustice.global.Global;
import com.codejustice.services.NetThread;
import com.codejustice.utils.db.FriendsDBHelper;
import com.codejustice.utils.db.MessagesDBHelper;

import NetService.ConnectionUtils.ConnectionManager;
import NetService.ConnectionUtils.PageObserver;

public class SendMessageActivity extends AppCompatActivity implements ReplierActivity, PageObserver {


    private final long MAX_CLICK_DURATION = 1000;
    private final float MAX_CLICK_DISTANCE = 40;
    private final float MIN_SWIPE_DISTANCE = 50;
    private final float HORIZONTAL_SWIPE_THRESH = 50;
    private TextView sendTipTextView;

    private NetThread netThread;

    private MessagesFragment messagesFragment;
    private ConnectionManager connectionManager;
    private ImageButton sendButton;
    private FriendsDBHelper friendsDBHelper;
    private boolean buttonActivated;
    private EditText sendContent;
    private ImageButton goToMainButton;
    private TextView batteryView;

    private MessagesDBHelper  messagesDBHelper;
    private TextView deviceStatusView;
    private TextView patientCurrentStatusView;
    private TextView patientLastStatusView;
    private TextView patientNameView;
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
        sendTipTextView = findViewById(R.id.send_messag_tip_text);
        patientLastStatusView = findViewById(R.id.ChatPatientLastStatus);
        patientNameView = findViewById(R.id.ChatFamilyName);
        friendsDBHelper = FriendsDBHelper.getInstance(this);
        goToMainButton.setOnClickListener(v->{
            Intent intent = new Intent(SendMessageActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        Global.messageSerial = messagesDBHelper.getLastSerial(Global.receiverID);
        connectionManager.registerPageObserver(this);
        patientNameView.setText(friendsDBHelper.getFriendNicknameByID(Global.receiverID, Global.selfID));


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
                        if (pressDuration < MAX_CLICK_DURATION && distance < MAX_CLICK_DISTANCE && buttonActivated) {
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

        initTeaching();

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

    public void initTeaching(){
        NewbieGuide.with(SendMessageActivity.this)
                .setLabel("teachMessage")
                //.alwaysShow(true)//总是显示，调试时可以打开
                .setOnGuideChangedListener(new OnGuideChangedListener() {
                    @Override
                    public void onShowed(Controller controller) {

                    }

                    @Override
                    public void onRemoved(Controller controller) {
                        finish();
                    }
                })
                .setOnPageChangedListener(new OnPageChangedListener() {
                    @Override
                    public void onPageChanged(int page) {
                        if(page==3){
                            gonnaSendQuestion=true;
                            alterButtonStatus();
                        }
                        if(page==5){
                            gonnaSendQuestion=false;
                            alterButtonStatus();
                        }
                    }
                })
                .addGuidePage(//第一页
                        GuidePage.newInstance()//创建一个实例
                                .setLayoutRes(R.layout.inst_page)//设置引导页布局
                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                    @Override
                                    public void onLayoutInflated(View view, Controller controller) {
                                        //引导页布局填充后回调，用于初始化
                                        ImageView ig = view.findViewById(R.id.imageViewinst);
                                        ig.setImageResource(R.drawable.inst5);
                                    }
                                })
                                .setBackgroundColor(0xd9D7D7D7)
                )
                .addGuidePage(//第二页
                        GuidePage.newInstance()//创建一个实例
                                .setLayoutRes(R.layout.inst_page)//设置引导页布局
                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                    @Override
                                    public void onLayoutInflated(View view, Controller controller) {
                                        //引导页布局填充后回调，用于初始化
                                        ImageView ig = view.findViewById(R.id.imageViewinst);
                                        ig.setImageResource(R.drawable.inst6);
                                    }
                                })
                                .setBackgroundColor(0xd9D7D7D7)
                )
                .addGuidePage(//第三页
                        GuidePage.newInstance()//创建一个实例
                                .setLayoutRes(R.layout.inst_but)//设置引导页布局
                                .addHighLight(sendButton, HighLight.Shape.RECTANGLE,20)
                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                    @Override
                                    public void onLayoutInflated(View view, Controller controller) {
                                        //引导页布局填充后回调，用于初始化
                                        ImageView ig = view.findViewById(R.id.imageViewbut);
                                        ig.setImageResource(R.drawable.inst7);
                                    }
                                })
                                .setBackgroundColor(0xd9D7D7D7)
                )
                .addGuidePage(//第四页
                        GuidePage.newInstance()//创建一个实例
                                .setLayoutRes(R.layout.inst_but)//设置引导页布局
                                .addHighLight(sendButton, HighLight.Shape.RECTANGLE,20)
                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                    @Override
                                    public void onLayoutInflated(View view, Controller controller) {
                                        //引导页布局填充后回调，用于初始化
                                        ImageView ig = view.findViewById(R.id.imageViewbut);
                                        ig.setImageResource(R.drawable.inst8);
                                    }
                                })
                                .setBackgroundColor(0xd9D7D7D7)
                )
                .addGuidePage(//第五页
                        GuidePage.newInstance()//创建一个实例
                                .setLayoutRes(R.layout.inst_but)//设置引导页布局
                                .addHighLight(sendButton, HighLight.Shape.RECTANGLE,20)
                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                    @Override
                                    public void onLayoutInflated(View view, Controller controller) {
                                        //引导页布局填充后回调，用于初始化
                                        ImageView ig = view.findViewById(R.id.imageViewbut);
                                        ig.setImageResource(R.drawable.inst9);
                                    }
                                })
                                .setBackgroundColor(0xd9D7D7D7)
                )
                .addGuidePage(//第六页
                        GuidePage.newInstance()//创建一个实例
                                .setLayoutRes(R.layout.inst_back)//设置引导页布局
                                .addHighLight(goToMainButton, HighLight.Shape.CIRCLE,30)
                                .setOnLayoutInflatedListener(new OnLayoutInflatedListener() {
                                    @Override
                                    public void onLayoutInflated(View view, Controller controller) {
                                        //引导页布局填充后回调，用于初始化
                                        ImageView ig = view.findViewById(R.id.imageViewback);
                                        ig.setImageResource(R.drawable.inst10);
                                    }
                                })
                                .setBackgroundColor(0xd9D7D7D7)
                )
                .addGuidePage(//第七页
                        GuidePage.newInstance()
                                .addHighLight(new RectF(10, 0, 1060, 200), HighLight.Shape.RECTANGLE,20,
                                        new RelativeGuide(R.layout.inst_low2,
                                                Gravity.BOTTOM, 10))
                                .setBackgroundColor(0xd9D7D7D7)
                )
                .show();

    }

    @Override
    protected void onStart(){
        super.onStart();
        connectionManager.registerPageObserver(this);


    }

    @Override
    protected void onResume(){
        super.onResume();
        ((EyeconizeFamilyApplication)getApplication()).setInForeground(true);
        connectionManager.registerPageObserver(this);

    }


    @Override
    protected void onPause(){
        super.onPause();
        System.out.println("pausing...");
        ((EyeconizeFamilyApplication)getApplication()).setInForeground(false);
        connectionManager.unregisterPageObserver(this);


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

            messagesFragment.addMessageAndRenewDatabase(new ChatMessage(content, Global.selfID, sendTime, Global.messageSerial, ChatMessage.SENDING));

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

            messagesFragment.addMessageAndRenewDatabase(new ChatMessage(content, Global.selfID, sendTime, Global.messageSerial, ChatMessage.SENDING));

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
        buttonActivated = activated;
        sendTipTextView.setText(genTip(gonnaSendQuestion));
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

    private String genTip(boolean isQuestion) {
        String head = "左右滑动按钮切换发送模式。当前模式：";
        if (isQuestion) {
            return head + "问题";
        } else {
            return head + "普通";
        }
    }
}
