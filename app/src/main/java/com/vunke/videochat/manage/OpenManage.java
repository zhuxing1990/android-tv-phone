package com.vunke.videochat.manage;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.vunke.videochat.tools.SPUtils;

public class OpenManage {
    private static final String TAG = "OpenManage";
    public static void saveOpenInfo(Context context,String userId , String pheonNumber){
        if (!TextUtils.isEmpty(pheonNumber)){
            Log.i(TAG, "saveOpenInfo:  userId:"+userId+"<-->phoneNumber:"+pheonNumber);
            SPUtils.putString(context,userId,pheonNumber);
        }
    }
}
