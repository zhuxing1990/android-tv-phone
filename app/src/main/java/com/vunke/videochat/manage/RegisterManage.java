package com.vunke.videochat.manage;

import android.content.Context;
import android.widget.Toast;

import com.vunke.videochat.config.BaseConfig;
import com.vunke.videochat.service.LinphoneMiniManager;

import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCoreException;

/**
 * Created by zhuxi on 2019/11/20.
 */

public class RegisterManage {

    public static void Login(Context context,String userName,String passWord){
        try {
            LinphoneMiniManager instance = LinphoneMiniManager.getInstance();
            LinphoneAddress.TransportType transport = LinphoneAddress.TransportType.LinphoneTransportUdp;
            instance.lilin_reg(BaseConfig.INSTANCE.getIpaddr(),BaseConfig.INSTANCE.getAreaCode()+userName, passWord,BaseConfig.INSTANCE.getPort() ,transport);
        }catch (LinphoneCoreException e){
            e.printStackTrace();
            Toast.makeText(context,"登录失败，请稍候再试!",Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context,"登录异常,请重试!",Toast.LENGTH_SHORT).show();
        }
    }
}
