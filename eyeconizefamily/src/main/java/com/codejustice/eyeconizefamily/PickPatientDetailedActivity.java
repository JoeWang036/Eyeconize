package com.codejustice.eyeconizefamily;

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

import NetService.ConnectionUtils.ChatMessage;
import NetService.ConnectionUtils.ConnectionManager;
import NetService.ConnectionUtils.PageObserver;
import NetService.MessageProtocol.TextMessage;

public class PickPatientDetailedActivity extends AppCompatActivity implements PageObserver, ReplierActivity {
    private PickPatientsFragment pickPatientsFragment;
    private Handler handler;
    private ConnectionManager connectionManager;
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
                        AskAvailableDialog askAvailableDialog =
                                new AskAvailableDialog(PickPatientDetailedActivity.this,
                                        R.style.AskAvailableStyle, (ChatMessage) message.obj,
                                        connectionManager, PickPatientDetailedActivity.this);
                        askAvailableDialog.show();
                    default:
                        break;
                }
            }
        };
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
        Global.SEND_NEW_MESSAGE = true;
        Global.receiverID = receiverID;
        Global.messageSerial++;
        Global.messageSerial = (short)(Global.messageSerial%10000);
        TextMessage textMessage = new TextMessage(content, Global.messageSerial, Global.selfID, System.currentTimeMillis());
        Global.messagesToSend.add(textMessage);
        onBackPressed();

    }

    @Override
    public void newMessageAlert(ChatMessage chatMessage) {
        Message message = handler.obtainMessage(MessageTypes.HANDLER_NEW_MESSAGE, chatMessage);
        handler.sendMessage(message);
    }
}
