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

public class CallRecordDialog implements Serializable ,View.OnKeyListener{
    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    public CallRecordDialog(Context context){
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
    private RelativeLayout callrecord_callaudio_rl1,callrecord_callvideo_rl1,callrecord_del_rl1,call_record_add_rl;
    private Button callrecord_call_back;
    public CallRecordDialog builder(){
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_callrecord,null);
        callrecord_call_back = mView.findViewById(R.id.callrecord_call_back);
        callrecord_callaudio_rl1= mView.findViewById(R.id.callrecord_callaudio_rl1);
        callrecord_callvideo_rl1= mView.findViewById(R.id.callrecord_callvideo_rl1);
        callrecord_del_rl1= mView.findViewById(R.id.callrecord_del_rl1);
        call_record_add_rl= mView.findViewById(R.id.call_record_add_rl);
        callrecord_callaudio_rl1.requestFocus();
        callrecord_call_back.setOnKeyListener(this);
        callrecord_callaudio_rl1.setOnKeyListener(this);
        callrecord_callvideo_rl1.setOnKeyListener(this);
        callrecord_del_rl1.setOnKeyListener(this);
        call_record_add_rl.setOnKeyListener(this);
        mView.setOnKeyListener(this);
        windowManager.addView(mView,layoutParams);
//        dialog = new Dialog(context,R.style.AlertDialogStyle);
//        dialog.setContentView(mView);
        return this;
    }

    public CallRecordDialog setCallAudioOnClickLinstener(View.OnClickListener linstener){
        callrecord_callaudio_rl1.setOnClickListener(linstener);
        return this;
    }
    public CallRecordDialog setCallVideoOnClickLinstener(View.OnClickListener linstener){
        callrecord_callvideo_rl1.setOnClickListener(linstener);
        return this;
    }
    public CallRecordDialog setDelOnClickLinstener(View.OnClickListener linstener){
        callrecord_del_rl1.setOnClickListener(linstener);
        return this;
    }
    public CallRecordDialog setBackOnClickLinstener(View.OnClickListener linstener){
        callrecord_call_back.setOnClickListener(linstener);
        return this;
    }
    public CallRecordDialog setAddOnClickListener(View.OnClickListener listener){
        call_record_add_rl.setOnClickListener(listener);
        return this;
    }
    public boolean isShowing(){
        if (mView!=null && mView.getVisibility() == View.VISIBLE){
            return true;
        }
        return false;
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
            if (mView!=null){
                windowManager.removeView(mView);
                mView=null;
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
