

package com.whu.eyerecongize.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;


import com.whu.eyerecongize.callback.ImageUtilCallBack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private  Context context;
    private ImageUtilCallBack imageUtilCallBack;

    public ImageUtils(Context context){
        this.context = context;
    }

    public void setImageUtilCallBack(ImageUtilCallBack imageUtilCallBack){
        this.imageUtilCallBack = imageUtilCallBack;
    }

    // Save the picture to the system album and refresh it.
    public void saveToAlbum(Bitmap bitmap){
        File file = null;
        String fileName = System.currentTimeMillis() +".jpg";
        File root = new File(Environment.getExternalStorageDirectory().getAbsoluteFile(), this.context.getPackageName());
        File dir = new File(root, "image");
        if(dir.mkdirs() || dir.isDirectory()){
            file = new File(dir, fileName);
        }
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();

        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }finally {
            try {
                if(os != null) {
                    os.close();
                }
            }catch (IOException e){
                Log.e(TAG, e.getMessage());
            }
        }
        if(file == null){
            return;
        }
        if(imageUtilCallBack != null) {
            try {
                imageUtilCallBack.callSavePath(file.getCanonicalPath());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        // Gallery refresh.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String path = null;
            try {
                path = file.getCanonicalPath();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            MediaScannerConnection.scanFile(this.context, new String[]{path}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            mediaScanIntent.setData(uri);
                            ImageUtils.this.context.sendBroadcast(mediaScanIntent);
                        }
                    });
        } else {
            String relationDir = file.getParent();
            File file1 = new File(relationDir);
            this.context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
        }
    }

    /**
     * Compare the size of the two pictures.
     *
     * @param foregroundBitmap the first bitmap
     * @param backgroundBitmap the second bitmap
     * @return true: same size; false: not.
     */
    public static boolean equalImageSize(Bitmap foregroundBitmap, Bitmap backgroundBitmap) {
        return backgroundBitmap.getHeight() == foregroundBitmap.getHeight() && backgroundBitmap.getWidth() == foregroundBitmap.getWidth();
    }

    /**
     * Scale background (background picture) size to foreground (foreground picture) size.
     *
     * @param foregroundBitmap foreground picture
     * @param backgroundBitmap background picture
     * @return A background image that is the same size as the foreground image.
     */
    public static Bitmap resizeImageToForegroundImage(Bitmap foregroundBitmap, Bitmap backgroundBitmap) {
        float scaleWidth = ((float) foregroundBitmap.getWidth() / backgroundBitmap.getWidth());
        float scaleHeight = ((float) foregroundBitmap.getHeight() / backgroundBitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        backgroundBitmap = Bitmap.createBitmap(backgroundBitmap, 0, 0, backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), matrix, true);
        return backgroundBitmap;
    }
}
