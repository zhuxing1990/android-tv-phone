package com.vunke.videochat.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

/**
 * Created by zhuxi on 2020/4/13.
 */

public class Utils {

    /**
     * 使用系统工具类判断是否是今天 是今天就显示发送的小时分钟 不是今天就显示发送的那一天
     * */
    public static String getDate(Context context, long when) {
        String date = null;
        if (DateUtils.isToday(when)) {
            date = DateFormat.getTimeFormat(context).format(when);
        } else {
            date = DateFormat.getDateFormat(context).format(when);
        }
        return date;
    }


    public static boolean isCameraCanUse() {

        boolean canUse = false;
        Camera mCamera = null;

        try {
            mCamera = Camera.open(0);
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }

        if (mCamera != null) {
            mCamera.release();
            canUse = true;
        }

        return canUse;
    }
    /**
     * 判断当前网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isNetConnected(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {

                NetworkInfo info = connectivity.getActiveNetworkInfo();

                if (info != null) {
                    boolean istrue = false;
                    istrue = istrue ? info.isConnected() : info.isAvailable();
                    return istrue;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    public static PackageInfo getPackageInfo(Context context, String packageName){
        PackageInfo packageInfo = null;
        try {
            packageInfo   = context.getPackageManager().getPackageInfo(packageName, 0);
            if (packageInfo!=null){
                return packageInfo;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return packageInfo;
    }
}
