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

public class MyDialog implements View.OnKeyListener{
    private static final String TAG = "MyDialog";
    private Context context;
    private Dialog dialog;
    private View mView;
    private TextView my_message;
    private TextView my_title;
    RelativeLayout my_commit;
    public MyDialog(Context context){
        this.context=context;
        builder();
    }
    private void builder(){
        mView =  LayoutInflater.from(context).inflate(R.layout.dialog_my,null);
        my_message= mView.findViewById(R.id.my_message);
        my_title = mView.findViewById(R.id.my_title);
        my_commit = mView.findViewById(R.id.my_commit);
        my_commit.setOnKeyListener(this);
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(mView);
    }
    public void setMessage(String message){
        if (!TextUtils.isEmpty(message)){
            my_message.setText(message);
        }
    }
    public void setTitle(String message){
        if (!TextUtils.isEmpty(message)){
            my_title.setText(message);
        }
    }
    public void setCommitOnClickLintener(View.OnClickListener lintener){
        if (lintener!=null){
            my_commit.setOnClickListener(lintener);
        }
    }
    private boolean isShow;
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
