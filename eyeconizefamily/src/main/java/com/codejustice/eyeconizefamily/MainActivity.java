package com.codejustice.eyeconizefamily;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private DualFragment dualFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 创建 DualFragment 实例并添加到布局中
        dualFragment = new DualFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, dualFragment)
                .commit();


    }
    @Override
    protected void onStart(){
        super.onStart();        // 替换 FamilyPickerFragment
        PickPatientsFragment familyPickerFragment = new PickPatientsFragment();
        dualFragment.replaceFamilyPickerFragment(familyPickerFragment);

        // 替换 ChatFragment
        MessagesFragment chatFragment = new MessagesFragment();
        dualFragment.replaceChatFragment(chatFragment);

    }
}
