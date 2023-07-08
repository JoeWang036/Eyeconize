package com.whu.eyerecongize;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaPlayer;
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

public class HomePage2 extends AppCompatActivity implements PageObserver {


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

    ArrayList<BigButton> bigButtons;//从左到右，从上到下

    ArrayList<BarButton>barButtons;


    ImageView tip;

    ImageView lock;

    TextView times;

    ImageEditText edit;

    //通信相关
    private ConnectionManager connectionManager;
    Handler handler;

    private boolean handlerEnabled = false;

    private NetThread netThread;

    ReceiveMesDialog dialog;//接收消息弹窗相关
    boolean isReceive;

    Stack<ReceiveMesDialog> dialogs;//消息栈，处理多个消息

    //音频播放
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page2);
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
                            Intent intent = new Intent(HomePage2.this, getMessage.class);

                            intent.putExtra("ID", cm.senderID);
                            intent.putExtra("content", cm.messageContent);

                            startActivity(intent);
                        }else{
                            dialog =new ReceiveMesDialog(HomePage2.this, R.style.MyDialogStyle,cm.messageContent,cm.senderID);
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



        BigButton bg1=findViewById(R.id.bigButton21);
        BigButton bg2=findViewById(R.id.bigButton22);
        BigButton bg3=findViewById(R.id.bigButton23);
        BigButton bg4=findViewById(R.id.bigButton24);
        BigButton bg5=findViewById(R.id.bigButton25);
        BigButton bg6=findViewById(R.id.bigButton26);

        BarButton br1=findViewById(R.id.barButton21);
        BarButton br2=findViewById(R.id.barButton22);
        BarButton br3=findViewById(R.id.barButton23);

        tip=findViewById(R.id.imageView2helper);
        lock=findViewById(R.id.imageView2block);
        times=findViewById(R.id.textView2time);
        edit=findViewById(R.id.imageEditText21);


        bigButtons=new ArrayList<BigButton>();
        barButtons=new ArrayList<BarButton>();

        bigButtons.add(bg1);
        bigButtons.add(bg2);
        bigButtons.add(bg3);
        bigButtons.add(bg4);
        bigButtons.add(bg5);
        bigButtons.add(bg6);


        barButtons.add(br1);
        barButtons.add(br2);
        barButtons.add(br3);

        decoder=new PageDecode(edit,this,bigButtons,barButtons,null,null,tip,null,null);


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

    private void setTime(){
        long sysTime = System.currentTimeMillis();
        CharSequence sysTimeStr = DateFormat.format("HH:mm", sysTime);//时间显示格式
        times.setText(sysTimeStr);
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

    @Override
    protected void onStart() {
        super.onStart();
        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                BlinkType enumValue = (BlinkType) intent.getSerializableExtra("newCode");
                //译码逻辑
                int index=decoder.parse(enumValue,2,0,isReceive);
                setTime();
                changePage(index);
                playMedia(index);
            }
        };
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(myReceiver, new IntentFilter("code"));

        decoder.Regius();
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
                Log.e(HomePage2.TAG, "Unable to start lensEngine.", e);
                this.lensEngine.release();
                this.lensEngine = null;
            }
        }
    }

    private void changePage(int index){
        Intent intent;
        switch (index){//4,5,6
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

        if(mediaPlayer!=null){
        mediaPlayer.release();}

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

    @Override
    protected void onPause() {
        super.onPause();
        if(mediaPlayer!=null){
        mediaPlayer.stop();
        }
    }
    private void playMedia(int index){
        if(index==9)
        {
            System.out.println("bofang");
            mediaPlayer = MediaPlayer.create(this, R.raw.tools);
            mediaPlayer.start();
        }
    }
}