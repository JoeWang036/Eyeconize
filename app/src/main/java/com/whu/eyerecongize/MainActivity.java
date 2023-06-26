package com.whu.eyerecongize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.whu.eyerecongize.permission.Permissions;
import com.whu.eyerecongize.permission.RequestPermit;
import com.whu.eyerecongize.permission.RequestRes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    Button bt;

    RequestPermit requestPermissions;
    RequestRes requestPermissionsResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt=findViewById(R.id.button2);
        //申请权限
        requestPermissions = RequestPermit.getInstance();//动态权限请求
        requestPermissionsResult = RequestRes.getInstance();//动态权限请求结果处理
        requestPermissions.requestPermissions(this, Permissions.permissions,100);
    }

    public void click(View view){
        Intent it=new Intent();
        it.setClass(this, testActivity.class);
        startActivity(it);
    }

    //用户授权操作结果（可能授权了，也可能未授权）
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}

