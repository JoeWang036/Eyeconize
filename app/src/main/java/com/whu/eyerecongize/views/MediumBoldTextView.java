

package com.whu.eyerecongize.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.whu.eyerecongize.R;


/**
 * 自定义粗体TextView
 *
 * @author: fWX1079472
 * @date: 2022/1/15
 */
public class MediumBoldTextView extends androidx.appcompat.widget.AppCompatTextView {
    private float mStrokeWidth = 0.5f;

    public MediumBoldTextView(Context context) {
        super(context);
    }

    public MediumBoldTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MediumBoldTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MediumBold_TextView, defStyleAttr, 0);
        mStrokeWidth = array.getFloat(R.styleable.MediumBold_TextView_strokeWidth, mStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 获取当前控件的画笔
        TextPaint paint = getPaint();

        // 设置画笔的描边宽度值
        paint.setStrokeWidth(mStrokeWidth);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        super.onDraw(canvas);
    }
}
