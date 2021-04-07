package com.vunke.videochat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vunke.videochat.R;

public class OpenProgessDialog extends Dialog {
    private TextView open_progess_message;
    public OpenProgessDialog(Context context){
        super(context,R.style.AlertDialogStyle);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_open_progess,null);
        open_progess_message = view.findViewById(R.id.open_progess_message);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(view, lp);
        setCancelable(false);
    }

    public OpenProgessDialog setMessage(CharSequence message){
        if (!TextUtils.isEmpty(message)) {
            open_progess_message.setText(message);
        }
        return this;
    }
}
