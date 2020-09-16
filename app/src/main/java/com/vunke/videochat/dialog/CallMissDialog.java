package com.vunke.videochat.dialog;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.vunke.videochat.R;

/**
 * Created by zhuxi on 2020/8/22.
 */

public class CallMissDialog implements View.OnKeyListener {

    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public static CallMissDialog instance;

    public CallMissDialog getInstance(){
        if (null==instance){
            instance = new CallMissDialog(context);
        }
        return instance;
    }


    public CallMissDialog(Context context){
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.format = PixelFormat.RGBA_8888;
        // 设置浮动窗口不可聚焦
//        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //设置窗口权重
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width =  WindowManager.LayoutParams.MATCH_PARENT;;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }
    private View mView;
    private  Button dialog_miss_call;
    public CallMissDialog builder(String miss_info, String miss_number){
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_call_miss,null);
        TextView dialog_miss_info = mView.findViewById(R.id.dialog_miss_info);
        dialog_miss_info.setText(miss_info);
        TextView dialog_miss_number = mView.findViewById(R.id.dialog_miss_number);
        dialog_miss_number.setText(miss_number);
        dialog_miss_call= mView.findViewById(R.id.dialog_miss_call);
        Button dialog_miss_cancel= mView.findViewById(R.id.dialog_miss_cancel);
        dialog_miss_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        dialog_miss_cancel.setOnKeyListener(this);
        dialog_miss_call.setOnKeyListener(this);
        mView.setOnKeyListener(this);
        windowManager.addView(mView,layoutParams);
        isShow = true;
        return this;
    }
    private boolean isShow = false;
    public Boolean isShow(){
        return isShow;
    }
    public CallMissDialog setCallBackOnClickListener(View.OnClickListener listener){
        if (null!=listener){
            dialog_miss_call.setOnClickListener(listener);
        }
        return this;
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            cancel();
        }
        return false;
    }

    private void cancel() {
        try {
            if (isShow){
                windowManager.removeView(mView);
                isShow = false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
