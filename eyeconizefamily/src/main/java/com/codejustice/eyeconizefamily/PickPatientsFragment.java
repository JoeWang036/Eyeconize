package com.codejustice.eyeconizefamily;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

import com.codejustice.entities.FriendEntity;
import com.codejustice.enums.MessageTypes;
import com.codejustice.eyeconizefamily.databinding.FragmentPickPatientsBinding;
import com.codejustice.global.Global;
import com.codejustice.utils.db.FriendsDBHelper;

import java.util.ArrayList;
import java.util.List;

public class PickPatientsFragment extends Fragment {

    public static final int DUAL_MODE = 0;
    public static final int DETAILED_MODE = 1;
    private long lastClickedTime = 0;

    private int mode;

    private RecyclerView familyPickerView;
    private FamilyPickerAdapter familyPickerAdapter;
    private List<FriendEntity> myFamilyList = new ArrayList<>();
    private FragmentPickPatientsBinding binding;
    private FriendsDBHelper friendsDBHelper;

    public PickPatientsFragment(int mode) {
        this.mode = mode;
    }

    // 在此处实现 onCreateView 方法
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 使用 binding 来绑定布局文件
        binding = FragmentPickPatientsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        friendsDBHelper = FriendsDBHelper.getInstance(requireContext());
        // 初始化 RecyclerView
        familyPickerView = rootView.findViewById(R.id.pick_family_view);
        familyPickerAdapter = new FamilyPickerAdapter();
        familyPickerView.setAdapter(familyPickerAdapter);
        familyPickerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        friendsDBHelper.switchTable(Global.selfID);
        // 添加测试数据
        for (int i = 0; i < 20; i++) {
            FriendEntity fm = new FriendEntity("家人" + i, "我需要钱", i);
            friendsDBHelper.insertData(fm);
        }
        myFamilyList = friendsDBHelper.getFriends();
        familyPickerAdapter.notifyDataSetChanged();

        return rootView;
    }


    class FamilyCell extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView name;
        TextView lastMessage;
        View view;

        public FamilyCell(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            name = itemView.findViewById(R.id.family_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            profilePic = itemView.findViewById(R.id.chat_profile_pic);
        }
    }


    class FamilyPickerAdapter extends RecyclerView.Adapter<FamilyCell> {

        @NonNull
        @Override
        public FamilyCell onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.family_cell, parent, false);
            FamilyCell familyCell = new FamilyCell(view);


            return familyCell;
        }



        @Override
        public void onBindViewHolder(@NonNull FamilyCell holder, int position) {
            FriendEntity friendEntity = myFamilyList.get(position);
            holder.name.setText(friendEntity.friendName);
            holder.lastMessage.setText(friendEntity.lastMessage);

            if (mode == DUAL_MODE) {
                System.out.println("setting on click listener for pick.");
                holder.view.setOnClickListener(v->{

                    Intent newIntent = new Intent(MessageTypes.ACTION_ALTER_CHAT_CONTENTS);
                    newIntent.putExtra(MessageTypes.INTENT_EXTRA_NEW_USER_ID, friendEntity.friendID);
                    requireContext().sendBroadcast(newIntent);
                    if (lastClickedTime == 0) {
                        lastClickedTime = SystemClock.uptimeMillis();
                    }
                    else{
                        long nowClickedTime = SystemClock.uptimeMillis();
                        long interval = nowClickedTime -lastClickedTime;
                        if (interval < 200) {
                            // 双击事件处理
                            Intent intent = new Intent(MessageTypes.ACTION_GO_TO_PICK_PATIENTS);
                            requireContext().sendBroadcast(intent);
                            System.out.println("Broadcast sent.");
                        }

                        lastClickedTime = nowClickedTime;
                    }
                });
            }
            else{
                holder.view.setOnClickListener(v->{
                    Intent newIntent = new Intent(MessageTypes.ACTION_ALTER_CHAT_CONTENTS);
                    newIntent.putExtra(MessageTypes.INTENT_EXTRA_NEW_USER_ID, friendEntity.friendID);
                    requireContext().sendBroadcast(newIntent);
                });
            }
        }

        @Override
        public int getItemCount() {
            return myFamilyList.size();
        }
    }
}
