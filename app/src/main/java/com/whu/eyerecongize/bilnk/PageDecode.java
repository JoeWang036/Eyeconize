package com.whu.eyerecongize.bilnk;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.whu.eyerecongize.R;
import com.whu.eyerecongize.views.BarButton;
import com.whu.eyerecongize.views.BigButton;
import com.whu.eyerecongize.views.ImageEditText;
import com.whu.eyerecongize.views.LongButton;
import com.whu.eyerecongize.views.MessageDialog;
import com.whu.eyerecongize.views.MyDialog;
import com.whu.eyerecongize.views.changeDialog;

import java.util.ArrayList;

public class PageDecode {
    ArrayList<BlinkType>code;

    String codeString="";
    private long lastTime;

    boolean spaceStatus;//记录当前有无空格

    boolean lockStatus;

    ImageEditText edit;

    Context context;

    //维护对activity组件的引用，便于修改其值
    ArrayList<BigButton>bigButtons=null;//从左到右，从上到下

    ArrayList<BarButton>barButtons=null;

    ArrayList<LongButton>longButtons=null;

    LongButton helpButton=null;

    ImageView tip=null;

    BigButton tmp;//用来取按钮

    LongButton longtmp;//用来取按钮

    BarButton bartmp;//用来取按钮

    int mode;//表示译码状态，1代表当前为正常，2代表当前译码空调，3代表当前译码灯光，后续可添加

    changeDialog mdialog;//选择弹窗，设为成员变量便于切换状态

    int page3NowIndex=1;//在其他页面记录当前页数，便于进行翻页

    //针对信息页面设置的成员变量
    ImageView yes;
    ImageView no;




    public PageDecode(ImageEditText edit,Context context,ArrayList<BigButton>bigButtons,
                      ArrayList<BarButton>barButtons,ArrayList<LongButton>longButtons, LongButton helpButton,
                      ImageView tip,ImageView yes,ImageView no){
        lastTime=0;
        code=new ArrayList<BlinkType>();
        this.edit=edit;
        boolean spaceStatus=false;
        boolean lockStatus=false;
        this.context=context;

        if(bigButtons!=null)this.bigButtons=bigButtons;
        if(barButtons!=null)this.barButtons=barButtons;
        if(longButtons!=null)this.longButtons=longButtons;
        if(helpButton!=null)this.helpButton=helpButton;
        if(tip!=null)this.tip=tip;

        mode=1;

        this.yes=yes;
        this.no=no;

    }

    public int parse(BlinkType myEnum,int nowpage,int lastpage4Three){
        try {
            int tmp=0;
            //BlinkType myEnum = BlinkType.valueOf(message);
            manageEdit(myEnum);
            code.add(myEnum);
            switch (myEnum){
                case LONG:codeString+=1;
                break;
                case SHORT:codeString+=0;
                break;
                case DELETE:codeString="";
                code.clear();
                lockStatus=false;
                break;
                case SPACE:tmp=decode4All(nowpage,lastpage4Three);
                //System.out.println(tmp);
                if(!codeString.equals("")){
                    codeString="";
                    code.clear();
                }
                break;
            }
            return tmp;

        } catch (IllegalArgumentException e) {
            return 0;
        }
    }

//    public void calculateTime(){//计算空格与删除
//        if(lastTime==0){
//            lastTime=System.currentTimeMillis();
//            return;
//        }
//
//        long timePass=System.currentTimeMillis()-lastTime;
//        if(timePass>700&&!spaceStatus){
//            code.add(BlinkType.SPACE);
//            codeString+=" ";
//            spaceStatus=true;
//        }
//
//        if(timePass>10000&&!lockStatus){
//            codeString="";
//            code.clear();
//            spaceStatus=false;
//            lockStatus=true;
//        }
//
//        lastTime=System.currentTimeMillis();
//
//    }

