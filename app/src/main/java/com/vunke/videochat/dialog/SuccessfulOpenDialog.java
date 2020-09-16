package com.vunke.videochat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.vunke.videochat.R;

/**
 * Created by zhuxi on 2020/9/15.
 */

public class SuccessfulOpenDialog implements View.OnKeyListener {
    private static final String TAG = "SuccessfulOpenDialog";

    private Dialog dialog;
    private View mView;
    private Context context;

    public SuccessfulOpenDialog(Context context){
        this.context = context;
        builder();
    }
    private Button diialog_selectphone_text;
    private void builder() {
        mView = LayoutInflater.from(context).inflate(R.layout.dialog_successfulopening,null);
        diialog_selectphone_text  = mView.findViewById(R.id.diialog_selectphone_text);
        diialog_selectphone_text.requestFocus();
        diialog_selectphone_text.setOnKeyListener(this);
        dialog = new Dialog(context,R.style.AlertDialogStyle);
        dialog.setContentView(mView);
    }
    public SuccessfulOpenDialog setOnClickListener(View.OnClickListener listener){
        if (listener!=null){
            diialog_selectphone_text.setOnClickListener(listener);
        }
        return this;
    }
    private boolean isShow;
    public boolean isShow() {
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
                    dialog.cancel();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
