package com.whu.eyerecongize.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.whu.eyerecongize.R;
import com.whu.eyerecongize.connect.Setting;

import java.util.HashMap;
import java.util.Map;

public class ReceiveMesDialog extends Dialog {
    private TextView content;
    private TextView people;
    private ImageView readed;

    String contentStr;
    Map<Long,String>friends;

    long senderID;

    //自定义风格
    public ReceiveMesDialog(@NonNull Context context, int theme, String contentStr,long id) {
        super(context,theme);
        this.contentStr=contentStr;
        initialFriends();
        senderID=id;

    }

    private void initialFriends(){
        friends=new HashMap<Long,String>();
        friends.put(Setting.receiverID,"儿子");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.receive_mes_dialog);
        content=findViewById(R.id.textViewRes);
        people=findViewById(R.id.textViewResWho);
        readed=findViewById(R.id.imageViewread);



        Typeface customTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/W7-P.ttf");
        content.setTypeface(customTypeface);
        people.setTypeface(customTypeface);

        if(senderID!=-1){
            String name=friends.get(senderID);
            people.setText("来自"+name);
        }
        content.setText(contentStr);

    }

    public void changeStatus(){

        readed.setImageResource(R.drawable.read_active);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    readed.setImageResource(R.drawable.readed);
            }
        }, 1000);
    }
}
