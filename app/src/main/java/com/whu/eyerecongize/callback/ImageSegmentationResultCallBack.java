
package com.whu.eyerecongize.callback;

import android.graphics.Bitmap;

public interface ImageSegmentationResultCallBack {
    /**
     * Save bitmap
     *
     * @param bitmap bitmap
     */
    void callResultBitmap(Bitmap bitmap);
}
