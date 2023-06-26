package com.whu.eyerecongize.permission;

import android.app.Activity;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import java.util.List;

public class RequestPermit {
    private static RequestPermit requestPermissions;
    public static RequestPermit getInstance(){
        if(requestPermissions == null){
            requestPermissions = new RequestPermit();
        }
        return requestPermissions;
    }

    public boolean requestPermissions(Activity activity, String[] permissions, int resultCode) {
        //判断手机版本是否23以下，如果是，不需要使用动态权限
        if(Build.VERSION.SDK_INT < 23){
            return true;
        }

        //判断并请求权限
        return requestNeedPermission(activity,permissions,resultCode);
    }

    private boolean requestAllPermission(Activity activity, String[] permissions, int resultCode){
        //判断是否已赋予了全部权限
        boolean isAllGranted = CheckPermit.checkPermissionAllGranted(activity, permissions);
        if(isAllGranted){
            return true;
        }
        ActivityCompat.requestPermissions(activity, permissions, resultCode);
        return false;
    }

    private boolean requestNeedPermission(Activity activity, String[] permissions, int resultCode){
        List<String> list = CheckPermit.checkPermissionDenied(activity, permissions);
        if(list.size() == 0){
            return true;
        }

        //请求权限
        String[] deniedPermissions = list.toArray(new String[list.size()]);
        ActivityCompat.requestPermissions(activity, deniedPermissions, resultCode);
        return false;
    }
}
