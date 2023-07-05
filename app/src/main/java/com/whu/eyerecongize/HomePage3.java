package com.whu.eyerecongize;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.whu.eyerecongize.bilnk.BlinkType;
import com.whu.eyerecongize.bilnk.PageDecode;
import com.whu.eyerecongize.camera.CameraConfiguration;
import com.whu.eyerecongize.camera.LensEngine;
import com.whu.eyerecongize.camera.LensEnginePreview;
import com.whu.eyerecongize.connect.MessageTypes;
import com.whu.eyerecongize.connect.NetThread;
import com.whu.eyerecongize.transactor.LocalFaceTransactor;
import com.whu.eyerecongize.views.BarButton;
import com.whu.eyerecongize.views.BigButton;
import com.whu.eyerecongize.views.ImageEditText;
import com.whu.eyerecongize.views.LongButton;
import com.whu.eyerecongize.views.ReceiveMesDialog;
import com.whu.eyerecongize.views.overlay.GraphicOverlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import NetService.ConnectionUtils.ChatMessage;
import NetService.ConnectionUtils.ConnectionManager;
import NetService.ConnectionUtils.PageObserver;

public class HomePage3 extends AppCompatActivity implements PageObserver {

    private static final String TAG = "FaceDetectionActivity";
    private static final String OPEN_STATUS = "open_status";
    private LensEngine lensEngine = null;
    private LensEnginePreview preview;
    private GraphicOverlay graphicOverlay;

    private CameraConfiguration cameraConfiguration = null;
    private int facing = CameraConfiguration.CAMERA_FACING_FRONT;
    private Camera mCamera;


    private BroadcastReceiver myReceiver;

    private LocalBroadcastManager broadcastManager;



    private PageDecode decoder;//译码

    ArrayList<LongButton> longButtons;//从上到下

    ArrayList<BarButton>barButtons;


    ImageView tip;

    ImageView lock;

    TextView times;

    ImageEditText edit;

    ImageView next;

    ImageView last;

    ArrayList<String>buttonText;
    ArrayList<Integer>buttonIcon;//维护两个数组，用于翻页时动态生成图标，后期应当从数据库中取

    Integer defaultIcon;//默认图标

    int totalPages;//用于计算可供翻页的数目
    int nowPages;//当前页码，从1开始

    //通信相关
    private ConnectionManager connectionManager;
    Handler handler;

    private boolean handlerEnabled = false;

    private NetThread netThread;

    ReceiveMesDialog dialog;//接收消息弹窗相关
    boolean isReceive;

    Stack<ReceiveMesDialog> dialogs;//消息栈，处理多个消息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page3);
        setStatusBar();

