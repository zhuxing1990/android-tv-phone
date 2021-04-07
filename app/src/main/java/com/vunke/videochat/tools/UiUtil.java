package com.vunke.videochat.tools;

import android.graphics.Bitmap;

import com.vunke.videochat.base.BaseConfig;

/**
 * Created by zhuxi on 2020/12/2.
 */

public class UiUtil {

    public static Bitmap getQrcodeImage(String userId){

        Bitmap bitmap = null;
        try {
            bitmap=QRCodeUtil.createQRCodeBitmap(BaseConfig.ADD_CONTACTS_QRCODE_URL + "?userId=" + userId, 400, 400);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }
}
