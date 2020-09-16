package com.vunke.videochat.login;

import com.vunke.videochat.model.LoginInfo;

/**
 * Created by zhuxi on 2020/2/27.
 */

public interface LoginCallBack {
    /**
     * 登录成功
     * @param loginInfo
     */
    void onSuccess(LoginInfo loginInfo);

    /**
     * 登录失败
     * @param loginInfo
     */
    void onFailed(LoginInfo loginInfo);

    /**
     * 请求失败或者请求异常
     */
    void onError();

}
