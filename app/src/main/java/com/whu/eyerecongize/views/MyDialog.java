package com.whu.eyerecongize.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.whu.eyerecongize.R;

public class MyDialog extends Dialog {

    private TextView content;
    private TextView res;
    private ImageView resImg;

    boolean status;
    String contentStr;
    //自定义风格
    public MyDialog(@NonNull Context context,int theme,String contentStr,boolean status) {
        super(context,theme);
        this.status=status;
        this.contentStr=contentStr;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_my);

        content=findViewById(R.id.textViewDiaCon);
        res=findViewById(R.id.textViewDia);
        resImg=findViewById(R.id.imageViewDia);

        Typeface customTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/W7-P.ttf");
        content.setTypeface(customTypeface);
        res.setTypeface(customTypeface);

        if(status){
            resImg.setImageResource(R.drawable.activate);
            res.setText("发送成功");
        }
        else{
            resImg.setImageResource(R.drawable.passtive);
            res.setText("发送失败");
        }
        content.setText("已发送消息："+contentStr);

    }


}
