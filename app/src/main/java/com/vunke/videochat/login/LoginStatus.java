package com.vunke.videochat.login;

import com.vunke.videochat.model.LoginInfo;

/**
 * Created by zhuxi on 2020/2/27.
 */

public abstract class LoginStatus implements LoginCallBack {


    @Override
    public void onSuccess(LoginInfo loginInfo) {
    }

    @Override
    public void onFailed(LoginInfo loginInfo) {

    }

    @Override
    public void onError() {

    }


}
