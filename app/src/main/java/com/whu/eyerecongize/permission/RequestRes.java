package com.whu.eyerecongize.permission;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

public class RequestRes {
        private static RequestRes requestPermissionsResult;
        public static  RequestRes getInstance(){
            if(requestPermissionsResult == null){
                requestPermissionsResult = new RequestRes();
            }
            return requestPermissionsResult;
        }

        public boolean doRequestPermissionsResult(Activity activity, @NonNull String[] permissions, @NonNull int[] grantResults) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            //已全部授权
            if (isAllGranted) {
                return true;
            }
            else {
                //什么也不做
            }
            return false;
        }
    }

