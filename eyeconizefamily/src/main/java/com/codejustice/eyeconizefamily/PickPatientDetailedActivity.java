package com.codejustice.eyeconizefamily;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PickPatientDetailedActivity extends AppCompatActivity {
    private PickPatientsFragment pickPatientsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_patient);
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

}
