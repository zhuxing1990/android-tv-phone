package com.vunke.videochat.base;

import android.app.Application;
import android.content.IntentFilter;

import com.lzy.okgo.OkGo;
import com.vunke.videochat.config.BaseConfig;
import com.vunke.videochat.receiver.LinphoneStatusReceiver;

/**
 * Created by zhuxi on 2019/11/20.
 */

public class BaseAppclication extends Application {
    private static final String TAG = "BaseAppclication";
    private LinphoneStatusReceiver mReceiver;
    @Override
    public void onCreate() {
        super.onCreate();
        OkGo.getInstance().init(this);
        registerBroad();
    }
    private void registerBroad() {
        mReceiver = new LinphoneStatusReceiver();
        IntentFilter intentFilter = new IntentFilter(BaseConfig.INSTANCE.getRECEIVE_MAIN_ACTIVITY());
        registerReceiver(mReceiver, intentFilter);
    }
}