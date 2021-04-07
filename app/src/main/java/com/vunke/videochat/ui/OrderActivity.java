package com.vunke.videochat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.vunke.videochat.R;
import com.vunke.videochat.base.BaseConfig;
import com.vunke.videochat.dialog.MyDialog;
import com.vunke.videochat.login.UserInfoUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderActivity extends AppCompatActivity {
    private static final String TAG = "OrderActivity";
    RelativeLayout order_commit,order_back;;
    TextView order_username;
    TextView order_usercard;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        initListener();
        initData();
    }
    private UserInfoUtil userInfoUtil;
    private MyDialog dialog;
    private void initData() {
        try {
            userInfoUtil = UserInfoUtil.getInstance(this);
            JSONObject json = new JSONObject();
            json.put("userId",userInfoUtil.getUserId())
                    .put("userToken",userInfoUtil.getUserToken());
            OkGo.<String>post(BaseConfig.BASE_URL+BaseConfig.QUY_BY_CUSTOMER_DATA).tag(this)
                    .upJson(json)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            try {
                                if (!TextUtils.isEmpty(response.body())){
                                    JSONObject js = new JSONObject(response.body());
                                    if (js.has("code")){
                                        int code = js.getInt("code");
                                        if (code==200){
                                            String certNum = js.getString("certNum");
                                            String custName = js.getString("custName");
                                            order_username.setText(custName);
                                           if (!TextUtils.isEmpty(certNum)){
                                               try {
                                                   if (certNum.length()==18){
                                                       String substring = certNum.substring(certNum.length() - 8,certNum.length() - 4);
                                                       String str = certNum.replace(substring,"****");
                                                       order_usercard.setText(str);
                                                   }
                                               }catch (Exception e){
                                                   e.printStackTrace();
                                                   order_usercard.setText(certNum);
                                               }
                                           }
                                        }else{
                                            if (js.has("message")){
                                                String message = js.getString("message");
                                                if (!TextUtils.isEmpty(message)){
//                                                    Toast.makeText(OrderActivity.this,message,Toast.LENGTH_SHORT).show();
                                                    initDialog(message);
                                                }
                                            }
                                        }
                                    }
                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Log.i(TAG, "onError: ");
                            initDialog("网络异常，请稍候再试!");
                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    private void initDialog(String message) {
        CancelDialog();
        dialog = new MyDialog(OrderActivity.this);
        dialog.setMessage(message);
        dialog.setCommitOnClickLintener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });
        dialog.show();
    }
    private void CancelDialog(){
        if (dialog!=null){
            dialog.cancel();
        }
    }
    private void initListener() {
        order_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderActivity.this, SelectPhoneActivity.class);
                startActivity(intent);
                finish();
            }
        });
        order_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        order_username = findViewById(R.id.order_username);
        order_usercard = findViewById(R.id.order_usercard);
        order_commit = findViewById(R.id.order_commit);
        order_back = findViewById(R.id.order_back);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CancelDialog();
    }
}
