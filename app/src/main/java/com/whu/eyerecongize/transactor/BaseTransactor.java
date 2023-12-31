

package com.whu.eyerecongize.transactor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.util.Log;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.whu.eyerecongize.camera.CameraConfiguration;
import com.whu.eyerecongize.camera.FrameMetadata;
import com.whu.eyerecongize.util.BitmapUtils;
import com.whu.eyerecongize.util.NV21ToBitmapConverter;
import com.whu.eyerecongize.views.overlay.GraphicOverlay;


import java.nio.ByteBuffer;

public abstract class BaseTransactor<T> implements ImageTransactor {
    private static final String TAG = "BaseTransactor";
    // To keep the latest images and its metadata.
    private ByteBuffer latestImage;

    private
    FrameMetadata latestImageMetaData;

    // To keep the images and metadata in process.
    private ByteBuffer transactingImage;

    private FrameMetadata transactingMetaData;

    private Context mContext;

    private NV21ToBitmapConverter converter = null;

    public BaseTransactor() {
    }

    public BaseTransactor(Context context) {
        this.mContext = context;
        this.converter = new NV21ToBitmapConverter(this.mContext);
    }

    @Override
    public synchronized void process(ByteBuffer data, final FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {
        this.latestImage = data;
        this.latestImageMetaData = frameMetadata;
        if (this.transactingImage == null && this.transactingMetaData == null) {
            this.processLatestImage(graphicOverlay);
        }
    }

    @Override
    public void process(Bitmap bitmap, GraphicOverlay graphicOverlay) {
        MLFrame frame = new MLFrame.Creator().setBitmap(bitmap).create();
        this.detectInVisionImage(bitmap, frame, null, graphicOverlay);
    }

    private synchronized void processLatestImage(GraphicOverlay graphicOverlay) {
        this.transactingImage = this.latestImage;
        this.transactingMetaData = this.latestImageMetaData;
        this.latestImage = null;
        this.latestImageMetaData = null;
        Bitmap bitmap = null;
        if (this.transactingImage != null && this.transactingMetaData != null) {
            int width;
            int height;
            width = this.transactingMetaData.getWidth();
            height = this.transactingMetaData.getHeight();
            MLFrame.Property metadata = new MLFrame.Property.Creator().setFormatType(ImageFormat.NV21)
                    .setWidth(width)
                    .setHeight(height)
                    .setQuadrant(this.transactingMetaData.getRotation())
                    .create();

            if (this.isFaceDetection()) {
                Log.d(TAG, "Total HMSFaceProc getBitmap start");
                bitmap = this.converter.getBitmap(this.transactingImage, this.transactingMetaData);
                Log.d(TAG, "Total HMSFaceProc getBitmap end");
                Bitmap resizeBitmap = BitmapUtils.scaleBitmap(bitmap, CameraConfiguration.DEFAULT_HEIGHT,
                        CameraConfiguration.DEFAULT_WIDTH);
                Log.d(TAG, "Total HMSFaceProc resizeBitmap end");
                this.detectInVisionImage(bitmap, MLFrame.fromBitmap(resizeBitmap), this.transactingMetaData,
                        graphicOverlay);
            } else {
                bitmap = BitmapUtils.getBitmap(this.transactingImage, this.transactingMetaData);
                this.detectInVisionImage(bitmap, MLFrame.fromByteBuffer(this.transactingImage, metadata),
                        this.transactingMetaData, graphicOverlay);
            }
        }
    }

    private void detectInVisionImage(final Bitmap bitmap, MLFrame image, final FrameMetadata metadata,
        final GraphicOverlay graphicOverlay) {
        this.detectInImage(image).addOnSuccessListener(new OnSuccessListener<T>() {
            @Override
            public void onSuccess(T results) {
                if (metadata == null || metadata.getCameraFacing() == CameraConfiguration.getCameraFacing()) {
                    BaseTransactor.this.onSuccess(bitmap, results, metadata, graphicOverlay);//此处添加眨眼检测

                }
                BaseTransactor.this.processLatestImage(graphicOverlay);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                BaseTransactor.this.onFailure(e);
            }
        });
    }

    @Override
    public void stop() {
    }

    /**
     * Detect image
     *
     * @param image MLFrame object
     * @return Task object
     */
    protected abstract Task<T> detectInImage(MLFrame image);

    /**
     * Callback that executes with a successful detection result.
     *
     * @param originalCameraImage hold the original image from camera, used to draw the background image.
     * @param results T object
     * @param frameMetadata FrameMetadata object
     * @param graphicOverlay GraphicOverlay object
     */

    protected abstract void onSuccess(
            Bitmap originalCameraImage,
            T results,
            FrameMetadata frameMetadata, GraphicOverlay graphicOverlay);

    /**
     * Callback that executes with failure detection result.
     *
     * @param exception Exception object
     */
    protected abstract void onFailure(Exception exception);

    @Override
    public boolean isFaceDetection() {
        return false;
    }
}
