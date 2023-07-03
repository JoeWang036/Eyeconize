package com.whu.eyerecongize.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.whu.eyerecongize.R;

public class BigButton extends View {

    private Bitmap content;
    private Bitmap rect;

    private Paint paint;

    float hei;
    float wid;
    Drawable drawable;

    public BigButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray tp= context.obtainStyledAttributes(attrs,R.styleable.BigButton);
        for(int i=0;i<tp.getIndexCount();i++){
            int index=tp.getIndex(i);
            if(index==R.styleable.BigButton_contentID) drawable=tp.getDrawable(index);
        }
        initview(drawable);
    }

    private void initview(Drawable drawable){
        BitmapDrawable bitmapDrawable=(BitmapDrawable)drawable;
        content = bitmapDrawable.getBitmap();
        rect=BitmapFactory.decodeResource(getResources(),R.drawable.baianniu);
        paint=new Paint();
        paint.setAntiAlias(true);

    }



    public void setRec(boolean status){//用于触发按钮时切换背景
        if(status){
            rect=BitmapFactory.decodeResource(getResources(),R.drawable.green_big_button);
        }else{
            rect=BitmapFactory.decodeResource(getResources(),R.drawable.baianniu);
        }
    }

    @Override
    protected void onDraw(Canvas canvas){

        int viewWidth = getWidth();
        int viewHeight = getHeight();



        //if(viewWidth<rect.getWidth()||viewHeight<rect.getHeight()) {

            float scaleX = (float) viewWidth / rect.getWidth();
            float scaleY = (float) viewHeight / rect.getHeight();

            Matrix matrixCon = new Matrix();
            Matrix matrixRec = new Matrix();
            matrixRec.setScale(scaleX, scaleY);
            matrixCon.setScale((float)(scaleX), (float) (scaleY));

            hei=(rect.getHeight()-content.getHeight())*scaleY;
            wid=(rect.getWidth()-content.getWidth())*scaleX;

            matrixRec.postTranslate(0, 0);
            matrixCon.postTranslate((float)(wid/2.0), (float)(hei/2.0));

            canvas.drawBitmap(rect, matrixRec, paint);
            canvas.drawBitmap(content, matrixCon, paint);

           // return;
        //}


//        hei=rect.getHeight()-content.getHeight();
//        wid=rect.getWidth()-content.getWidth();
//
//        canvas.drawBitmap(rect,0,0,paint);
//        canvas.drawBitmap(content,(float)(hei/2.0),(float)(wid/2.0) ,paint);

    }


}
