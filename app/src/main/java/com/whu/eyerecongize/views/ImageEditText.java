package com.whu.eyerecongize.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.whu.eyerecongize.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ImageEditText extends androidx.appcompat.widget.AppCompatEditText{

    public Drawable lon;
    public Drawable dot;

    public int maxLenth;//dot 1 lon 1.75

    public int nowLenth;

    public ImageEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        lon=ContextCompat.getDrawable(context, R.drawable.lon);
        dot=ContextCompat.getDrawable(context,R.drawable.dot);


        Drawable background = ContextCompat.getDrawable(context,R.drawable.text_bar);
        setBackgroundDrawable(background);


        disableShowInput(this);
        setSingleLine(true); // 设置为单行输入
        requestFocus();
        setText("    ");
        setSelection(4);

        TypedArray tp = context.obtainStyledAttributes(attrs, R.styleable.BigButton);
        for(int i=0;i<tp.getIndexCount();i++){
            int index=tp.getIndex(i);
            if(index==R.styleable.BigButton_barStatus)maxLenth=tp.getInteger(index,14);
        }

        nowLenth=0;


    }


    public void insertImage(Drawable drawable) {
        SpannableString spanString = new SpannableString("img");
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spanString.setSpan(imageSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int selectionStart = getSelectionStart();
        Editable editable = getText();
        editable.insert(selectionStart, spanString);
        this.append(" ");
    }

    public void insertSpace(){
        int selectionStart = getSelectionStart();
        Editable editable = getText();
        editable.insert(selectionStart," ");
    }

    public void disableShowInput(EditText et) {
        Class<EditText> cls = EditText.class;
        Method method;
        try {
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(et, false);
        } catch (Exception e) {//TODO: handle exception
        }
    }

    public void clear(){
        setText("    ");
        setSelection(4);
        nowLenth=0;
    }
}
