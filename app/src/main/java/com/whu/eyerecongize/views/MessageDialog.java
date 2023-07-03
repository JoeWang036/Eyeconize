package com.whu.eyerecongize.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.whu.eyerecongize.R;

public class MessageDialog extends Dialog {

    private TextView content;

    boolean status;
    String contentStr;

    public MessageDialog(@NonNull Context context, int theme,boolean status) {
        super(context,theme);
        this.status=status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message_dialog);

        content=findViewById(R.id.messageContent);


        Typeface customTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/W7-P.ttf");
        content.setTypeface(customTypeface);


        if(status){
           content.setText("已经是第一页了！");
        }
        else{
            content.setText("已经是最后一页了！");
        }


    }
}
