package com.vunke.videochat.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.vunke.videochat.R;

import java.io.IOException;

/**
 * Created by zhuxi on 2020/9/3.
 */

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
    private Camera mCamera;
    private SurfaceView test_surface;
    private SurfaceHolder holder;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        test_surface = findViewById(R.id.test_surface);
        holder=test_surface.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                try {
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });

        initCamera();
    }
    @Nullable
    private Camera.CameraInfo mFrontCameraInfo = null;
    private int mFrontCameraId = -1;

    @Nullable
    private Camera.CameraInfo mBackCameraInfo = null;
    private int mBackCameraId = -1;
    private void initCamera() {
        try {
            int numberOfCameras = Camera.getNumberOfCameras();
            for (int cameraId = 0; cameraId < numberOfCameras; cameraId++) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                Camera.getCameraInfo(cameraId, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    // 后置摄像头信息
                    mBackCameraId = cameraId;
                    mBackCameraInfo = cameraInfo;
                } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                    // 前置摄像头信息
                    mFrontCameraId = cameraId;
                    mFrontCameraInfo = cameraInfo;
                }
            }
            openCamera();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 开启指定摄像头
     */
    private void openCamera() {
        if (mCamera != null) {
            throw new RuntimeException("相机已经被开启，无法同时开启多个相机实例！");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            if (mFrontCameraId!=-1) {
                // 优先开启前置摄像头
                mCamera = Camera.open(mFrontCameraId);
            } else if (mBackCameraId!=-1) {
                // 没有前置，就尝试开启后置摄像头
                mCamera = Camera.open(mBackCameraId);
            } else {
                throw new RuntimeException("没有任何相机可以开启！");
            }
        }
    }

}
