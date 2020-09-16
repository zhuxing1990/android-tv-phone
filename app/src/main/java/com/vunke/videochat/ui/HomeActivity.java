package com.vunke.videochat.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vunke.videochat.R;
import com.vunke.videochat.base.HomeFragmentFactory;
import com.vunke.videochat.fragment.AttnFragment;
import com.vunke.videochat.fragment.CallFragment;
import com.vunke.videochat.fragment.ContactsFragment;
import com.vunke.videochat.manage.BackgroundManage;
import com.vunke.videochat.tools.LinphoneMiniUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuxi on 2020/2/27.
 */

public class HomeActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, View.OnClickListener,View.OnFocusChangeListener
//        ,View.OnKeyListener
{
    private static final String TAG = "HomeActivity";
//    public Button home_Call,home_Contacts,home_Attn;
    public RelativeLayout home_call_rl,home_contacts_rl,home_atth_rl,home_bg_rl;
    private List<Fragment> fragments;
    /**
     * 拨号界面
     */
    private CallFragment fragment1;
    /**
     * 联系人界面
     */
    private ContactsFragment fragment2;

    /**
     * 最近联系界面
     */
    private AttnFragment fragment3;
    private FrameLayout home_frame_lr;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        LinphoneMiniUtils.initLinphoneService(this);
        View rootView = findViewById(android.R.id.content);
        BackgroundManage.setBackground(this,rootView);
        initView();
        setFragment(0);
    }


    private void initFragment() {
        fragment1 = (CallFragment) HomeFragmentFactory.createFragment(0);
        fragment2 = (ContactsFragment) HomeFragmentFactory
                .createFragment(1);
        fragment3 = (AttnFragment) HomeFragmentFactory.createFragment(2);
    }
    private void setFragment(int index) {
        FragmentTransaction beginTransaction = getFragmentManager()
                .beginTransaction();
        if (fragments == null) {
            fragments = new ArrayList<>();
            initFragment();
            fragments.add(fragment1);
            fragments.add(fragment2);
            fragments.add(fragment3);
            for (int i = 0; i < fragments.size(); i++) {
                if (!fragments.get(i).isAdded()) {
                    beginTransaction.add(R.id.home_frame_lr, fragments.get(i),"f" + i);
                }
            }
        }

        for (int i = 0; i < fragments.size(); i++) {
            if (i == index) {
                Fragment fragment = fragments.get(i);
                if (fragment != null && fragment.isHidden()) {
                    beginTransaction.show(fragments.get(i));
                }
            } else {
                Fragment fragment = fragments.get(i);
                if (fragment != null && !fragment.isHidden()) {
                    beginTransaction.hide(fragments.get(i));
                }
            }
        }

        try {
            if (!isDes) {
                beginTransaction.commitAllowingStateLoss();
            }

        } catch (Exception e) {
        }

    }


    private boolean isDes;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
         super.onSaveInstanceState(outState);
        isDes = true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        isDes = false;
        Log.e("HomeActivity", "only--onResume");
        Log.i(TAG, "onResume: ");
        View rootView = findViewById(android.R.id.content);
        BackgroundManage.setBackground(this,rootView);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        home_frame_lr= findViewById(R.id.home_frame_lr);
        home_call_rl=findViewById(R.id.home_call_rl);
        home_contacts_rl=findViewById(R.id.home_contacts_rl);
        home_atth_rl=findViewById(R.id.home_atth_rl);
        home_bg_rl=findViewById(R.id.home_bg_rl);
        home_call_rl.setOnClickListener(this);
        home_contacts_rl.setOnClickListener(this);
        home_atth_rl.setOnClickListener(this);
        home_call_rl.setOnFocusChangeListener(this);
        home_contacts_rl.setOnFocusChangeListener(this);
        home_atth_rl.setOnFocusChangeListener(this);
        home_bg_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,BackgroundActivity.class);
                startActivity(intent);
            }
        });

//        home_Call.setOnKeyListener(this);
//        home_Contacts.setOnKeyListener(this);
//        home_Attn.setOnKeyListener(this);
    }

//    @Override
//    public boolean onKey(View v, int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            switch (v.getId()){
//                case R.id.home_Call:
//                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
////                            home_Contacts.requestFocus();
//                            back true;
//                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
//
//                    }else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
////                            home_Attn.requestFocus();
//                            back true;
//                    }
//                    break;
//                case R.id.home_Contacts:
//                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
////                            home_Attn.requestFocus();
//                            back true;
//                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
//
//                    }else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
////                            home_Call.requestFocus();
//                            back true;
//                    }
//                    break;
//                case R.id.home_Attn:
//                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
////                        home_Call.requestFocus();
//                        back true;
//                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
//
//                    }else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
////                        home_Contacts.requestFocus();
//                        back true;
//                    }
//                    break;
//            }
//        }
//        back false;
//    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    private long exitTime = 0;
    public void exit() {
        if (System.currentTimeMillis() - exitTime > 2000L) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
            return;
        }
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_call_rl:
                setFragment(0);
                break;
            case R.id.home_contacts_rl:
                setFragment(1);
                break;
            case R.id.home_atth_rl:
                setFragment(2);
                break;
        }
    }

    private void setButtonSelector() {
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus){
            switch (v.getId()){
                case R.id.home_call_rl:

                    break;
                case R.id.home_contacts_rl:

                    break;
                case R.id.home_atth_rl:

                    break;
            }
        }else{
            switch (v.getId()){
                case R.id.home_call_rl:

                    break;
                case R.id.home_contacts_rl:

                    break;
                case R.id.home_atth_rl:

                    break;
            }
        }
    }
}
