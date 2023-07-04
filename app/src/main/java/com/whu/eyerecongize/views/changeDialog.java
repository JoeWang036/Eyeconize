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

public class changeDialog extends Dialog {

    boolean choice;

    boolean status=false;

    ImageView up;

    ImageView down;

    TextView text;

    //自定义风格
    public changeDialog(@NonNull Context context, int theme,boolean choice) {//choice true是空调 false是灯
        super(context,theme);
        this.choice=choice;

    }

    public void changeStatus(boolean position){

        if(position){
            if(choice){
                up.setImageResource(R.drawable.uptmp_active);
            }else{
                up.setImageResource(R.drawable.openlight_active);
            }

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(choice){
                        up.setImageResource(R.drawable.uptmp);
                    }else{
                        up.setImageResource(R.drawable.openlight);
                    }
                }
            }, 1000);
        }else {
            if(choice){
                down.setImageResource(R.drawable.downtmp_active);
            }else{
                down.setImageResource(R.drawable.closelight_active);
            }

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(choice){
                        down.setImageResource(R.drawable.downtmp);
                    }else{
                        down.setImageResource(R.drawable.closelight);
                    }
                }
            }, 1000);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialogchange);

        up=findViewById(R.id.changeup);
        down=findViewById(R.id.changedown);

        text=findViewById(R.id.textViewchange);


        Typeface customTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/W7-P.ttf");
        text.setTypeface(customTypeface);


        if(choice){
            up.setImageResource(R.drawable.uptmp);
            down.setImageResource(R.drawable.downtmp);
        }else{
            up.setImageResource(R.drawable.openlight);
            down.setImageResource(R.drawable.closelight);
        }

    }
}
