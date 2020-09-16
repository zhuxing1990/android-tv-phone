package com.vunke.videochat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.vunke.videochat.base.BaseConfig;

/**
 * Created by zhuxi on 2020/8/24.
 */

public class UsbStatusReceiver extends BroadcastReceiver {
    private static final String TAG = "UsbStatusReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent!=null){
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)){
                Log.i(TAG, "onReceive: get usb device input:"+action);
                intent= new Intent(BaseConfig.RECEIVE_VIDEO_ACTIVITY);
                intent.putExtra("action", BaseConfig.RECEIVE_UPB_CHANGE);
                intent.putExtra("status",action);
                context.sendBroadcast(intent);
            }
        }
    }

}
