

package com.whu.eyerecongize.transactor;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.face.MLFace;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer;
import com.huawei.hms.mlsdk.face.MLFaceAnalyzerSetting;
import com.whu.eyerecongize.bilnk.Decode;
import com.whu.eyerecongize.camera.FrameMetadata;
import com.whu.eyerecongize.views.graphic.CameraImageGraphic;
import com.whu.eyerecongize.views.graphic.LocalFaceGraphic;
import com.whu.eyerecongize.views.overlay.GraphicOverlay;


import java.io.IOException;
import java.util.List;

public class LocalFaceTransactor extends BaseTransactor<List<MLFace>> {
    private static final String TAG = "LocalFaceTransactor";

    private final MLFaceAnalyzer detector;
    private boolean isOpenFeatures;
    private Context mContext;
    private boolean isOpenDots;

    private Decode decode;

    public LocalFaceTransactor(MLFaceAnalyzerSetting options, Context context) {
        super(context);
        this.detector = MLAnalyzerFactory.getInstance().getFaceAnalyzer(options);
        this.isOpenFeatures = isOpenFeatures;
        this.mContext = context;
        this.isOpenDots = isOpenDots;
        this.decode=new Decode();
    }

    @Override
    public void stop() {
        try {
            this.detector.stop();
        } catch (IOException e) {
            Log.e(LocalFaceTransactor.TAG, "Exception thrown while trying to close face transactor: " + e.getMessage());
        }
    }

    @Override
    protected Task<List<MLFace>> detectInImage(MLFrame image) {
        return this.detector.asyncAnalyseFrame(image);
    }

    @Override
    protected void onSuccess(
            @Nullable Bitmap originalCameraImage,
            @NonNull List<MLFace> faces,
            @NonNull FrameMetadata frameMetadata,
            @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();
        Log.d("toby", "Total HMSFaceProc graphicOverlay start");
        if (originalCameraImage != null) {
            CameraImageGraphic imageGraphic = new CameraImageGraphic(graphicOverlay, originalCameraImage);
            graphicOverlay.addGraphic(imageGraphic);
        }
        Log.d("toby", "Total HMSFaceProc hmsMLLocalFaceGraphic start");
        LocalFaceGraphic hmsMLLocalFaceGraphic = new LocalFaceGraphic(graphicOverlay, faces, mContext);

        decode.blinksDetectors(faces,mContext);

        graphicOverlay.addGraphic(hmsMLLocalFaceGraphic);
        graphicOverlay.postInvalidate();
        Log.d("toby", "Total HMSFaceProc graphicOverlay end");
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.d("toby", "Total HMSFaceProc graphicOverlay onFailure");
        Log.e(LocalFaceTransactor.TAG, "Face detection failed: " + e.getMessage());
    }

    @Override
    public boolean isFaceDetection() {
        return true;
    }
}
