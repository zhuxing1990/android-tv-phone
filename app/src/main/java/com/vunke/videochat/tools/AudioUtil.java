package com.vunke.videochat.tools;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;

/**
 * Created by zhuxi on 2020/9/8.
 */

public class AudioUtil {

    public static boolean hasMicroPhone(Context context){
        PackageManager pm = context.getPackageManager();
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        boolean hasMicroPhone = pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
        boolean isMicroPhoneMute = am.isMicrophoneMute();
        return hasMicroPhone&&!isMicroPhoneMute;
    }
    public static void openMicroPhone(Context context){
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setMicrophoneMute(false); //打开麦克风
    }
}
