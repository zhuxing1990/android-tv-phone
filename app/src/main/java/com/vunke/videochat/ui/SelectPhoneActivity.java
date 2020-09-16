package com.vunke.videochat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.vunke.videochat.R;
import com.vunke.videochat.adaper.RecyclerViewSpacesItemDecoration;
import com.vunke.videochat.adaper.SelectPhoneAdapter;
import com.vunke.videochat.base.BaseConfig;
import com.vunke.videochat.login.UserInfoUtil;
import com.vunke.videochat.model.AccountBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zhuxi on 2020/9/11.
 */

public class SelectPhoneActivity extends AppCompatActivity {
    private static final String TAG = "SelectPhoneActivity";
    private UserInfoUtil userInfoUtil;
    private RecyclerView select_num_recvcler;
    private SelectPhoneAdapter adapter;
    private List<String> phoneList;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectphone);
        initView();
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        View rootView = findViewById(android.R.id.content);
//        BackgroundManage.setBackground(this, rootView);
    }

    private void initData() {
        userInfoUtil = UserInfoUtil.getInstance(this);
        try {
            JSONObject json = new JSONObject();
            json.put("userId",userInfoUtil.getUserId())
                .put("userToken",userInfoUtil.getUserToken());
            OkGo.<String>post(BaseConfig.BASE_URL+BaseConfig.GET_OPTIONAL_ACCOUNT)
                    .tag(this).upJson(json)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.i(TAG, "onSuccess: "+response);
                            if (!TextUtils.isEmpty(response.body())){
                                try {
                                    AccountBean accountBean = new Gson().fromJson(response.body(), AccountBean.class);
                                    if (accountBean!=null){
                                        if (200==accountBean.getCode()){
                                            phoneList = accountBean.getOptionalFixedLine();
                                            adapter = new SelectPhoneAdapter(SelectPhoneActivity.this,phoneList);
                                            select_num_recvcler.setAdapter(adapter);
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
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

    private void initView() {
        select_num_recvcler = findViewById(R.id.selectphone_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3);
        select_num_recvcler.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION,20);//top间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION,20);//底部间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION,30);//左间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION,30);//右间距
        select_num_recvcler.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
    }
}
