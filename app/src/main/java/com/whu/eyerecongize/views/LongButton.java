package com.whu.eyerecongize.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.whu.eyerecongize.R;

public class LongButton extends View {
    private Bitmap icon;
    private Bitmap rect;

    private Bitmap codeContent;

    private Typeface customTypeface;

    private Paint paint;

    float hei;
    float wid;

    Drawable drawable;
    Drawable code;
    public LongButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray tp = context.obtainStyledAttributes(attrs, R.styleable.BigButton);
        for(int i=0;i<tp.getIndexCount();i++){
            int index=tp.getIndex(i);
            if(index==R.styleable.BigButton_contentID) drawable=tp.getDrawable(index);
            if(index==R.styleable.BigButton_codeID) code=tp.getDrawable(index);
        }
        initview(drawable,code);
    }

    private void initview(Drawable drawable,Drawable code) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        BitmapDrawable codeBit = (BitmapDrawable) code;
        icon = bitmapDrawable.getBitmap();
        codeContent=codeBit.getBitmap();
        rect = BitmapFactory.decodeResource(getResources(), R.drawable.white_long_button);
        paint = new Paint();
        paint.setAntiAlias(true);
        customTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/W7-P.ttf");

    }

    @Override
    protected void onDraw(Canvas canvas) {

        int viewWidth = getWidth();
        int viewHeight = getHeight();


        float scaleX = (float) (viewWidth / (rect.getWidth()*1.5+icon.getWidth()));

        float scaleY = (float) viewHeight / rect.getHeight();

        Matrix matrixIcon = new Matrix();
        Matrix matrixRec = new Matrix();
        Matrix matrixCod=new Matrix();
        matrixIcon.setScale(scaleX, scaleY);
        matrixRec.setScale(scaleX, scaleY);
        matrixCod.setScale(scaleX, scaleY);



        matrixIcon.postTranslate(0, 0);

        float moveH=(float) -(rect.getHeight()*0.1)*scaleY;
        float moveW=(float)(icon.getWidth()*1.6)*scaleX;

        matrixRec.postTranslate(moveW, moveH);

        canvas.drawBitmap(icon, matrixIcon, paint);
        canvas.drawBitmap(rect, matrixRec, paint);

        // 设置画笔字体和字号
        paint.setTypeface(customTypeface);
        paint.setTextSize(75);


        String text = "求救"; // 要绘制的字符串

        float textWidth = paint.measureText(text); // 字符串的宽度

        float centerX = (float) ((rect.getWidth() * scaleX / 2));
        float centerY = (float) ((rect.getHeight() * scaleY / 2)-(rect.getHeight()*0.1)*scaleY);

        float textX = (float) (centerX/2+(icon.getWidth()*1.6)*scaleX)-textWidth / 2; // 字符串的绘制横坐标,取背景长方形4/1处为中心
        float textY = centerY - (paint.descent() + paint.ascent()) / 2; // 字符串的绘制纵坐标


        float codeX=(float) (centerX*1.4+(icon.getWidth()*1.6)*scaleX)-textWidth / 2;
        float codeY=(float)(centerY-codeContent.getHeight()*scaleY/2);
        matrixCod.postTranslate(codeX,codeY);


        canvas.drawBitmap(codeContent,matrixCod, paint);
        canvas.drawText(text, textX, textY, paint); // 绘制字符串

    }

}
