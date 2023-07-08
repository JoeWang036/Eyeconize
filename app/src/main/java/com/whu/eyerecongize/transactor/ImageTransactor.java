

package com.whu.eyerecongize.transactor;

import android.graphics.Bitmap;


import com.whu.eyerecongize.camera.FrameMetadata;
import com.whu.eyerecongize.views.overlay.GraphicOverlay;

import java.nio.ByteBuffer;

public interface ImageTransactor {

    /**
     * Start detection
     *
     * @param data ByteBuffer object
     * @param frameMetadata FrameMetadata object
     * @param graphicOverlay GraphicOverlay object
     */
    void process(ByteBuffer data, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay);

    /**
     * Start detection
     *
     * @param bitmap Bitmap object
     * @param graphicOverlay GraphicOverlay object
     */
    void process(Bitmap bitmap, GraphicOverlay graphicOverlay);

    /**
     * Stop detection
     */
    void stop();

    /**
     * Is it face detection?
     *
     * @return boolean value
     */
    boolean isFaceDetection();
}
