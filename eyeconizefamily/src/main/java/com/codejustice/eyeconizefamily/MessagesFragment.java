package com.codejustice.eyeconizefamily;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import NetService.ConnectionUtils.ChatMessage;
import NetService.ConnectionUtils.ChatMessageAbstract;
import NetService.ConnectionUtils.TimeShowingMessage;
import com.codejustice.enums.MessageTypes;
import com.codejustice.eyeconizefamily.databinding.FragmentMessagesBinding;
import com.codejustice.global.Global;
import com.codejustice.global.tempProfile;
import com.codejustice.utils.db.MessagesDBHelper;

import java.util.List;

import NetService.ConnectionUtils.ConnectionManager;
import NetService.ConnectionUtils.MessageObserver;
import NetService.MessageProtocol.ConfirmMessage;

public class MessagesFragment extends Fragment implements MessageObserver {

    private boolean handlerEnabled = false;


    public static final int DUAL_MODE = 0;
    public static final int DETAILED_MODE = 1;
    public static int num = 0;
    private int mode;
    Handler handler;

    private ChatContentAdapter chatContentAdapter;
    private MessagesDBHelper messagesDbHelper;
    private RecyclerView chatView;
    private LinearLayoutManager layoutManager;

    private ConnectionManager connectionManager;
    private List<ChatMessageAbstract> messages;
    private long lastClickedTime = 0;
    private long newestTime = 0;
    private FragmentMessagesBinding binding;
    public MessagesFragment(int arg, Context context) {
        mode = arg;
        messagesDbHelper = MessagesDBHelper.getInstance(context);
    }

    // 在此处实现 onCreateView 方法
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 使用 binding 来绑定布局文件
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        connectionManager = ConnectionManager.getInstance();
        connectionManager.registerMessageObserver(this);

