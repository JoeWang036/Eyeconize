package com.whu.eyerecongize.bilnk;

import android.content.Context;
import android.content.Intent;
import android.os.Message;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.huawei.hms.mlsdk.face.MLFace;

import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

public class Decode {
    private static String tmpText="";
    private static String current_code = "";
    private static String former_code = "";
    private static String output = "";


    private static final int EYE_AR_CONSEC_TIMES = 50;//闭眼门限
    private static final int EYE_OPEN_CONSEC_TIMES = 50;//睁眼门限

    private static final int DIV_THRESH = 700;//空格门限
    private static final int SHORT_THRESH =160;//短眨眼门限
    private static final int LONG_THRESH = 500;//长眨眼门限



    private static int STATUS = 0;


    private boolean lastStatus;
    private long lastTime=0;

    private long totalOpenTime=0;
    private long totalCloseTime=0;

    boolean isParse=true;//空格标记符



    public void blinksDetectors(List<MLFace> faces, Context mt) {

        boolean blink=BlinkCheck.checkBlink(faces);

        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(mt);
        Intent intent = new Intent("code");


        if(totalOpenTime>700&&!isParse){
            intent.putExtra("newCode", BlinkType.SPACE);
            broadcastManager.sendBroadcast(intent);
            isParse=true;
        }

        if(lastTime==0){//第一次取样，直接退出
            lastTime=System.currentTimeMillis();
            lastStatus=blink;
            return;
        }

        long nowTime=System.currentTimeMillis();
        long timePass=0;

        if (blink) {//确定闭眼

            if(lastStatus!=blink){//计算时间差
                timePass=(nowTime-lastTime)/2;
                lastTime=nowTime;
            }else{
                timePass=nowTime-lastTime;
                lastTime=nowTime;
                //System.out.println("间隔时间"+timePass);
            }

            totalCloseTime+=timePass;//总闭眼时间

            if(totalCloseTime>=EYE_AR_CONSEC_TIMES){//大于闭眼门限
                if (STATUS == 1) {
                    STATUS = 0;
                }
                totalOpenTime = 0;
            }

        } else {//睁眼

            if(lastStatus!=blink){
                timePass=(nowTime-lastTime)/2;
                lastTime=nowTime;
            }else{
                timePass=nowTime-lastTime;
                lastTime=nowTime;
                //System.out.println("间隔时间"+timePass);
            }

            totalOpenTime+=timePass;

            if (totalOpenTime>= EYE_OPEN_CONSEC_TIMES) {
                if (STATUS == 0) {
                    //TOTAL++;
                    if (totalCloseTime < SHORT_THRESH) {
                        intent.putExtra("newCode", BlinkType.SHORT);
                        broadcastManager.sendBroadcast(intent);
                        isParse=false;
                        STATUS = 1;
                        totalCloseTime = 0;
                        return;
                    } else {
                        if (totalCloseTime < LONG_THRESH) {
                            intent.putExtra("newCode", BlinkType.LONG);
                            broadcastManager.sendBroadcast(intent);
                            isParse=false;
                            STATUS = 1;
                            totalCloseTime = 0;
                            return;
                        } else {
                            intent.putExtra("newCode", BlinkType.DELETE);
                            broadcastManager.sendBroadcast(intent);
                            isParse=false;
                            STATUS = 1;
                            totalCloseTime = 0;
                            return;
                        }
                    }
                }
                STATUS = 1;
                //System.out.println("闭眼时间："+totalCloseTime);
                totalCloseTime = 0;
            }
        }
    }
}
