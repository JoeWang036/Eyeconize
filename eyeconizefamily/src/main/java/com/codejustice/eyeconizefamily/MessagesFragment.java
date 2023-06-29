package com.codejustice.eyeconizefamily;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.codejustice.eyeconizefamily.databinding.FragmentMessagesBinding;
import com.codejustice.utils.db.MessagesDBHelper;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {

    private ChatContentAdapter chatContentAdapter;
    private RecyclerView chatView;
    private LinearLayoutManager layoutManager;
    private List<ChatMessage> messages;
    private long selfID = 123456;
    private FragmentMessagesBinding binding;

    // 在此处实现 onCreateView 方法
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 使用 binding 来绑定布局文件
        binding = FragmentMessagesBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        MessagesDBHelper messagesDbHelper = new MessagesDBHelper(requireContext());
        messagesDbHelper.switchTable(123456, 5);


        // 初始化消息数据列表
        messages = new ArrayList<>();
        // 添加测试数据
        for (int i = 0; i < 20; i++) {
            ChatMessage cm;
            if (i % 2 == 0) {
                cm = new ChatMessage("救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我救我", 2, i);
            } else {
                cm = new ChatMessage("没空没空没空没空没空没空没空没空没空没空没空没空没空没空没空没空", selfID, i+100);
            }
            messages.add(cm);
            messagesDbHelper.insertData(cm);
        }
        chatView = binding.chatContentRecycler;
        chatContentAdapter = new ChatContentAdapter();
        chatView.setAdapter(chatContentAdapter);
        layoutManager = new LinearLayoutManager(requireContext());
        //TODO 设置滚动使得滚动到底部不出现误差
        layoutManager.setStackFromEnd(true);
        chatView.setLayoutManager(layoutManager);
        List<ChatMessage> dbList = messagesDbHelper.getChatMessages();
        System.out.println(dbList.size());

        return rootView;
    }


    class chatCell extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView chatMessage;

        public chatCell(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.chat_profile_pic);
            chatMessage = itemView.findViewById(R.id.messageContent);

        }
    }


    class ChatContentAdapter extends RecyclerView.Adapter<chatCell> {
        private static final byte VIEW_TYPE_LEFT = 0;
        private static final byte VIEW_TYPE_RIGHT = 1;

        @NonNull
        @Override
        public chatCell onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view;
            if (viewType == VIEW_TYPE_LEFT) {
                view = inflater.inflate(R.layout.chat_view_left, parent, false);
            } else {
                view = inflater.inflate(R.layout.chat_view_right, parent, false);
            }
            return new chatCell(view);
        }

        @Override
        public void onBindViewHolder(@NonNull chatCell holder, int position) {
            ChatMessage cm = messages.get(position);
            holder.chatMessage.setText(cm.messageContent);

        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (messages.get(position).senderID == selfID) {
                return VIEW_TYPE_RIGHT;
            }
            else{
                return VIEW_TYPE_LEFT;
            }

        }

    }

}
