package com.codejustice.eyeconizefamily;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codejustice.entities.FamilyEntity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView familyPickerView;
    FamilyPickerAdapter familyPickerAdapter;
    List<FamilyEntity> myFamilyList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        familyPickerView = findViewById(R.id.pick_family_view);
        for (int i = 0; i < 20; i++) {
            FamilyEntity fm = new FamilyEntity("家人" + i, "我需要钱");
            myFamilyList.add(fm);
        }
        familyPickerAdapter = new FamilyPickerAdapter();
        familyPickerView.setAdapter(familyPickerAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        familyPickerView.setLayoutManager(layoutManager);

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
            View view = View.inflate(MainActivity.this, R.layout.family_cell, null);
            FamilyCell familyCell = new FamilyCell(view);
            return familyCell;
        }

        @Override
        public void onBindViewHolder(@NonNull FamilyCell holder, int position) {
            FamilyEntity familyEntity = myFamilyList.get(position);
            holder.name.setText(familyEntity.familyName);
            holder.lastMessage.setText(familyEntity.lastMessage);

        }

        @Override
        public int getItemCount() {
            return myFamilyList.size();
        }
    }


}