    public void messageDialogHandler(Context context,String content,boolean status){
        MyDialog dialog =new MyDialog(context, R.style.MyDialogStyle,content,status);
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        }, 1000);
    }

    public void choiceDialogHandler(boolean choice){
        mdialog =new changeDialog(context,R.style.selectDialogStyle,choice);


        WindowManager.LayoutParams localLayoutParams = mdialog.getWindow().getAttributes();

        localLayoutParams.gravity = Gravity.LEFT|Gravity.TOP;

        localLayoutParams.x = 100;
        localLayoutParams.y=  10;
        mdialog.getWindow().setAttributes(localLayoutParams);

        mdialog.show();
    }

    public void choiceDialogDisappear(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mdialog.isShowing()) {
                    mdialog.dismiss();
                }
            }
        }, 2000); // 延时3秒关闭弹窗
    }

    public void mindDialogHandler(boolean status){
        MessageDialog mesdialog =new MessageDialog(context, R.style.MyDialogStyle,status);
        mesdialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mesdialog.isShowing()) {
                    mesdialog.dismiss();
                }
            }
        }, 2000); // 延时3秒关闭弹窗
    }

    public void buttonHandler(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tmp.setRec(false);
                tmp.invalidate();
            }
        }, 1000);
    }

    public void barButtonHandler(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bartmp.setRec(false);
                bartmp.invalidate();
            }
        }, 1000);
    }

    public void longButtonHandler(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                longtmp.setRec(false);
                longtmp.invalidate();
            }
        }, 1000);
    }

    public void clear(){
        codeString="";
        code.clear();
        edit.clear();
    }

    public void page1Decode(){
        boolean status=true;//代表通信结果成功与否
        //101heshui 011yinshi 100dabian 110xiaobian
        if(codeString.equals("101")){//喝水
            tmp=bigButtons.get(0);
            tmp.setRec(true);
            tmp.invalidate();
            buttonHandler();
            //通讯逻辑

            messageDialogHandler(context,"我需要喝水",status);
            clear();

        }

        if(codeString.equals("011")){//饮食
            tmp=bigButtons.get(1);
            tmp.setRec(true);
            tmp.invalidate();
            buttonHandler();

            messageDialogHandler(context,"我现在有些饿",status);
            clear();
        }

        if(codeString.equals("100")){//大便
            tmp=bigButtons.get(2);
            tmp.setRec(true);
            tmp.invalidate();
            buttonHandler();

            messageDialogHandler(context,"我需要大便",status);
            clear();
        }

        if(codeString.equals("110")){//小便
            tmp=bigButtons.get(3);
            tmp.setRec(true);
            tmp.invalidate();
            buttonHandler();

            messageDialogHandler(context,"我需要小便",status);
            clear();
        }

        if(codeString.equals("111")){
            helpButton.setRec(true);
            helpButton.invalidate();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    helpButton.setRec(false);
                    helpButton.invalidate();
                }
            }, 1000);

            messageDialogHandler(context,"我需要紧急帮助！",status);
            clear();
        }

    }

    public void page2Decode() {
        if (mode == 1) {
            boolean status = true;//表示通信结果，不能为定值
            if (codeString.equals("101")) {//召唤
                tmp = bigButtons.get(0);
                tmp.setRec(true);
                tmp.invalidate();
                buttonHandler();
                //通讯逻辑

                messageDialogHandler(context, "请马上过来", status);
                clear();

            }

            if (codeString.equals("011")) {//心情
                tmp = bigButtons.get(1);
                tmp.setRec(true);
                tmp.invalidate();
                buttonHandler();
                //后续增加页面跳转逻辑

                clear();
            }

            if (codeString.equals("100")) {//小说
                tmp = bigButtons.get(2);
                tmp.setRec(true);
                tmp.invalidate();
                buttonHandler();
                //后续增加页面跳转逻辑

                clear();

            }

            if (codeString.equals("110")) {//音乐
                tmp = bigButtons.get(3);
                tmp.setRec(true);
                tmp.invalidate();
                buttonHandler();
                //后续增加页面跳转逻辑

                clear();

            }

            if (codeString.equals("111")) {//空调
                tmp = bigButtons.get(4);
                tmp.setRec(true);
                tmp.invalidate();
                buttonHandler();
                choiceDialogHandler(true);
                mode = 2;
                clear();

            }

            if (codeString.equals("010")) {//灯光
                tmp = bigButtons.get(5);
                tmp.setRec(true);
                tmp.invalidate();
                buttonHandler();
                choiceDialogHandler(false);
                mode = 3;
                clear();
            }

        }
        if(mode == 2){//空调译码
            if(codeString.equals("000")){
                mdialog.changeStatus(true);
                //空调相关逻辑

                clear();
                choiceDialogDisappear();
                mode=1;
            }
            if(codeString.equals("010")){
                mdialog.changeStatus(false);
                //空调相关逻辑

                clear();
                choiceDialogDisappear();
                mode=1;
            }
        }
        if(mode == 3){//灯光译码
            if(codeString.equals("000")){
                mdialog.changeStatus(true);
                //灯光相关逻辑

                clear();
                choiceDialogDisappear();
                mode=1;
            }
            if(codeString.equals("010")){
                mdialog.changeStatus(false);
                //灯光相关逻辑

                clear();
                choiceDialogDisappear();
                mode=1;
            }
        }
    }

    public int page3Decode(int indexNow,int last){//返回是否翻页的信息，为0是不翻页，1为上一页，2为下一页
        if(codeString.equals("0001")){//上一页
            if(indexNow==1){
                mindDialogHandler(true);
                clear();
                return 0;
            }
            clear();
            page3NowIndex--;
            return 1;
        }
        if(codeString.equals("010")){
            if(indexNow==last){
                mindDialogHandler(false);
                clear();
                return 0;
            }
            page3NowIndex++;
            clear();
            return 2;
        }

        if(codeString.equals("100")){
            longtmp=longButtons.get(0);
            longtmp.setRec(true);
            longtmp.invalidate();
            longButtonHandler();
            clear();
            //可在此添加switch实现不同页面不同操作，下方类似

        }
        if(codeString.equals("110")){
            longtmp=longButtons.get(1);
            longtmp.setRec(true);
            longtmp.invalidate();
            longButtonHandler();
            clear();
        }
        if(codeString.equals("101")){
            longtmp=longButtons.get(2);
            longtmp.setRec(true);
            longtmp.invalidate();
            longButtonHandler();
            clear();
        }
        if(codeString.equals("011")){
            longtmp=longButtons.get(3);
            longtmp.setRec(true);
            longtmp.invalidate();
            longButtonHandler();
            clear();
        }

        return 0;

    }

    public int pageMesDecode(){
        boolean status = true;//表示通信结果，不能为定值
        if(codeString.equals("000")){
            yes.setImageResource(R.drawable.yesactive);
            messageDialogHandler(context, "好的", status);//此处发送消息


            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    yes.setImageResource(R.drawable.yes);
                }
            }, 1000);
            clear();
            return 7;
        }

        if(codeString.equals("010")){
            no.setImageResource(R.drawable.noactive);
            messageDialogHandler(context, "不用", status);//此处发送消息


            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    no.setImageResource(R.drawable.no);
                }
            }, 1000);
            clear();
            return 7;
        }

        return 0;
    }

    public int decode4All(int nowPage,int lastPage4Three){//4表示去常用，5表示工具，6表示其他,7针对消息页面，表示退出消息页面
        if(nowPage==4){
            return pageMesDecode();
        }


        if(codeString.equals("0010")){
            if(nowPage!=1){
                bartmp=barButtons.get(0);
                bartmp.setRec(true);
                bartmp.invalidate();
                barButtonHandler();
                clear();
                return 4;
            }else{
                bartmp=barButtons.get(0);
                bartmp.setRec(true);
                bartmp.invalidate();
                barButtonHandler();
                clear();
                return 0;
            }
        }
        if(codeString.equals("0101")){
            if(nowPage!=2){
                bartmp=barButtons.get(1);
                bartmp.setRec(true);
                bartmp.invalidate();
                barButtonHandler();
                clear();
                return 5;
            }else{
                bartmp=barButtons.get(1);
                bartmp.setRec(true);
                bartmp.invalidate();
                barButtonHandler();
                clear();
                return 0;
            }

        }
        if(codeString.equals("0110")){
            if(nowPage!=3){
                bartmp=barButtons.get(2);
                bartmp.setRec(true);
                bartmp.invalidate();
                barButtonHandler();
                clear();
                return 6;
            }else{
                bartmp=barButtons.get(2);
                bartmp.setRec(true);
                bartmp.invalidate();
                barButtonHandler();
                clear();
                return 0;
            }

        }

        if(codeString.equals("1010")){
            tip.setImageResource(R.drawable.helper_active);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tip.setImageResource(R.drawable.helper_normal);
                }
            }, 1000);
            clear();
            return 0;
        }

        switch(nowPage){
            case 1:
                page1Decode();
                return 0;
            case 2:
                page2Decode();
                return 0;
            case 3:
                return page3Decode(page3NowIndex,lastPage4Three);
        }

        return 0;

    }

    public void manageEdit(BlinkType tmp){
        if(tmp.equals(BlinkType.DELETE)){
            edit.clear();
            return;
        }

        if(tmp.equals(BlinkType.SPACE)){
            if((edit.nowLenth+1)<=edit.maxLenth){
                edit.insertSpace();
                edit.nowLenth+=1;
            }
            else{
                clear();
            }
        }

        if(tmp.equals(BlinkType.SHORT)){
            if((edit.nowLenth+1)<=edit.maxLenth){
                edit.insertImage(edit.dot);
                edit.nowLenth+=1;
            }
            else{
                clear();
                edit.insertImage(edit.dot);
                edit.nowLenth+=1;
            }
        }

        if(tmp.equals(BlinkType.LONG)){
            if((edit.nowLenth+1.75)<=edit.maxLenth){
                edit.insertImage(edit.lon);
                edit.nowLenth+=1.75;
            }
            else{
                clear();
                edit.insertImage(edit.lon);
                edit.nowLenth+=1.75;
            }
        }

    }


}