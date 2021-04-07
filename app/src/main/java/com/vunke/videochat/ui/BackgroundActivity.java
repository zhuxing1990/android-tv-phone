package com.vunke.videochat.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.vunke.videochat.R;
import com.vunke.videochat.manage.BackgroundManage;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuxi on 2020/3/1.
 */

public class BackgroundActivity extends AppCompatActivity{
    private static final String TAG = "BackgroundActivity";
    private int[] imgArr = {R.mipmap.bg0,R.mipmap.bg1, R.mipmap.bg2, R.mipmap.bg3, R.mipmap.bg4};
    private Button bg_left_bt,bg_right_bt,bg_confirm;
    private ImageView bg_content,bg_img_left,bg_img_right;
    //图片下标序号
    private int count = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        View rootView = findViewById(android.R.id.content);
//        BackgroundManage.setBackground(this,rootView);
    }

    private void initView() {
        bg_content = findViewById(R.id.bg_content);
        bg_left_bt = findViewById(R.id.bg_left_bt);
        bg_right_bt = findViewById(R.id.bg_right_bt);
        bg_img_left = findViewById(R.id.bg_img_left);
        bg_img_left.setVisibility(View.INVISIBLE);
        bg_img_right = findViewById(R.id.bg_img_right);
        bg_confirm = findViewById(R.id.bg_confirm);
        bg_content.setImageResource(imgArr[count]);
        bg_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (count){
                    case 0:
                        BackgroundManage.updateBackground(BackgroundActivity.this,"0");
                        break;
                    case 1:
                        BackgroundManage.updateBackground(BackgroundActivity.this,"1");
                        break;
                    case 2:
                        BackgroundManage.updateBackground(BackgroundActivity.this,"2");
                        break;
                    case 3:
                        BackgroundManage.updateBackground(BackgroundActivity.this,"3");
                        break;
                    case 4:
                        BackgroundManage.updateBackground(BackgroundActivity.this,"4");
                        break;
                }
                Toast.makeText(BackgroundActivity.this,"设置成功",Toast.LENGTH_SHORT).show();
                Observable.interval(2, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DisposableObserver<Long>() {
                            @Override
                            public void onNext(Long aLong) {
                                finish();
                                onComplete();
                            }

                            @Override
                            public void onError(Throwable e) {
                                dispose();
                                finish();
                            }

                            @Override
                            public void onComplete() {
                                dispose();
                            }
                        });
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode ==KeyEvent.KEYCODE_DPAD_RIGHT){
            if (count<imgArr.length-1){
                count++;
                Log.i(TAG, "onKeyDown: RIGHT："+count);
                bg_content.setImageResource(imgArr[count]);
                if (count == imgArr.length-1){
                    hintRight();
                }
                if (count>0){
                    showLeft();
                }
                    return false;
            }else{
                showLeft();
                hintRight();
            }
        }else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
            if (count>0){
                count --;
                Log.i(TAG, "onKeyDown: LEFT："+count);
                bg_content.setImageResource(imgArr[count]);
                if (count==0){
                    hintLeft();
                }
                if (count<imgArr.length-1){
                    showRight();
                }
                return false;
            }else{
                showRight();
                hintLeft();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showRight() {
        if (bg_img_right.getVisibility()== View.INVISIBLE){
            bg_img_right.setVisibility(View.VISIBLE);
        }
    }

    private void hintLeft() {
        if (bg_img_left.getVisibility()== View.VISIBLE){
            bg_img_left.setVisibility(View.INVISIBLE);
        }
    }

    private void showLeft() {
        if (bg_img_left.getVisibility()== View.INVISIBLE){
            bg_img_left.setVisibility(View.VISIBLE);
        }
    }

    private void hintRight() {
        if (bg_img_right.getVisibility()== View.VISIBLE){
            bg_img_right.setVisibility(View.INVISIBLE);
        }
    }

}
