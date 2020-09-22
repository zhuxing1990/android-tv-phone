package com.vunke.videochat.manage;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.vunke.videochat.base.BaseConfig;
import com.vunke.videochat.dialog.SuccessfulOpenDialog;
import com.vunke.videochat.login.UserInfoUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhuxi on 2020/9/15.
 */

public class SelectPhoneManage {
    private static final String TAG = "SelectPhoneManage";
    public static void fixedLineNumber(final Activity context, String PhoneNumber){
        UserInfoUtil userInfoUtil = UserInfoUtil.getInstance(context);
        try {
            JSONObject json = new JSONObject();
            json.put("userId",userInfoUtil.getUserId())
                .put("fixedLineNumber",PhoneNumber);
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
                                            Intent intent = new Intent(BaseConfig.RECEVIE_OPEN_OVER);
                                            context.sendBroadcast(intent);
                                            final SuccessfulOpenDialog successfulOpenDialog = new SuccessfulOpenDialog(context);
                                            successfulOpenDialog.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    successfulOpenDialog.cancel();
                                                    context.finish();
                                                }
                                            });
                                            successfulOpenDialog.show();
                                        }else{

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

                        }
                    });
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
