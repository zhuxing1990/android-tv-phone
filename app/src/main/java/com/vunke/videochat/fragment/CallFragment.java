package com.vunke.videochat.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vunke.videochat.R;
import com.vunke.videochat.base.BaseConfig;
import com.vunke.videochat.callback.RegisterCallBack;
import com.vunke.videochat.dialog.NotCameraDialog;
import com.vunke.videochat.login.LoginCallBack;
import com.vunke.videochat.login.LoginManage;
import com.vunke.videochat.login.UserInfoUtil;
import com.vunke.videochat.manage.CallManage;
import com.vunke.videochat.manage.RegisterManage;
import com.vunke.videochat.model.LoginInfo;
import com.vunke.videochat.receiver.RegisterReceiver;
import com.vunke.videochat.service.LinphoneMiniManager;
import com.vunke.videochat.tools.FocusUtil;
import com.vunke.videochat.tools.SPUtils;
import com.vunke.videochat.ui.HomeActivity;
import com.vunke.videochat.ui.ProductDesActivity;
import com.vunke.videochat.ui.SelectPhoneActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuxi on 2020/2/27.
 */

public class CallFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "CallFragment";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


//    private TextView call_login_status;
    private TextView call_username;
    private EditText call_edit_num;
//    private Button call_relogin;
    private Button call_openforuse;
    private ImageView call_call_video,call_call_audio;
    private Button call_btn_num1,call_btn_num2,call_btn_num3,call_btn_num4,call_btn_num5,call_btn_num6,call_btn_num7,call_btn_num8,call_btn_num9,call_btn_num0,call_btn_del2,call_product_des;
    private LinphoneMiniManager instance;
    private LoginManage loginManage;
    private String userName;
    private String passWord;
    private UserInfoUtil userInfoUtil;
    private NotCameraDialog dialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call,null);
        initView(view);
        registerBroad();
        initLogin();
        return view;
    }
    private boolean isLogin;
    private long reLoginTimes = 0L;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0x2169:
                    stratLogin(userName,passWord);
                    break;
            }
        }
    };

    private void stratLogin(String userName,String passWord) {
        try {
            RegisterManage.Login(getActivity(),userName,passWord);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public RegisterReceiver mReceiver;

    private void registerBroad() {
        mReceiver = new RegisterReceiver(new RegisterCallBack() {
            @Override
            public void onSuccess() {
//                Toast.makeText(HomeActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                Log.i(TAG, "registerBroad longin onSuccess: ");
                isLogin = true;
            }

            @Override
            public void onFailed(String message) {
                Log.i(TAG, "registerBroad login onFailed: "+message);
                isLogin = false;
                if (message.contains("Busy Here")||message.contains("Forbidden")||message.contains("io error")){
                    if (System.currentTimeMillis()-reLoginTimes>30000){
                        Log.i(TAG, "registerBroad onFailed: start relogin");
                        reLoginTimes = System.currentTimeMillis();
                        stratLogin(userName,passWord);
                    }else{
                        Log.i(TAG, "onFailed: ");
                        handler.sendEmptyMessageDelayed(0x2169,30000);
                    }
                }
            }
        });
        IntentFilter intentFilter = new IntentFilter(BaseConfig.RECEIVE_MAIN_ACTIVITY);
        getActivity().registerReceiver(mReceiver,intentFilter);
    }

    private void initView(View view) {
//        call_login_status = view.findViewById(R.id.call_login_status);
//        call_login_status.requestFocus();
        call_edit_num = view.findViewById(R.id.call_edit_num);
//        call_relogin = view.findViewById(R.id.call_relogin);
//        call_relogin.setOnClickListener(this);
        call_username = view.findViewById(R.id.call_username);
        call_openforuse = view.findViewById(R.id.call_openforuse);
        call_openforuse.setOnClickListener(this);
        call_btn_num1 = view.findViewById(R.id.call_btn_num1);
        call_btn_num2 = view.findViewById(R.id.call_btn_num2);
        call_btn_num3 = view.findViewById(R.id.call_btn_num3);
        call_btn_num4 = view.findViewById(R.id.call_btn_num4);
        call_btn_num5 = view.findViewById(R.id.call_btn_num5);
        call_btn_num6 = view.findViewById(R.id.call_btn_num6);
        call_btn_num7 = view.findViewById(R.id.call_btn_num7);
        call_btn_num8 = view.findViewById(R.id.call_btn_num8);
        call_btn_num9 = view.findViewById(R.id.call_btn_num9);
        call_btn_num0 = view.findViewById(R.id.call_btn_num0);
        call_btn_del2 = view.findViewById(R.id.call_btn_del2);
        call_product_des = view.findViewById(R.id.call_product_des);
        call_call_audio = view.findViewById(R.id.call_call_audio);
        call_call_video = view.findViewById(R.id.call_call_video);
        call_btn_num1.setOnKeyListener(keyListener);
        call_btn_num2.setOnKeyListener(keyListener);
        call_btn_num3.setOnKeyListener(keyListener);
        call_btn_num4.setOnKeyListener(keyListener);
        call_btn_num5.setOnKeyListener(keyListener);
        call_btn_num6.setOnKeyListener(keyListener);
        call_btn_num7.setOnKeyListener(keyListener);
        call_btn_num8.setOnKeyListener(keyListener);
        call_btn_num9.setOnKeyListener(keyListener);
        call_btn_num0.setOnKeyListener(keyListener);
        call_btn_del2.setOnKeyListener(keyListener);
        call_call_audio.setOnKeyListener(keyListener);
        call_call_video.setOnKeyListener(keyListener);
        call_product_des.setOnKeyListener(keyListener);
        call_openforuse.setOnKeyListener(keyListener);
        ((HomeActivity) getActivity()).home_call_rl.setOnKeyListener(keyListener);
        call_btn_num1.setOnClickListener(this);
        call_btn_num2.setOnClickListener(this);
        call_btn_num3.setOnClickListener(this);
        call_btn_num4.setOnClickListener(this);
        call_btn_num5.setOnClickListener(this);
        call_btn_num6.setOnClickListener(this);
        call_btn_num7.setOnClickListener(this);
        call_btn_num8.setOnClickListener(this);
        call_btn_num9.setOnClickListener(this);
        call_btn_num0.setOnClickListener(this);
        call_btn_del2.setOnClickListener(this);
        call_product_des.setOnClickListener(this);
        call_btn_del2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                call_edit_num.setText("");
                return false;
            }
        });
        call_call_audio.setOnClickListener(this);
        call_call_video.setOnClickListener(this);
        call_call_audio.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                FocusUtil.INSTANCE.setFocus(hasFocus,v,getActivity());
            }
        });
        call_call_video.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                FocusUtil.INSTANCE.setFocus(hasFocus,v,getActivity());
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.call_relogin:
//                stopClick(call_relogin,5);
//                initRegister();
//                break;
            case R.id.call_openforuse:
                ShowOpenView();
                break;
            case R.id.call_btn_num1:
                call_edit_num.append("1");
                break;
            case R.id.call_btn_num2:
                call_edit_num.append("2");
                break;
            case R.id.call_btn_num3:
                call_edit_num.append("3");
                break;
            case R.id.call_btn_num4:
                call_edit_num.append("4");
                break;
            case R.id.call_btn_num5:
                call_edit_num.append("5");
                break;
            case R.id.call_btn_num6:
                call_edit_num.append("6");
                break;
            case R.id.call_btn_num7:
                call_edit_num.append("7");
                break;
            case R.id.call_btn_num8:
                call_edit_num.append("8");
                break;
            case R.id.call_btn_num9:
                call_edit_num.append("9");
                break;
            case R.id.call_btn_num0:
                call_edit_num.append("0");
                break;
            case R.id.call_btn_del2:
