package com.vunke.videochat.manage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.vunke.videochat.base.BaseConfig;
import com.vunke.videochat.dialog.MyDialog;
import com.vunke.videochat.dialog.SuccessfulOpenDialog;
import com.vunke.videochat.login.LoginCallBack;
import com.vunke.videochat.login.LoginManage;
import com.vunke.videochat.login.UserInfoUtil;
import com.vunke.videochat.model.LoginInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuxi on 2020/9/15.
 */

public class SelectPhoneManage {
    private static final String TAG = "SelectPhoneManage";
    private static ProgressDialog progressDialog;
    public static void fixedLineNumber(final Activity context,final String PhoneNumber){
        progressDialog =ProgressDialog.show(context,null,"正在开通中，请稍候……");
        UserInfoUtil userInfoUtil = UserInfoUtil.getInstance(context);
        try {
            JSONObject json = new JSONObject();
            json.put("userId",userInfoUtil.getUserId())
                .put("fixedLineNumber",PhoneNumber)
                .put("userToken",userInfoUtil.getUserToken());
            OkGo.<String>post(BaseConfig.BASE_URL+BaseConfig.FIXE_LINE_BINDING)
                    .tag(TAG).upJson(json)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.i(TAG, "onSuccess: "+response.body());
                            try {
                                if (!TextUtils.isEmpty(response.body())){
                                    JSONObject js = new JSONObject(response.body());
                                    if (js.has("code")){
                                        int code = js.getInt("code");
                                        if (200==code){

                                            initTimeOut(context);
                                        }else{
                                            if (js.has("message")){
                                                String message = js.getString("message");
                                                if (!TextUtils.isEmpty(message)){
                                                    initDialog(context,message);
                                                }
                                            }
                                        }
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Log.i(TAG, "onError: ");
                            initDialog(context,"网络异常，请稍候再试!");
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            progressDialog.dismiss();
                        }
                    });
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public static DisposableObserver<Long> disposableObserver;
    private static void initTimeOut(final Activity context) {
        if (disposableObserver!=null){
            if (!disposableObserver.isDisposed()){
                disposableObserver.dispose();
            }
        }
        disposableObserver = new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                UserInfoUtil userInfoUtil = UserInfoUtil.getInstance(context);
                LoginManage loginManage = new LoginManage();
                loginManage.startLogin(userInfoUtil.getUserId(), new LoginCallBack() {
                    @Override
                    public void onSuccess(LoginInfo loginInfo) {
                        final SuccessfulOpenDialog successfulOpenDialog = new SuccessfulOpenDialog(context);
                        successfulOpenDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(BaseConfig.RECEVIE_OPEN_OVER);
                                context.sendBroadcast(intent);
                                successfulOpenDialog.cancel();
                                context.finish();
                            }
                        });
                        successfulOpenDialog.show();
                    }

                    @Override
                    public void onFailed(LoginInfo loginInfo) {
                        initDialog(context, "开通失败，请拨打4009900901,人工开通.");
                    }

                    @Override
                    public void onError() {
                        initDialog(context, "开通失败，请稍后再试.");
                    }
                });
                onComplete();
            }

            @Override
            public void onError(Throwable e) {
                this.dispose();
            }

            @Override
            public void onComplete() {
                this.dispose();
            }
        };
        Observable.interval(60, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableObserver);
    }

    private static MyDialog dialog;
    private static void initDialog(final Activity context, String message) {
        CancelDialog();
        dialog = new MyDialog(context);
        dialog.setMessage(message);
        dialog.setCommitOnClickLintener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }
    private static void CancelDialog(){
        if (dialog!=null){
            dialog.cancel();
        }
    }
}
