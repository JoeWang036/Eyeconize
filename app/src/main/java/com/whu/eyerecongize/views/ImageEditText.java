package com.whu.eyerecongize.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.whu.eyerecongize.R;

public class ImageEditText extends androidx.appcompat.widget.AppCompatEditText{

    Drawable tmpd;

    public ImageEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        tmpd=ContextCompat.getDrawable(context, R.drawable.lon);

        Drawable background = ContextCompat.getDrawable(context,R.drawable.text_bar);
        setBackgroundDrawable(background);

        setSingleLine(true); // 设置为单行输入

        setCursorVisible(true);
        setSelection(3);

        insertImage(tmpd);
    }


    public void insertImage(Drawable drawable) {
        SpannableString spanString = new SpannableString("img");
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        spanString.setSpan(imageSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int selectionStart = getSelectionStart();
        Editable editable = getText();
        editable.insert(selectionStart, spanString);
    }
}