        //注册通信对象
        netThread = ((EyeconizeApplication)getApplication()).getNetThread();
        connectionManager = ConnectionManager.getInstance();
        connectionManager.registerPageObserver(this);
        //注册handler处理接收消息
        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message message) {
                System.out.println("Message received.");
                switch (message.what) {
                    case MessageTypes.HANDLER_NEW_MESSAGE:
                        ChatMessage cm = (ChatMessage) message.obj;
                        if(cm.isQuestion){
                            Intent intent = new Intent(HomePage3.this, getMessage.class);

                            intent.putExtra("ID", cm.senderID);
                            intent.putExtra("content", cm.messageContent);

                            startActivity(intent);
                        }else{
                            dialog =new ReceiveMesDialog(HomePage3.this, R.style.MyDialogStyle,cm.messageContent,cm.senderID);
                            WindowManager.LayoutParams localLayoutParams = dialog.getWindow().getAttributes();
                            localLayoutParams.gravity = Gravity.LEFT|Gravity.TOP;
                            localLayoutParams.x = 100;
                            localLayoutParams.y=  10;
                            dialog.getWindow().setAttributes(localLayoutParams);
                            dialogs.push(dialog);
                            dialog.show();
                            isReceive=true;
                        }
                    default:
                        break;

                }
            }
        };

        isReceive=false;
        dialogs=new Stack<ReceiveMesDialog>();

        BarButton br1=findViewById(R.id.barButton31);
        BarButton br2=findViewById(R.id.barButton32);
        BarButton br3=findViewById(R.id.barButton33);

        LongButton lb1=findViewById(R.id.longButton41);
        LongButton lb2=findViewById(R.id.longButton42);
        LongButton lb3=findViewById(R.id.longButton43);
        LongButton lb4=findViewById(R.id.longButton44);

        tip=findViewById(R.id.imageView3helper);
        lock=findViewById(R.id.imageView3block);
        times=findViewById(R.id.textView3time);
        edit=findViewById(R.id.imageEditText31);
        next=findViewById(R.id.imageView3next);
        last=findViewById(R.id.imageView3last);


        longButtons=new ArrayList<LongButton>();
        barButtons=new ArrayList<BarButton>();

        barButtons.add(br1);
        barButtons.add(br2);
        barButtons.add(br3);

        longButtons.add(lb1);
        longButtons.add(lb2);
        longButtons.add(lb3);
        longButtons.add(lb4);

        decoder=new PageDecode(edit,this,null,barButtons,longButtons,null,tip,null,null);

        initialButtonList();

        //初始化
        this.preview = this.findViewById(R.id.face_preview);
        this.graphicOverlay = this.findViewById(R.id.face_overlay);


        //相机配置对象
        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(this.facing);
        this.createLensEngine();
        this.setStatusBar();

        setTime();
    }

    @Override
    public void newMessageAlert(ChatMessage chatMessage) {
        Message message = handler.obtainMessage(MessageTypes.HANDLER_NEW_MESSAGE, chatMessage);
        handler.sendMessage(message);
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

    private void setTime(){
        long sysTime = System.currentTimeMillis();
        CharSequence sysTimeStr = DateFormat.format("HH:mm", sysTime);//时间显示格式
        times.setText(sysTimeStr);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BlinkType enumValue = (BlinkType) intent.getSerializableExtra("newCode");
                //译码逻辑
                int index=decoder.parse(enumValue,3,totalPages,isReceive);//最后一页是多少需要通过数据计算
                setTime();
                changePage(index);
            }
        };
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(myReceiver, new IntentFilter("code"));

        decoder.unRegis();
    }

    private void setAnalyzer() {
        int featureType = MLFaceAnalyzerSetting.TYPE_UNSUPPORT_FEATURES;
        int pointsType = MLFaceAnalyzerSetting.TYPE_UNSUPPORT_KEYPOINTS;
        int shapeType = MLFaceAnalyzerSetting.TYPE_SHAPES;


        //配置设置
        MLFaceAnalyzerSetting mlFaceAnalyzerSetting = new MLFaceAnalyzerSetting.Factory()
                .setPerformanceType(MLFaceAnalyzerSetting.TYPE_SPEED)
                .setFeatureType(featureType)
                .setKeyPointType(pointsType)
                .setShapeType(shapeType)
                .setPoseDisabled(false)
                .create();

        //创建引擎
        this.lensEngine.setMachineLearningFrameTransactor(new LocalFaceTransactor(mlFaceAnalyzerSetting, getApplicationContext()));
    }


    private void initialButtonList(){
        buttonIcon=new ArrayList<Integer>();
        buttonText=new ArrayList<String>();

        buttonText.add("选择联系人");
        buttonText.add("播放声音");
        buttonText.add("主动问询");
        buttonText.add("撰写文字信息");
        buttonText.add("退出");

        buttonIcon.add(R.drawable.connect);
        buttonIcon.add(R.drawable.sound);
        buttonIcon.add(R.drawable.ask);
        buttonIcon.add(R.drawable.write);
        buttonIcon.add(R.drawable.exit);

        defaultIcon=R.drawable.defau;

        totalPages=(buttonText.size()+3)/4;
        nowPages=1;

    }

    private void nextPage(){
        LongButton tmp;
        nowPages++;
        for(int i=0;i<4;i++){
            int index=i+4*(nowPages-1);
            tmp=longButtons.get(i);
            if(index<buttonIcon.size()){
                tmp.setIcon(buttonIcon.get(index));
                tmp.setTextContent(buttonText.get(index));
            }
            else{
                tmp.setIcon(defaultIcon);
                tmp.setTextContent("");
            }
            tmp.invalidate();
        }
    }

    private void lastPage(){
        LongButton tmp;
        nowPages--;
        for(int i=0;i<4;i++){
            int index=i+4*(nowPages-1);
            tmp=longButtons.get(i);
            if(index<buttonIcon.size()){
                tmp.setIcon(buttonIcon.get(index));
                tmp.setTextContent(buttonText.get(index));
            }
            else{
                tmp.setIcon(defaultIcon);
                tmp.setTextContent("");
            }
            tmp.invalidate();
        }
    }

    private void createLensEngine() {
        if (this.lensEngine == null) {
            this.lensEngine = new LensEngine(this, this.cameraConfiguration, this.graphicOverlay);
        }
        try {
            setAnalyzer();
        } catch (Exception e) {
            Log.e(TAG, "createLensEngine IOException." + e.getMessage());
        }
    }

    private void reStartLensEngine() {
        this.startLensEngine();
        if (null != this.lensEngine) {
            this.mCamera = this.lensEngine.getCamera();
            try {
                this.mCamera.setPreviewTexture(this.preview.getSurfaceTexture());
            } catch (IOException e) {
                Log.e(TAG, "initViews IOException." + e.getMessage());
            }
        }
    }

    private void startLensEngine() {
        if (this.lensEngine != null) {
            try {
                this.preview.start(this.lensEngine, true);
            } catch (IOException e) {
                Log.e(HomePage3.TAG, "Unable to start lensEngine.", e);
                this.lensEngine.release();
                this.lensEngine = null;
            }
        }
    }

    private void changePage(int index){
        Intent intent;
        switch (index){//4,5,6
            case 1:
                lastPage();
                break;
            case 2:
                nextPage();
                break;
            case 4:
                intent = new Intent(this, HomePage1.class);
                startActivity(intent);
                break;
            case 5:
                intent = new Intent(this, HomePage2.class);
                startActivity(intent);
                break;
            case 6:
                intent = new Intent(this, HomePage3.class);
                startActivity(intent);
                break;
            case 8:
                if(!dialogs.empty()){
                    dialog=dialogs.pop();
                    dialog.changeStatus();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    }, 2000); // 延时2秒关闭弹窗
                }
                if(dialogs.empty()){
                    isReceive=false;
                }
                break;


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.startLensEngine();
        connectionManager.registerPageObserver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.preview.stop();
        broadcastManager.unregisterReceiver(myReceiver);
        connectionManager.unregisterPageObserver(this);

        decoder.unRegis();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releaseLensEngine();
        connectionManager.unregisterPageObserver(this);
    }

    private void releaseLensEngine() {
        if (this.lensEngine != null) {
            this.lensEngine.release();
            this.lensEngine = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseLensEngine();
        connectionManager.unregisterPageObserver(this);
    }
}