package com.vunke.videochat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.vunke.videochat.R;

/**
 * Created by zhuxi on 2020/8/22.
 */

public class NotCameraDialog implements View.OnKeyListener{
    private static final String TAG = "NotCameraDialog";
    private boolean isShow = false;
//    private static NotCameraDialog instance;
//    public static  NotCameraDialog getInstance(Context context){
//        if (null==instance){
//            instance = new NotCameraDialog(context);
//        }
//        back instance;
//    }
    private View mView;
    private Dialog dialog;
    private Context context;
    public NotCameraDialog(Context context){
       this.context = context;
//        Builder(context);
    }

    public NotCameraDialog Builder(Context context){
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_not_camera,null);
        RelativeLayout not_camera_rl =mView.findViewById(R.id.not_camera_rl);
        not_camera_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        not_camera_rl.setOnKeyListener(this);
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(mView);
     return this;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            cancel();
        }
        return false;
    }
    public boolean isShow(){
        return isShow;
    }
    public void show(){
        isShow = true;
        try {
            if (mView!=null&& mView.getVisibility()==View.GONE){
                mView.setVisibility(View.VISIBLE);
            }
            if (dialog!=null&&!dialog.isShowing()){
                dialog.show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void cancel() {
        try {
            if (isShow){
                isShow = false;
                if (dialog!=null&&dialog.isShowing()){
                    isShow = false;
                    dialog.cancel();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
