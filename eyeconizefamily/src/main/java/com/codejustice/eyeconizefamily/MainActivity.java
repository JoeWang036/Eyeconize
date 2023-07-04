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

import com.codejustice.enums.MessageTypes;

public class MainActivity extends AppCompatActivity {

    private DualFragment dualFragment;
    Handler handler;

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 取消注册广播接收器
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        MessagesFragment chatFragment = new MessagesFragment(MessagesFragment.DUAL_MODE);

        dualFragment.replaceChatFragment(chatFragment);



    }
}
