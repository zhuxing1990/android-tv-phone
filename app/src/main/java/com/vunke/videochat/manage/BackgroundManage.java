package com.vunke.videochat.manage;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.vunke.videochat.R;
import com.vunke.videochat.tools.SPUtils;

/**
 * Created by zhuxi on 2020/3/1.
 */

public class BackgroundManage {
    private static String background_key = "background_key";
    public static String TYPE_ONE = "1";
    public static String TYPE_TWO ="2";
    public static String TPYE_THREE = "3";
    public static String TYPE_FOUR = "4";
    public static void updateBackground(Context context, String type){
        if (TextUtils.isEmpty(type)){
            SPUtils.putString(context, background_key,TYPE_ONE);
        }else if (type.equals(TYPE_ONE)) {
            SPUtils.putString(context, background_key,TYPE_ONE);
        } else if (type.equals(TYPE_TWO)) {
            SPUtils.putString(context, background_key,TYPE_TWO);
        } else if (type.equals(TPYE_THREE)) {
            SPUtils.putString(context, background_key,TPYE_THREE);
        } else if (type.equals(TYPE_FOUR)) {
            SPUtils.putString(context, background_key,TYPE_FOUR);
        }
    }
    public static int getBackground(Context context){
        int back = R.mipmap.bg1;
        String type = SPUtils.getString(context,background_key,"");
        if (TextUtils.isEmpty(type)){
            back = R.mipmap.bg1;
        }else if (type.equals(TYPE_ONE)) {
            back = R.mipmap.bg1;
        } else if (type.equals(TYPE_TWO)) {
            back = R.mipmap.bg2;
        } else if (type.equals(TPYE_THREE)) {
            back = R.mipmap.bg3;
        } else if (type.equals(TYPE_FOUR)) {
            back = R.mipmap.bg4;
        }
        return back;
    }
    public static void setBackground(Context context, View view){
        int imgae = getBackground(context);
        view.setBackgroundResource(imgae);
    }
}
