package com.codejustice.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import NetService.ConnectionUtils.ChatMessage;
import NetService.ConnectionUtils.ConnectionManager;

import com.codejustice.eyeconizefamily.R;
import com.codejustice.eyeconizefamily.ReplierActivity;
import com.codejustice.global.Global;
import com.codejustice.utils.db.FriendsDBHelper;

public class AskAvailableDialog extends Dialog {
    private String message;
    private Button availableButton;
    private Button unavailableButton;
    private TextView messageView;
    private FriendsDBHelper friendsDBHelper;
    private ConnectionManager connectionManager;

    private ReplierActivity replierActivity;

    public AskAvailableDialog(@NonNull Context context, int theme, ChatMessage chatMessage, ConnectionManager connectionManager, ReplierActivity replierActivity) {
        super(context, theme);
        friendsDBHelper = FriendsDBHelper.getInstance(context);
        init(chatMessage);
        this.connectionManager = connectionManager;
        this.replierActivity = replierActivity;
    }

    public void init(ChatMessage chatMessage){
        String content = chatMessage.messageContent;
        String senderName = friendsDBHelper.getFriendNicknameByID(chatMessage.senderID, Global.selfID);

        setContentView(R.layout.dialog_ask_available);
        availableButton = findViewById(R.id.available_button_ask);
        unavailableButton = findViewById(R.id.unavailable_button_ask);
        messageView = findViewById(R.id.message_content_ask_available);
        availableButton.setOnClickListener(v->{
            replierActivity.sendMessage("这就来！", chatMessage.senderID);
            //TODO 完成点击“有空”按钮的行为
            dismiss();
        });
        messageView.setText(senderName + ": " + content);
        unavailableButton.setOnClickListener(v->{
            replierActivity.sendMessage("抱歉我没空", chatMessage.senderID);
            dismiss();;
        });
    }
}
