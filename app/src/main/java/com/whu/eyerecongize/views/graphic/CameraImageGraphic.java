

package com.whu.eyerecongize.views.graphic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.whu.eyerecongize.views.overlay.GraphicOverlay;


public class CameraImageGraphic extends BaseGraphic {

    private final Bitmap bitmap;

    private boolean isFill = true;

    public CameraImageGraphic(GraphicOverlay overlay, Bitmap bitmap) {
        super(overlay);
        this.bitmap = bitmap;
    }

    public CameraImageGraphic(GraphicOverlay overlay, Bitmap bitmap, boolean isFill) {
        super(overlay);
        this.bitmap = bitmap;
        this.isFill = isFill;
    }

    @Override
    public void draw(Canvas canvas) {
        int width;
        int height;
        if (this.isFill) {
            width = canvas.getWidth();
            height = canvas.getHeight();
        } else {
            width = this.bitmap.getWidth();
            height = this.bitmap.getHeight();
        }
        Log.d("toby","Total HMSFaceProc drawBitmap start");
        canvas.drawBitmap(this.bitmap, null, new Rect(0, 0, width, height), null);
        Log.d("toby","Total HMSFaceProc drawBitmap end");
    }
}

