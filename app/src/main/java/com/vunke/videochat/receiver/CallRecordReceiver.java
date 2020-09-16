package com.vunke.videochat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.vunke.videochat.callback.CallRecordCallBack;

/**
 * Created by zhuxi on 2020/4/17.
 */

public class CallRecordReceiver extends BroadcastReceiver {
    private static final String TAG = "CallRecordReceiver";
    public static final String CALL_RECORD_ACTION = "call_record_action";
    private CallRecordCallBack callRecordCallBack;
    public CallRecordReceiver(CallRecordCallBack callRecordCallBack){
        this.callRecordCallBack = callRecordCallBack;
    }

    public void setCallBack(CallRecordCallBack callRecordCallBack){
        this.callRecordCallBack = callRecordCallBack;
    }
    public CallRecordCallBack getCallBack(){
        return callRecordCallBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)){
            if (action.equals(CALL_RECORD_ACTION)){
                    if (callRecordCallBack!=null){
                        callRecordCallBack.onUpdate();
                    }
            }
        }
    }
}
