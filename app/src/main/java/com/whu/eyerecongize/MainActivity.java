package com.whu.eyerecongize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.whu.eyerecongize.permission.CheckPermit;
import com.whu.eyerecongize.permission.Permissions;
import com.whu.eyerecongize.permission.RequestPermit;
import com.whu.eyerecongize.permission.RequestRes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    RequestPermit requestPermissions;
    RequestRes requestPermissionsResult;

    private static final long DELAY_TIME = 1200; // 延迟时间，单位为毫秒

    private Handler handler;

    String[] permissions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //申请权限
        requestPermissions = RequestPermit.getInstance();//动态权限请求
        requestPermissionsResult = RequestRes.getInstance();//动态权限请求结果处理
        permissions=Permissions.getRequiredPermissions(this);
        requestPermissions.requestPermissions(this, permissions,100);
        boolean premissionRes=CheckPermit.checkPermissionAllGranted(this,permissions);
        setStatusBar();
        System.out.println(premissionRes);
        if(premissionRes){
            handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 跳转到目标页面
                    Intent intent = new Intent(MainActivity.this, HomePage1.class);
                    startActivity(intent);
                    finish(); // 结束当前页面
                }
            }, DELAY_TIME);
        }
    }

    private void setStatusBar() {
        // SDK 21/Android 5.0.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            View decorView = this.getWindow().getDecorView();
            int setting = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(setting);
            // Set the status bar to transparent.
            this.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    //用户授权操作结果（可能授权了，也可能未授权）
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (CheckPermit.checkPermissionAllGranted(this,permissions)) {
                Intent intent = new Intent(MainActivity.this, HomePage1.class);
                startActivity(intent);
                finish(); // 结束当前页面

            } else if(grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
                System.out.println("111");
                finish();
            }
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        // 移除延迟执行的任务，以防止内存泄漏
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

}

