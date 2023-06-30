package com.codejustice.global;

import android.os.Handler;
import android.os.Looper;

public class Global {
    private static Global instance;
    public static long selfID = 123456;

    private Handler handler;


    private Global() {
        handler = new Handler(Looper.getMainLooper());

    }

    public static Global getInstance() {
        if (instance == null) {
            synchronized (Global.class) {
                if (instance == null) {
                    instance = new Global();
                }
            }
        }
        return instance;
    }

    // 其他成员方法...

}
