package com.vunke.videochat.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Button;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuxi on 2020/4/13.
 */

public class Utils {
    private static final String TAG = "Utils";
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
    public static void stopClick(final Button button, final long stopTime) {
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

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     * @param context
     * @return versionName 版本名字
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            String pkName = context.getPackageName();
            versionName = context.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
        return versionName;
    }
    /**
     * @param context
     * @return versionCode 版本号
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            String pkName = context.getPackageName();
            versionCode = context.getPackageManager()
                    .getPackageInfo(pkName, 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return versionCode;
    }

}
