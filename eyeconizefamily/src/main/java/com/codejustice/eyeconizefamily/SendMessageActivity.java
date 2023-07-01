package com.codejustice.eyeconizefamily;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.codejustice.entities.ChatMessage;
import com.codejustice.enums.MessageTypes;
import com.codejustice.global.Global;
import com.codejustice.netservice.NetThread;

import NetService.ConnectionUtils.ConnectionManager;
import NetService.MessageProtocol.TextMessage;
import NetService.MessageProtocol.TextMessageCoder;

public class SendMessageActivity extends AppCompatActivity {


    private final long MAX_CLICK_DURATION = 1000;
    private final float MAX_CLICK_DISTANCE = 40;
    private final float MIN_SWIPE_DISTANCE = 50;
    private final float HORIZONTAL_SWIPE_THRESH = 50;

    private NetThread netThread;

    private MessagesFragment messagesFragment;
    private ConnectionManager connectionManager;
    private Button sendButton;
    private EditText sendContent;
    public FrameLayout FamilyStatusHead;
    private ImageButton goToMainButton;
    private TextView batteryView;
    private TextView deviceStatusView;
    private TextView patientCurrentStatusView;
    private TextView patientLastStatusView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        netThread = ((EyeconizeFamilyApplication)getApplication()).getNetThread();
        setContentView(R.layout.activity_send_messages_detailed);
        connectionManager = ConnectionManager.getInstance();
        messagesFragment = new MessagesFragment(MessagesFragment.DETAILED_MODE);
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



        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                sendButton.setEnabled(!s.toString().trim().equals(""));
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendButton.setEnabled(!s.toString().trim().equals(""));
            }

            @Override
            public void afterTextChanged(Editable s) {
                sendButton.setEnabled(!s.toString().trim().equals(""));
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
                            if (!message.trim().equals("")) {
                                System.out.println("connection manager id:");
                                System.out.println(connectionManager.getSelfID());
                                new Thread(()->{
                                    connectionManager.sendTextMessage(message, 5);
                                }).start();
                                sendContent.setText("");
                            }

                        } else if (event.getY() < pressedY && distance > MIN_SWIPE_DISTANCE) {
                          //上滑
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


    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        System.out.println("main view destroyed.");
    }


    @Override
    protected void onStart(){
        super.onStart();

    }

}
