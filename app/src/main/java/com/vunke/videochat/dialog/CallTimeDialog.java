package com.vunke.videochat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.vunke.videochat.R;
import com.vunke.videochat.callback.CallOverCallBack;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by zhuxi on 2020/8/29.
 */

public class CallTimeDialog implements View.OnKeyListener{
    private static final String TAG = "CallTimeDialog";
    private Context context;
    private View mView;
//    private WindowManager windowManager;
//    private WindowManager.LayoutParams layoutParams;
    private boolean isShow = false;
    private Dialog dialog;
    private DisposableObserver<Long> disposableObserver;
    public CallTimeDialog(Context context){
        this.context = context;
        builder();
//        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        layoutParams = new WindowManager.LayoutParams();
//        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
//        layoutParams.format = PixelFormat.RGBA_8888;
//        // 设置浮动窗口不可聚焦
////        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        //设置窗口权重
//        layoutParams.gravity = Gravity.RIGHT;
//        layoutParams.x = 0;
//        layoutParams.y = 0;
//        layoutParams.width =  WindowManager.LayoutParams.MATCH_PARENT;;
//        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
    }
    private RelativeLayout calltime_confirm;
    private void builder() {
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_calltime,null);
        calltime_confirm = mView.findViewById(R.id.selectphone_commit);
        calltime_confirm.requestFocus();
        RelativeLayout calltime_back = mView.findViewById(R.id.selectphone_back);
        calltime_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        calltime_confirm.setOnKeyListener(this);
        calltime_back.setOnKeyListener(this);
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(mView);
    }
    public CallTimeDialog setConfirmOnClickListener(View.OnClickListener listener){
        if (listener!=null){
            calltime_confirm.setOnClickListener(listener);
        }
        return this;
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
//           try {
//                   windowManager.addView(mView,layoutParams);
//           }catch (Exception e){
//                e.printStackTrace();
//           }
            CancelObserver();
             disposableObserver = new DisposableObserver<Long>() {
                @Override
                public void onNext(Long aLong) {
                    if (callOverCallBack != null) {
                        callOverCallBack.onOver();
                    }
                    onComplete();
                }

                @Override
                public void onError(Throwable e) {
                    dispose();
                }

                @Override
                public void onComplete() {
                    dispose();
                }
            };
            Observable.interval(1, TimeUnit.MINUTES)
//            Observable.interval(10, TimeUnit.SECONDS)
                    .subscribe(disposableObserver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void cancel() {
        try {
            if (isShow){
                isShow = false;
//                if (mView!=null){
//                    windowManager.removeView(mView);
//                }
                if (dialog!=null&&dialog.isShowing()){
                    isShow = false;
                    dialog.cancel();
                }
            }
            CancelObserver();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void CancelObserver(){
        if (disposableObserver!=null&&!disposableObserver.isDisposed()){
            disposableObserver.dispose();
        }
    }
    private CallOverCallBack callOverCallBack;
    public void setCallOverCallBack(CallOverCallBack callOverCallBack) {
        this.callOverCallBack = callOverCallBack;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() ==KeyEvent.ACTION_DOWN){
            if (keyCode == KeyEvent.KEYCODE_BACK){
                cancel();
            }
        }
        return false;
    }
}
