

package com.whu.eyerecongize.views.graphic;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.huawei.hms.mlsdk.common.LensEngine;
import com.whu.eyerecongize.views.overlay.GraphicOverlay;


public abstract class BaseGraphic {
    private GraphicOverlay graphicOverlay;

    public BaseGraphic(GraphicOverlay overlay) {
        this.graphicOverlay = overlay;
    }

    /**
     * Draw of view
     *
     * @param canvas Canvas object
     */
    public abstract void draw(Canvas canvas);

    public float scaleX(float x) {
        return x * this.graphicOverlay.getWidthScaleValue();
    }

    public float unScaleX(float horizontal) {
        return horizontal / graphicOverlay.getWidthScaleValue();
    }

    public float scaleY(float y) {
        return y * this.graphicOverlay.getHeightScaleValue();
    }

    public float unScaleY(float vertical) {
        return vertical / graphicOverlay.getHeightScaleValue();
    }

    public Rect translateRect(Rect rect){
        float left = translateX(rect.left);
        float right = translateX(rect.right);
        float bottom = translateY(rect.bottom);
        float top = translateY(rect.top);

        if (left >right){
            float size = left;
            left = right;
            right = size;
        }
        if (bottom< top){
            float size = bottom;
            bottom = top;
            top = size;
        }
        
        return new Rect((int)left, (int)top, (int)right, (int)bottom);
    }

    public float translateX(float x) {
        if (this.graphicOverlay.getCameraFacing() == LensEngine.FRONT_LENS) {
            return this.graphicOverlay.getWidth() - this.scaleX(x);
        } else {
            return this.scaleX(x);
        }
    }

    public float translateY(float y) {
        return this.scaleY(y);
    }
}
