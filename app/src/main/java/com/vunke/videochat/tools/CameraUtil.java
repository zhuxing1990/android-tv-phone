package com.vunke.videochat.tools;

import android.util.Log;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.vunke.videochat.service.LinphoneMiniManager;

import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;

/**
 * Created by zhuxi on 2020/8/27.
 */

public class CameraUtil {
    private static final String TAG = "CameraUtil";
    public static void initCamera(LinphoneMiniManager instance) {
        Log.i(TAG, "initCamera: ");
       try {
           AndroidCameraConfiguration.AndroidCamera[] cameras = AndroidCameraConfiguration.retrieveCameras();
           if (cameras!=null&&cameras.length!=0){
               for (AndroidCameraConfiguration.AndroidCamera androidCamera : cameras) {
                   Log.i(TAG, "initCamera: androidCamera:"+androidCamera.id);
                   Log.i(TAG, "initCamera: set video device :"+androidCamera.id);
                   instance.getLC().setVideoDevice(androidCamera.id);
               }
           }else{
               Log.i(TAG, "initCamera: get cameras is null");
           }
       }catch (Exception  e){
           e.printStackTrace();
           Log.i(TAG, "initCamera: failed");
       }
    }

    public static RelativeLayout.LayoutParams getSmallLayoutParams() {
        RelativeLayout.LayoutParams lpSmall = new RelativeLayout.LayoutParams(304, 220);
        lpSmall.topMargin = 0;
        lpSmall.leftMargin = 0;
        return lpSmall;
    }

    public static RelativeLayout.LayoutParams getNormalLayoutParams() {
        RelativeLayout.LayoutParams lpNormal = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lpNormal.topMargin = 0;
        lpNormal.leftMargin = 0;
        return lpNormal;
    }

    public static void changeScreen(boolean mIsChangeScreen, RelativeLayout locatRL, RelativeLayout RenderRL, SurfaceView mSurfaceViewSmall,SurfaceView mSurfaceView) {
        RelativeLayout.LayoutParams lpSmall = getSmallLayoutParams();
        RelativeLayout.LayoutParams lpNormal = getNormalLayoutParams();
        if (mIsChangeScreen) {
            Log.i(TAG, "changeScreen: 远程切小屏,本地切大屏");
            locatRL.removeView(mSurfaceViewSmall);
            RenderRL.removeView(mSurfaceView);
            mSurfaceView.setLayoutParams(lpSmall);
            locatRL.addView(mSurfaceView);
            mSurfaceViewSmall.setLayoutParams(lpNormal);
            RenderRL.addView(mSurfaceViewSmall);
//            mSurfaceView.setZOrderOnTop(true);
            mSurfaceView.setZOrderMediaOverlay(true);
        } else {
            Log.i(TAG, "changeScreen: 远程切大屏,本地切小屏");
            RenderRL.removeView(mSurfaceViewSmall);
            locatRL.removeView(mSurfaceView);
            mSurfaceView.setLayoutParams(lpNormal);
            RenderRL.addView(mSurfaceView);
            mSurfaceViewSmall.setLayoutParams(lpSmall);
            locatRL.addView(mSurfaceViewSmall);
//            mSurfaceViewSmall.setZOrderOnTop(true);
            mSurfaceView.setZOrderOnTop(false);
            mSurfaceViewSmall.setZOrderOnTop(true);
            mSurfaceViewSmall.setZOrderMediaOverlay(true);
        }
    }

}
