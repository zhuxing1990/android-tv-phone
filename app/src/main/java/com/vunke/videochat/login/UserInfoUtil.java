package com.vunke.videochat.login;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;

import com.vunke.videochat.tools.Utils;

/**
 * Created by zhuxi on 2020/2/27.
 */

public class UserInfoUtil {
//    private Uri localUri = Uri.parse("content://com.starcor.mango.hndx.provider/deviceinfo");
    /**
     * 认证数据库
     */
    private static Uri authUri = Uri.parse("content://com.huawei.hunandx.auth.provider/authinfo");
    private static Uri mangguoUir = Uri.parse("content://com.hunantv.operator.mango.hndxiptv/userinfo");
    private static UserInfoUtil userInfoUtil;
    private static String userId = "";
    private static String userToken = "";

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static UserInfoUtil getInstance(Context context) {
        if (userInfoUtil == null) {
            synchronized (UserInfoUtil.class) {
                userInfoUtil = new UserInfoUtil(context);
            }
        }
        queryDeviceInfo(context);
        return userInfoUtil;
    }

    private UserInfoUtil(Context context) {
        queryDeviceInfo(context);
    }

    /**
     * 通过查询数据库获取业务帐号
     *
     * @param context 上下文
     */
    public static void queryDeviceInfo(Context context) {
        PackageInfo packageInfo = Utils.getPackageInfo(context, "com.vunke.auth");
        Cursor mCursor;
        if (packageInfo != null) {
            mCursor = context.getContentResolver().query(authUri,
                    null, null, null, null);
        } else {
            mCursor = context.getContentResolver().query(mangguoUir,
                    null, null, null, null);
        }
        try {
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    String name = mCursor.getString(mCursor.getColumnIndex("name"));
                    if ("user_id".equals(name)) {
                        userId = mCursor.getString(mCursor.getColumnIndex("value"));
                    } else if ("user_token".equals(name)) {
                        userToken = mCursor.getString(mCursor.getColumnIndex("value"));
                    }
                }
            }
        } finally {
            if (mCursor != null)
                mCursor.close();
        }
    }

}
