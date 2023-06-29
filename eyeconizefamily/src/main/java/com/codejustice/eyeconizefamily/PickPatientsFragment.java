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

import com.codejustice.entities.FriendEntity;
import com.codejustice.eyeconizefamily.databinding.FragmentPickPatientsBinding;

import java.util.ArrayList;
import java.util.List;

public class PickPatientsFragment extends Fragment {
    private RecyclerView familyPickerView;
    private FamilyPickerAdapter familyPickerAdapter;
    private List<FriendEntity> myFamilyList = new ArrayList<>();
    private FragmentPickPatientsBinding binding;

    // 在此处实现 onCreateView 方法
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 使用 binding 来绑定布局文件
        binding = FragmentPickPatientsBinding.inflate(inflater, container, false);
        View rootView = binding.getRoot();

        // 初始化 RecyclerView
        familyPickerView = rootView.findViewById(R.id.pick_family_view);
        familyPickerAdapter = new FamilyPickerAdapter();
        familyPickerView.setAdapter(familyPickerAdapter);
        familyPickerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // 添加测试数据
        for (int i = 0; i < 20; i++) {
            FriendEntity fm = new FriendEntity("家人" + i, "我需要钱");
            myFamilyList.add(fm);
        }
        familyPickerAdapter.notifyDataSetChanged();

        return rootView;
    }


    class FamilyCell extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView name;
        TextView lastMessage;

        public FamilyCell(@NonNull View itemView) {
            super(itemView);
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

        }

        @Override
        public int getItemCount() {
            return myFamilyList.size();
        }
    }
}
