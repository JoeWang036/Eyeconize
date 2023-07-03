package com.whu.eyerecongize.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.whu.eyerecongize.R;

public class BarButton extends View {

    private Bitmap content;
    private Bitmap rect;

    private Bitmap save;

    private Bitmap select;
    private Paint paint;

    float hei;
    float wid;

    Drawable drawable;

    Drawable selectDrawable;





    public BarButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray tp= context.obtainStyledAttributes(attrs, R.styleable.BigButton);
        for(int i=0;i<tp.getIndexCount();i++){
            int index=tp.getIndex(i);
            if(index==R.styleable.BigButton_contentID) drawable=tp.getDrawable(index);
            if(index==R.styleable.BigButton_selectID) selectDrawable=tp.getDrawable(index);
        }
        initview(drawable);
    }

    private void initview(Drawable drawable){
        BitmapDrawable bitmapDrawable=(BitmapDrawable)drawable;
        content = bitmapDrawable.getBitmap();
        rect= BitmapFactory.decodeResource(getResources(),R.drawable.white_bar_button);
        bitmapDrawable=(BitmapDrawable)selectDrawable;
        select=bitmapDrawable.getBitmap();
        paint=new Paint();
        paint.setAntiAlias(true);

    }

    public void setRec(boolean status){//用于触发按钮时切换背景
        if(status){
            rect=BitmapFactory.decodeResource(getResources(),R.drawable.green_bar_button);
            save=content;
            content=select;
        }else{
            rect=BitmapFactory.decodeResource(getResources(),R.drawable.white_bar_button);
            select=content;
            content=save;
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
            matrixCon.setScale(scaleX, scaleY);
            matrixRec.setScale(scaleX, scaleY);

            hei=(rect.getHeight()-content.getHeight())*scaleY;
            wid=(rect.getWidth()-content.getWidth())*scaleX;

            float moveH=(float) (-(rect.getHeight()*0.1))*scaleY;
            float moveW=(float) (-(rect.getWidth()*0.1))*scaleX;

            matrixRec.postTranslate(moveW, moveH);
            matrixCon.postTranslate((float)((hei/2.0)+moveH*1.5), (float)(wid/2.0));

            canvas.drawBitmap(rect, matrixRec, paint);
            canvas.drawBitmap(content, matrixCon, paint);
            //return;
        //}


        //hei=rect.getHeight()-content.getHeight();
        //wid=rect.getWidth()-content.getWidth();
        //float moveH=(float) (-(rect.getHeight()*0.1));
        //float moveW=(float) (-(rect.getWidth()*0.1));

        //canvas.drawBitmap(rect,moveW,moveH,paint);
        //canvas.drawBitmap(content,(float)(hei/2.0)+moveH,(float)(wid/2.0)+moveW,paint);

    }

}
