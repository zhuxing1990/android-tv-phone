package com.vunke.videochat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vunke.videochat.R;

/**
 * Created by zhuxi on 2020/9/15.
 */

public class SelectPhoneDialog implements View.OnKeyListener {
    private static final String TAG = "SelectPhoneDialog";
    private Context context;
    private Dialog dialog;
    private View mView;
    private TextView selectphone_info;
    public SelectPhoneDialog(Context context){
        this.context=context;
        builder();
    }
    RelativeLayout selectphone_commit;
    public void builder(){
        mView  = LayoutInflater.from(context).inflate(R.layout.dialog_selectphone,null);
        selectphone_commit = mView.findViewById(R.id.selectphone_commit);
        selectphone_info = mView.findViewById(R.id.selectphone_info);
        selectphone_commit.requestFocus();
        RelativeLayout selectphone_back = mView.findViewById(R.id.selectphone_back);
        selectphone_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        selectphone_back.setOnKeyListener(this);
        selectphone_commit.setOnKeyListener(this);
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(mView);
    }
    private boolean isShow;
    public SelectPhoneDialog setConfirmOnClickListener(View.OnClickListener listener){
        if (listener!=null){
            selectphone_commit.setOnClickListener(listener);
        }
        return this;
    }
    public void setMessage(String message){
        if (!TextUtils.isEmpty(message)){
            selectphone_info.setText(message);
        }
    }
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