        View rootView = binding.getRoot();
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if (handlerEnabled) {
                    switch (msg.what) {
                        case MessageTypes.HANDLER_NEW_MESSAGE:
                            ChatMessage cm = (ChatMessage) msg.obj;
                            System.out.println("adding...");
                            System.out.println(mode);
                            addMessageWithoutRenewingDatabase(cm);
                            break;
                        case MessageTypes.HANDLER_DONE_INITIALIZATION:
                            break;
                        case MessageTypes.HANDLER_UPDATE_RECYCLER:
                            ChatMessage message = (ChatMessage) msg.obj;
                            refreshContent(message);
                            break;
                        case MessageTypes.HANDLER_MESSAGE_FAIL:
                            FailMessage failMessage = (FailMessage)msg.obj;
                            System.out.println("setting failed message...");
                            System.out.println(failMessage.receiverID);
                            System.out.println(failMessage.messageSerial);
                            System.out.println(failMessage.sendTime);
                            messagesDbHelper.updateSentStatusFail(failMessage.receiverID, failMessage.sendTime, failMessage.messageSerial);
                            refreshContent();
                            break;
                        case MessageTypes.HANDLER_REFRESH_TABLE:

                            messagesDbHelper.switchTable(Global.selfID, (long)(msg.obj));
                            refreshContent();
                        default:
                            break;
                    }
                }
            }
        };

        messagesDbHelper = MessagesDBHelper.getInstance(requireContext());
        messagesDbHelper.switchTable(Global.selfID, Global.receiverID);

        // 初始化消息数据列表
        // 添加测试数据
        for (int i = 0; i < 4; i++) {
            ChatMessage cm;
            if (i % 2 == 0) {
                cm = new ChatMessage("救我救我救我救我救我救我" + i, Global.receiverID, i * 100 + 200000, ((short) i), ChatMessage.SENT);
            } else {
                cm = new ChatMessage("没空没空没空没空没空没空没空没空没空没空没空没空没空没空没空没空"+i, Global.selfID, i*100+200000,((short) i), ChatMessage.SENT);
            }
            messagesDbHelper.insertData(cm);
        }
        messages = messagesDbHelper.getChatMessages();
        chatView = binding.chatContentRecycler;
        chatContentAdapter = new ChatContentAdapter();
        chatView.setAdapter(chatContentAdapter);
        layoutManager = new LinearLayoutManager(requireContext());
        //TODO 设置滚动使得滚动到底部不出现误差
        layoutManager.setStackFromEnd(true);
        chatView.setLayoutManager(layoutManager);
        System.out.println("size of messages:");
        System.out.println(messages.size());
        if (messages.size() > 0) {
            newestTime = ((ChatMessage)(messages.get(messages.size() - 1))).timestamp;
        }

        return rootView;
    }

    private void refreshContent(ChatMessage chatMessage){
        messagesDbHelper.switchTable(Global.selfID, Global.receiverID);
        messages = messagesDbHelper.getChatMessages();
        int position = messages.size() - 1;
        System.out.println("message size:"+messages.size());
        System.out.println("notifying data set changed.");
        chatContentAdapter.notifyDataSetChanged();
        chatView.scrollToPosition(position);
    }




    public void refreshDatabase(long newID) {
        Message msg = handler.obtainMessage(MessageTypes.HANDLER_REFRESH_TABLE, newID);
        handler.sendMessage(msg);
    }

    private void refreshContent(){
        messages = messagesDbHelper.getChatMessages();
        int position = messages.size() - 1;
        chatContentAdapter.notifyDataSetChanged();
        chatView.scrollToPosition(position);
    }
    @Override
    public void onPause() {

        super.onPause();
        connectionManager.unregisterMessageObserver(this);
        handlerEnabled = false;
    }
    @Override
    public void onResume(){
        super.onResume();
        handlerEnabled = true;
        connectionManager.registerMessageObserver(this);

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        connectionManager.unregisterMessageObserver(this);
        handlerEnabled = false;

    }


    public void addMessageWithoutRenewingDatabase(ChatMessage message) {

        if (message.timestamp - newestTime > 30000) {
            messages.add(new TimeShowingMessage(message.timestamp));
        }
        newestTime = message.timestamp;
        System.out.println("adding message.....");

        messages.add(message);
        if (mode == DUAL_MODE) {
            Intent intent = new Intent(MessageTypes.ACTION_REFRESH_FAMILY_PICKER_CONTENT);
            intent.putExtra(MessageTypes.INTENT_EXTRA_NEW_USER_ID, Global.receiverID);
            requireContext().sendBroadcast(intent);
        }

        int position = messages.size() - 1;
        chatContentAdapter.notifyItemInserted(position);
        chatView.scrollToPosition(position);
    }
    public void addMessageAndRenewDatabase(ChatMessage message) {

        if (message.timestamp - newestTime > 30000) {
            messages.add(new TimeShowingMessage(message.timestamp));
        }
        newestTime = message.timestamp;
        System.out.println("adding message.....");

        messages.add(message);
        messagesDbHelper.insertData(message);
        if (mode == DUAL_MODE) {
            Intent intent = new Intent(MessageTypes.ACTION_REFRESH_FAMILY_PICKER_CONTENT);
            intent.putExtra(MessageTypes.INTENT_EXTRA_NEW_USER_ID, Global.receiverID);
            requireContext().sendBroadcast(intent);
        }

        int position = messages.size() - 1;
        chatContentAdapter.notifyItemInserted(position);
        chatView.scrollToPosition(position);
    }
    private void renew(ChatMessage message){
    }


    //收到消息时对数据库进行更新

    public void getMessage(ChatMessage chatMessage) {
        System.out.println("getting message...");
        Message message = handler.obtainMessage(MessageTypes.HANDLER_NEW_MESSAGE, chatMessage);
        handler.sendMessage(message);
    }

    @Override
    public void messageFail(long receiverID, long sendTime, short messageSerial) {
        //TODO 增加消息发送失败的逻辑
        FailMessage failMessage = new FailMessage(receiverID, sendTime, messageSerial);
        Message message = handler.obtainMessage(MessageTypes.HANDLER_MESSAGE_FAIL, failMessage);
        handler.sendMessage(message);

    }

    @Override
    public void updateSentStatus(ConfirmMessage confirmMessage) {
        ChatMessage cm = messagesDbHelper.findUpdatedMessage(confirmMessage);
        Message message = handler.obtainMessage(MessageTypes.HANDLER_UPDATE_RECYCLER, cm);
        handler.sendMessage(message);
    }

    class Cell extends RecyclerView.ViewHolder{

        public Cell(@NonNull View itemView) {
            super(itemView);
        }
    }
    class ChatCell extends Cell {
        ImageView profilePic;
        TextView chatMessage;
        ImageView bg;

        public ChatCell(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.chat_profile_pic);
            chatMessage = itemView.findViewById(R.id.messageContent);
        }
    }

    class TimeCell extends Cell {
        TextView TimeString;
        public TimeCell(@NonNull View itemView) {
            super(itemView);
            TimeString = itemView.findViewById(R.id.chat_cell_time);
        }
    }


    class ChatContentAdapter extends RecyclerView.Adapter<Cell> {
        private static final byte VIEW_TYPE_LEFT = 0;
        private static final byte VIEW_TYPE_RIGHT = 1;
        private static final byte VIEW_TYPE_TIME = 2;

        @NonNull
        @Override
        public Cell onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view;
            switch (viewType) {
                case VIEW_TYPE_LEFT:
                    view = inflater.inflate(R.layout.chat_view_left, parent, false);
                    break;
                case VIEW_TYPE_RIGHT:
                    view = inflater.inflate(R.layout.chat_view_right, parent, false);

                    break;
                default:
                    view = inflater.inflate(R.layout.chat_view_time, parent, false);
                    break;
            }

            if (mode == DUAL_MODE) {
                System.out.println("setting on click listener.");
                view.setOnClickListener(v->{
                    if (lastClickedTime == 0) {
                        lastClickedTime = SystemClock.uptimeMillis();
                    }
                    else{
                        long nowClickedTime = SystemClock.uptimeMillis();
                        long interval = nowClickedTime -lastClickedTime;
                        if (interval < 200) {
                            // 双击事件处理
                            Intent intent = new Intent(MessageTypes.ACTION_GO_TO_SEND_MESSAGES);

                            requireContext().sendBroadcast(intent);
                            System.out.println("Broadcast sent.");
                        }

                        lastClickedTime = nowClickedTime;
                    }
                });
            }
            if (viewType == VIEW_TYPE_TIME) {
                return new TimeCell(view);
            } else {
                return new ChatCell(view);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull Cell holder, int position) {
            ChatMessageAbstract cm = messages.get(position);
            if (cm == null) {
                return;
            }
            if (cm instanceof ChatMessage) {
                ((ChatCell)holder).chatMessage.setText(((ChatMessage)cm).messageContent);
                if (tempProfile.profile.containsKey(((ChatMessage) cm).senderID)) {
                    ((ChatCell)holder).profilePic.setImageResource(tempProfile.profile.get(((ChatMessage) cm).senderID));
                }

                if (((ChatMessage) cm).senderID == Global.selfID) {
                    switch (((ChatMessage) cm).sentStatus) {
                        case ChatMessage.SENT:
                            ((ChatCell) holder).chatMessage.setBackgroundResource(R.drawable.chat_bubble_right_green);
                            break;
                        case ChatMessage.FAILED:
                            ((ChatCell) holder).chatMessage.setBackgroundResource(R.drawable.chat_bubble_right_failed);
                            break;
                        case ChatMessage.SENDING:
                            ((ChatCell) holder).chatMessage.setBackgroundResource(R.drawable.chat_bubble_right_sending);
                    }
                }
            }
            else {
                ((TimeCell)holder).TimeString.setText(((TimeShowingMessage)cm).timeString);
            }

        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            ChatMessageAbstract message = messages.get(position);
            if (message instanceof ChatMessage) {
                if (((ChatMessage)message).senderID == Global.selfID) {
                    return VIEW_TYPE_RIGHT;
                }
                else{
                    return VIEW_TYPE_LEFT;
                }
            }
            else {
                return VIEW_TYPE_TIME;
            }

        }

    }
    class FailMessage{
        long receiverID;
        long sendTime;
        short messageSerial;

        public FailMessage(long receiverID, long sendTime, short messageSerial) {
            this.receiverID = receiverID;
            this.sendTime = sendTime;
            this.messageSerial = messageSerial;
        }
    }

}