//                int index=call_edit_num.getSelectionStart();   //获取Edittext光标所在位置
//                String str=call_edit_num.getText().toString();
//                if (!TextUtils.isEmpty(str)) {//判断输入框不为空，执行删除
//                    call_edit_num.getText().delete(index-1,index);
//                }
                String  PhoneNumber = call_edit_num.getText().toString().trim();
                DelNumber(PhoneNumber);
                break;
            case R.id.call_call_audio:
                if (isLogin == true){
                    startCall(false);
                }else{
                    Toast.makeText(getActivity(),"正在登录中,请稍候再试!",Toast.LENGTH_SHORT).show();
                    initLogin();
                }
                break;
            case R.id.call_call_video:
                if (isLogin == true) {
                    startCall(true);
                }else{
                    Toast.makeText(getActivity(),"正在登录中,请稍候再试!",Toast.LENGTH_SHORT).show();
                    initLogin();
                }
                break;
            case R.id.call_product_des:
                goProductDes();
                break;
            default:
                break;
        }
    }

    private void goProductDes() {
       Intent intent = new Intent(getActivity(), ProductDesActivity.class);
        startActivity(intent);
    }

    private void ShowOpenView() {
        Intent intent = new Intent(getActivity(), SelectPhoneActivity.class);
        startActivity(intent);
    }

    private void DelNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {

        } else{
            StringBuffer buffer = new StringBuffer(phoneNumber);
            buffer.deleteCharAt(buffer.length() - 1);
            call_edit_num.setText(buffer.toString().trim());
        }
    }

    private void startCall(boolean isVideo) {
        String str=call_edit_num.getText().toString();
        if (TextUtils.isEmpty(str)){
            String lastCall = SPUtils.getString(getActivity(), BaseConfig.lastCallNumber,"");
            if (TextUtils.isEmpty(lastCall)){
                return;
            }else{
                Log.i(TAG, "startCall: lastCall:"+lastCall);
                call_edit_num.setText(lastCall);
            }
        }else{
            int numberOfCameras = Camera.getNumberOfCameras();
            Log.i(TAG,"get camera number:"+numberOfCameras);
            if (numberOfCameras==0){
                dialog =  new NotCameraDialog(getActivity());
                dialog .show();
            }else{
                Log.i(TAG, "onClick: startCall:"+str);
                SPUtils.putString(getActivity(), BaseConfig.lastCallNumber,str);
                if (isVideo){
                    CallManage.CallVideo(getActivity(),str);
                }else{
                    CallManage.CallAudio(getActivity(),str);
                }
            }
        }
    }
    private void initRegister() {
        Log.i(TAG, "initRegister: ");
        if (TextUtils.isEmpty(userName)){
            Log.i(TAG, "initRegister: get userName is null, relogin");
            initLogin();
            return;
        }
        if(!LinphoneMiniManager.isReady()){
            instance = LinphoneMiniManager.getInstance();
        }
        stratLogin(userName,passWord);
    }
    private void initLogin() {
        Log.i(TAG, "initLogin: ");
        userInfoUtil = UserInfoUtil.getInstance(getActivity());
        String userId = userInfoUtil.getUserId();
        if (!TextUtils.isEmpty(userId)){
//            login_userId.setText("用户账号:"+userInfoUtil.getUserId());
            loginManage = new LoginManage();
            loginManage.startLogin(userId, new LoginCallBack() {
                @Override
                public void onSuccess(LoginInfo loginInfo) {
                    Log.i(TAG, "onSuccess: get loginInfo success:"+loginInfo.toString());
                    //用户已经注册，可以正常登录
                    userName = loginInfo.getData().getUserName();
                    if (!TextUtils.isEmpty(userName)){
                        call_username.setText("本机号码:0"+userName);
                    }
                    passWord = loginInfo.getData().getPassword();
                    if(!LinphoneMiniManager.isReady()){
                        instance = LinphoneMiniManager.getInstance();
                    }
                    stratLogin(userName,passWord);
                }

                @Override
                public void onFailed(LoginInfo loginInfo) {
                    // 获取用户信息失败，无法登录
                    Log.i(TAG, "onSuccess: get loginInfo failed");
//                    call_login_status.setText("获取登陆信息失败:"+loginInfo.getMessage());
                    call_username.setText("您尚未开通此产品");
                    call_openforuse.requestFocus();
//                    Intent intent = new Intent(getActivity(), WelcomeActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                    getActivity().finish();
                }

                @Override
                public void onError() {
                    //网络异常
                    Log.i(TAG, "onSuccess: get loginInfo error");
//                    call_login_status.setText("网络异常，登录失败");
                    call_username.setText("网络异常，登录失败");
                }
            });
        }
    }
    private void stopClick(final Button button,final long stopTime) {
        Log.i(TAG, "stopClick: ");
        button.setEnabled(false);
        Observable<Long> longObservable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(Long t) throws Exception {
                        return t <= stopTime;
                    }
                }).map(new Function<Long, Long>() {
                    @Override
                    public Long apply(Long t) throws Exception {
                        return -(t-stopTime);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        DisposableObserver<Long> disposableObserver = new DisposableObserver<Long>() {

            @Override
            public void onNext(Long aLong) {
                Log.i(TAG, "onNext: "+aLong);
                if (aLong>0){
                    button.setText( aLong + "".trim());
                }else{
                    button.setText("登录");
                    onComplete();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ");
                button.setEnabled(true);
                this.dispose();
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
                button.setEnabled(true);
                this.dispose();
            }
        };
        longObservable.subscribe(disposableObserver);
    }

    private View.OnKeyListener keyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            Log.i(TAG, "onKey: keyCode:"+keyCode);
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode >= 7 && keyCode <= 16) {
                    call_edit_num.append((keyCode - 7) + "");
                    return true;
                } else if (keyCode == KeyEvent.KEYCODE_STAR) {
                    String string = call_edit_num.getText().toString();
                    if (!TextUtils.isEmpty(string)) {
                        String substring = string.substring(0,
                                string.length() - 1);
                        call_edit_num.setText(substring);
                        return true;
                    }

                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    switch (v.getId()) {
//                        case R.id.attn_phone:
                        case R.id.call_btn_num1:
                        case R.id.call_btn_num4:
                        case R.id.call_btn_num7:
//                        case R.id.attn_xin:
                            ((HomeActivity) getActivity()).home_call_rl.requestFocus();
                            return true;

                    }
                }else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                    switch (v.getId()){
                        case R.id.call_openforuse:
                            call_edit_num.requestFocus();
                            return true;
                    }
                }else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    switch (v.getId()) {
//                        case R.id.attn_jin:
//                            three.requestFocus();
//                            back true;
//                        case R.id.attn_0:
//                            two.requestFocus();
//                            back true;
//                        case R.id.attn_xin:
//                            one.requestFocus();
//                            back true;
//                        case R.id.home_call_mobile:
//                            home_call.requestFocus();
//                            back true;
                    }
                }else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
                    switch (v.getId()) {
//                        case R.id.attn_1:
//                            Number.requestFocus();
//                            back true;
                    }
                }else if(keyCode == KeyEvent.KEYCODE_DEL || keyCode == 1185){
                    String  PhoneNumber = call_edit_num.getText().toString().trim();
                    DelNumber(PhoneNumber);
                    return false;
                }
            }

            return false;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        try {
            if (mReceiver!=null){
                getActivity().unregisterReceiver(mReceiver);
            }
            if (!isDetached()){
                if (null!=dialog&& dialog.isShow()){
                    dialog.cancel();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
