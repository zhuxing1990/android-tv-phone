package com.vunke.videochat.manage;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.vunke.videochat.base.BaseConfig;
import com.vunke.videochat.config.CallInfo;
import com.vunke.videochat.db.CallRecordTable;
import com.vunke.videochat.service.LinphoneMiniManager;
import com.vunke.videochat.ui.AudioActivity;
import com.vunke.videochat.ui.VideoActivity;

import org.linphone.core.LinphoneCoreException;

/**
 * Created by zhuxi on 2019/11/20.
 */

public class CallManage {
    private static final String TAG = "CallManage";
    public static  void CallVideo(Activity activity, String str) {
        try {
            LinphoneMiniManager instance = LinphoneMiniManager.getInstance();
            Log.i(TAG, "CallVideo: "+str);
            instance.lilin_call(str, BaseConfig.ipaddr,true);
//            instance.lilin_call(BaseConfig.INSTANCE.getNINE()+str,BaseConfig.INSTANCE.getIpaddr(),true);
            Intent intent =new Intent(activity,VideoActivity.class);
            intent.putExtra("number",str);
            intent.putExtra(CallRecordTable.INSTANCE.getCALL_STATUS(), CallInfo.INSTANCE.getCALL_OUT());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (LinphoneCoreException e1) {
            org.linphone.mediastream.Log.e("MainActivity", e1.getMessage());
        }
    }
    public static  void CallAudio(Activity activity, String str) {
        try {
            LinphoneMiniManager instance = LinphoneMiniManager.getInstance();
            Log.i(TAG, "CallVideo: "+str);
            instance.lilin_call(str, BaseConfig.ipaddr,false);
//            instance.lilin_call(BaseConfig.INSTANCE.getNINE()+str,BaseConfig.INSTANCE.getIpaddr(),false);
            Intent intent = new Intent(activity, AudioActivity.class);
            intent.putExtra("number",str);
            intent.putExtra(CallRecordTable.INSTANCE.getCALL_STATUS(), CallInfo.INSTANCE.getCALL_OUT());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
//            Intent intent =new Intent(activity,VideoActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            activity.startActivity(intent);
        } catch (LinphoneCoreException e1) {
            org.linphone.mediastream.Log.e("MainActivity", e1.getMessage());
        }
    }
}
