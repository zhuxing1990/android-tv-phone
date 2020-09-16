package com.vunke.videochat.dialog;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.vunke.videochat.R;

import java.io.Serializable;

/**
 * Created by zhuxi on 2020/3/1.
 */

public class SetContactDialog implements Serializable ,View.OnKeyListener{
    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public SetContactDialog (Context context){
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
//    private Dialog dialog;
    private View mView;
    private RelativeLayout add_callaudio_rl1,add_callvideo_rl1,add_del_rl1,add_updae_rl1;
    private Button add_call_back;
    private boolean isShow =false;
    public SetContactDialog builder(){
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_setcontacts,null);
        add_call_back = mView.findViewById(R.id.add_call_back);
        add_callaudio_rl1= mView.findViewById(R.id.add_callaudio_rl1);
        add_callvideo_rl1= mView.findViewById(R.id.add_callvideo_rl1);
        add_del_rl1= mView.findViewById(R.id.add_del_rl1);
        add_updae_rl1= mView.findViewById(R.id.add_updae_rl1);
        add_callaudio_rl1.requestFocus();
        add_call_back.setOnKeyListener(this);
        add_callaudio_rl1.setOnKeyListener(this);
        add_callvideo_rl1.setOnKeyListener(this);
        add_del_rl1.setOnKeyListener(this);
        add_updae_rl1.setOnKeyListener(this);
        mView.setOnKeyListener(this);
        windowManager.addView(mView,layoutParams);
        isShow = true;
//        dialog = new Dialog(context,R.style.AlertDialogStyle);
//        dialog.setContentView(mView);
        return this;
    }

    public SetContactDialog setCallAudioOnClickLinstener(View.OnClickListener linstener){
        add_callaudio_rl1.setOnClickListener(linstener);
        return this;
    }
    public SetContactDialog setCallVideoOnClickLinstener(View.OnClickListener linstener){
        add_callvideo_rl1.setOnClickListener(linstener);
        return this;
    }
    public SetContactDialog setDelOnClickLinstener(View.OnClickListener linstener){
        add_del_rl1.setOnClickListener(linstener);
        return this;
    }
    public SetContactDialog setBackOnClickLinstener(View.OnClickListener linstener){
        add_call_back.setOnClickListener(linstener);
        return this;
    }
    public SetContactDialog setEditOnClickLinsterer(View.OnClickListener linsterer){
        add_updae_rl1.setOnClickListener(linsterer);
        return this;
    }
    public boolean isShowing(){
//        if (mView!=null && mView.getVisibility() == View.VISIBLE){
//            back true;
//        }
        return isShow;
//        if (dialog==null){
//            back false;
//        }
//        back dialog.isShowing();
    }
//    public SetContactDialog setCanceledOnTouchOutside(boolean b){
//        dialog.setCanceledOnTouchOutside(b);
//        back this;
//    }
//    public SetContactDialog setCancelable(boolean b){
//        dialog.setCancelable(b);
//        back this;
//    }
    public void show(){
        isShow = true;
        if (mView==null){
            builder();
            return;
        }
        if (mView!=null&& mView.getVisibility()==View.GONE){
            mView.setVisibility(View.VISIBLE);
        }
//        dialog.show();
    }
    public void cancel(){
//        if (mView!=null && mView.getVisibility() == View.VISIBLE){
//            mView.setVisibility(View.GONE);
//        }
        try {
            if (isShow){
                isShow = false;
                if (mView!=null){
                    windowManager.removeView(mView);
                    mView=null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        if (dialog!=null&&dialog.isShowing()){
//            dialog.cancel();
//        }
    }
    public void hide(){
        if (mView != null) {
            mView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            cancel();
        }
        return false;
    }
}
