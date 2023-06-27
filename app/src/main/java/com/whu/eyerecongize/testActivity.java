package com.whu.eyerecongize;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.whu.eyerecongize.camera.CameraConfiguration;
import com.whu.eyerecongize.camera.LensEngine;
import com.whu.eyerecongize.camera.LensEnginePreview;
import com.whu.eyerecongize.transactor.LocalFaceTransactor;
import com.whu.eyerecongize.views.overlay.GraphicOverlay;

import java.io.IOException;

public class testActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "FaceDetectionActivity";
    private static final String OPEN_STATUS = "open_status";
    private LensEngine lensEngine = null;
    private LensEnginePreview preview;
    private GraphicOverlay graphicOverlay;

    private Button start;
    private CameraConfiguration cameraConfiguration = null;
    private int facing = CameraConfiguration.CAMERA_FACING_FRONT;
    private Camera mCamera;

    private TextView tet;

    private BroadcastReceiver myReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_test);


        //初始化
        this.preview = this.findViewById(R.id.face_preview);
        this.graphicOverlay = this.findViewById(R.id.face_overlay);
        this.tet=this.findViewById(R.id.textView);


        //相机配置对象
        this.cameraConfiguration = new CameraConfiguration();
        this.cameraConfiguration.setCameraFacing(this.facing);
        this.createLensEngine();
        this.setStatusBar();

        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String value = intent.getStringExtra("current_code");
                tet.setText(value);
            }
        };
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(myReceiver, new IntentFilter("code"));


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

    @Override
    public void onClick(View view) {//点击返回按钮释放引擎
            releaseLensEngine();
            this.finish();
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
                Log.e(testActivity.TAG, "Unable to start lensEngine.", e);
                this.lensEngine.release();
                this.lensEngine = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.startLensEngine();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.preview.stop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releaseLensEngine();
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
    }
}