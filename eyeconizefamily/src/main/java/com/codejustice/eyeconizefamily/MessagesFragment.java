package com.codejustice.eyeconizefamily;

import android.annotation.SuppressLint;
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

import com.codejustice.entities.ChatMessage;
import com.codejustice.entities.ChatMessageAbstract;
import com.codejustice.entities.TimeShowingMessage;
import com.codejustice.enums.MessageTypes;
import com.codejustice.eyeconizefamily.databinding.FragmentMessagesBinding;
import com.codejustice.global.Global;
import com.codejustice.utils.db.MessagesDBHelper;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {



    public static final int DUAL_MODE = 0;
    public static final int DETAILED_MODE = 1;
    private int mode;

    private ChatContentAdapter chatContentAdapter;
    private MessagesDBHelper messagesDbHelper;
    private RecyclerView chatView;
    private LinearLayoutManager layoutManager;
    private List<ChatMessageAbstract> messages;
    private long lastClickedTime = 0;
    private long newestTime = 0;
    private FragmentMessagesBinding binding;
    public MessagesFragment(int arg) {
        mode = arg;
    }

    // 在此处实现 onCreateView 方法
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 使用 binding 来绑定布局文件
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        messagesDbHelper = new MessagesDBHelper(requireContext());
        messagesDbHelper.switchTable(123456, 5);


        // 初始化消息数据列表
        // 添加测试数据
        for (int i = 0; i < 4; i++) {
            ChatMessage cm;
            if (i % 2 == 0) {
                cm = new ChatMessage("救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我"+i, 2, i*100+200000);
            } else {
                cm = new ChatMessage("没空没空没空没空没空没空没空没空没空没空没空没空没空没空没空没空"+i, Global.selfID, i*100+200000);
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
        if (messages.size() > 0) {
            newestTime = ((ChatMessage)(messages.get(messages.size() - 1))).timestamp;
        }

        return rootView;
    }

    public void addMessage(ChatMessage message) {

        if (message.timestamp - newestTime > 30000) {
            messages.add(new TimeShowingMessage(message.timestamp));
        }
        newestTime = message.timestamp;

        messages.add(message);
        messagesDbHelper.insertData(message);
        int position = messages.size() - 1;
        chatContentAdapter.notifyItemInserted(position);
        chatView.scrollToPosition(position);

    }

    class Cell extends RecyclerView.ViewHolder{

        public Cell(@NonNull View itemView) {
            super(itemView);
        }
    }
    class ChatCell extends Cell {
        ImageView profilePic;
        TextView chatMessage;

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
            if (cm != null) {
                if (cm instanceof ChatMessage) {
                    ((ChatCell)holder).chatMessage.setText(((ChatMessage)cm).messageContent);
                }
                else {
                    ((TimeCell)holder).TimeString.setText(((TimeShowingMessage)cm).timeString);
                }
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

}
